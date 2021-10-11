package org.hrds.rdupm.nexus.infra.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusProxyConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusLogMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.util.NexusUtils;
import org.hzero.core.base.BaseConstants;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2020/12/9
 */
@Component
public class NexusFilter implements Filter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String LOG_PULL_TEMPLATE = "%s(%s)下载了%s包【%s】";
    private final String LOG_PUSH_TEMPLATE = "%s(%s)上传了%s包【%s】";

    protected static final String ATTR_TARGET_URI =
            ProxyServlet.class.getSimpleName() + ".targetUri";
    protected static final String ATTR_TARGET_HOST =
            ProxyServlet.class.getSimpleName() + ".targetHost";

    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Value("${nexus.choerodon.capacity.limit.base: 2}")
    private Integer nexusBaseCapacityLimit;

    /**
     * 企业版 一个项目限制5G
     */
    @Value("${nexus.choerodon.capacity.limit.business: 5}")
    private Integer nexusBusinessCapacityLimit;

    @Autowired
    private NexusProxyConfigProperties nexusProxyConfigProperties;

    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    @Autowired
    private NexusLogMapper nexusLogMapper;

    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;
    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;

    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;
    @Autowired
    private C7nBaseService c7nBaseService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 对上传、下载jar包的请求进行过滤拦截操作
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        //1.获得请求的地址 /v1/nexus/proxy/repository/lilly-release/wx/test/1.0/test-1.0.jar 去除前缀 /repository/lilly-release/wx/test/1.0/test-1.0.jar
        String uriWithConfig = NexusUtils.getServletUri(httpServletRequest, nexusProxyConfigProperties);

        String configIdStr = StringUtils.substringBefore(StringUtils.substringAfter(uriWithConfig, BaseConstants.Symbol.SLASH), BaseConstants.Symbol.SLASH);
        String servletUri = uriWithConfig.replace(BaseConstants.Symbol.SLASH + configIdStr, "");
        LOGGER.info("The uri of the request servlet :{}", servletUri);
        Long configId = Long.parseLong(configIdStr);
        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectByPrimaryKey(configId);

        //http://nexus.c7n.devops.hand-china.com/repository/wx-java-snapshot/io/choerodon/springboot/0.0.1-SNAPSHOT/springboot-0.0.1-20210412.072018-1.jar
        String pipelineDownLoad = httpServletRequest.getParameter("pipelineDownLoad");
        //如果是流水线里面下载jar包的操作，那么
        if (StringUtils.equalsIgnoreCase(pipelineDownLoad, Boolean.TRUE.toString())) {
            httpServletResponse.sendRedirect(nexusServerConfig.getServerUrl() + servletUri);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }


        //2.提取拉取制品包的地址和包的名字，仓库的名字 解析用户名和密码 Basic MjUzMjg6V2FuZz==
        if ((StringUtils.endsWithIgnoreCase(servletUri, ".jar") || StringUtils.endsWithIgnoreCase(servletUri, ".tgz"))
                && !StringUtils.isEmpty(httpServletRequest.getHeader("authorization"))) {
            //仓库名字在整个nexus中唯一存在
            NexusRepository repository = null;
            String repositoryName = getRepositoryName(servletUri);
            LOGGER.info("The name of the repository is : {}", repositoryName);
            if (!StringUtils.isEmpty(repositoryName)) {
                NexusRepository nexusRepository = new NexusRepository();
                nexusRepository.setNeRepositoryName(repositoryName);
                nexusRepository.setConfigId(configId);
                repository = nexusRepositoryMapper.selectOne(nexusRepository);
            }
            /**
             *  如果是推包 查询当前仓库容量，如果超出限制 则终止
             */
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "put")) {
                NexusAssets record = new NexusAssets();
                record.setRepositoryId(repository.getRepositoryId());
                List<NexusAssets> nexusAssets = nexusAssetsMapper.select(record);
                if (!CollectionUtils.isEmpty(nexusAssets)) {
                    Long totalSize = nexusAssets.stream().map(NexusAssets::getSize).reduce((aLong, aLong2) -> aLong + aLong2).orElseGet(() -> 0L);
                    ExternalTenantVO externalTenantVO = c7nBaseService.queryTenantByIdWithExternalInfo(repository.getOrganizationId());
                    if (Objects.isNull(externalTenantVO)) {
                        throw new CommonException("tenant not exists");
                    }
                    if (externalTenantVO.getRegister()
                            || StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.FREE.name())
                            || StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.STANDARD.name())) {
                        LOGGER.info(">>>>>>>>>>>仓库的容量限制为{}>>>>>>>>>>>>>>>>", HarborUtil.getStorageLimit(nexusBaseCapacityLimit, HarborConstants.GB));
                        LOGGER.info(">>>>>>>>>>>已经使用的仓库的大小为{}>>>>>>>>>>>>>>>>", totalSize);

                        if (totalSize <= HarborUtil.getStorageLimit(nexusBaseCapacityLimit, HarborConstants.GB)) {
                          throw new CommonException("Exceeded repository capacity limit");
                        }
                    }

                    if (StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.SENIOR.name())) {
                        if (totalSize <= HarborUtil.getStorageLimit(nexusBusinessCapacityLimit, HarborConstants.GB)) {
                            throw new CommonException("Exceeded repository capacity limit");
                        }
                    }
                }

            }
            if (!Objects.isNull(repository)) {
                String packageName = getPackageName(servletUri);
                UserDTO userDTO = getUserDTO(httpServletRequest);
                // 直接在流水线下载jar包不需要记录日志，传的用户
                if (!Objects.isNull(userDTO)) {
                    //3.记录用户在哪个仓库下下载了哪个jar包的记录
                    NexusLog nexusLog = new NexusLog();
                    if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "get")) {
                        nexusLog = generateLog(repository, userDTO, packageName, servletUri, NexusConstants.LogOperateType.AUTH_PULL);
                    } else if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "put")) {
                        nexusLog = generateLog(repository, userDTO, packageName, servletUri, NexusConstants.LogOperateType.AUTH_PUSH);
                    }
                    nexusLogMapper.insert(nexusLog);
                }
            }
        }

        // 动态设置代理服务器地址
        httpServletRequest.setAttribute(ATTR_TARGET_URI, nexusServerConfig.getServerUrl());
        httpServletRequest.setAttribute(ATTR_TARGET_URI, nexusServerConfig.getServerUrl());
        httpServletRequest.setAttribute(NexusProxyConstants.CONFIG_SERVER_ID, configId);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private NexusLog generateLog(NexusRepository repository, UserDTO userDTO, String packageName, String servletUri, String operateType) {
        NexusLog nexusLog = new NexusLog();
        nexusLog.setOperatorId(userDTO.getId());
        nexusLog.setOperateType(operateType);
        nexusLog.setProjectId(repository.getProjectId());
        nexusLog.setOrganizationId(repository.getOrganizationId());
        nexusLog.setRepositoryId(repository.getRepositoryId());
        String repo = null;
        if (StringUtils.endsWithIgnoreCase(servletUri, ".jar")) {
            repo = NexusConstants.RepoType.JAR;
        }
        if (StringUtils.endsWithIgnoreCase(servletUri, ".tgz")) {
            repo = NexusConstants.RepoType.NPM;
        }
        String content = "";
        if (NexusConstants.LogOperateType.AUTH_PULL.equals(operateType)) {
            content = String.format(LOG_PULL_TEMPLATE, userDTO.getRealName(), userDTO.getLoginName(), repo, packageName);
        } else if (NexusConstants.LogOperateType.AUTH_PUSH.equals(operateType)) {
            content = String.format(LOG_PUSH_TEMPLATE, userDTO.getRealName(), userDTO.getLoginName(), repo, packageName);
        }
        nexusLog.setContent(content);
        nexusLog.setOperateTime(new Date());
        return nexusLog;
    }


    @Override
    public void destroy() {

    }

    private static String getRepositoryName(String servletUri) {
        //repository/lilly-release/wx/test/1.0/test-1.0.jar  仅允许英文、数字、下划线、中划线、点(.)
        return servletUri.split(BaseConstants.Symbol.SLASH)[2];
    }

    private static String getPackageName(String servletUri) {
        //repository/lilly-release/wx/test/1.0/test-1.0.jar  仅允许英文、数字、下划线、中划线、点(.)
        //repository/@base2/pretty-print-object/-/pretty-print-object-1.0.0.tgz
        return servletUri.substring(servletUri.lastIndexOf(BaseConstants.Symbol.SLASH) + 1);
    }

    private UserDTO getUserDTO(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        // Basic MjUzMjg6V2FuZz==
        String authorization = httpServletRequest.getHeader("authorization");
        String userEncode = authorization.split("Basic")[1];
        // 25328:wx
        String userDecode = new String(decoder.decode(userEncode.trim()), "UTF-8");
        String[] user = userDecode.split(BaseConstants.Symbol.COLON);
        UserDTO userDTO = baseServiceFeignClient.query(user[0]);
//        if (Objects.isNull(userDTO)) {
//            throw new CommonException("user " + user[0] + " not exist");
//        }
        return userDTO;
    }
}
