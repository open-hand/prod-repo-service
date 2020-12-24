package org.hrds.rdupm.nexus.infra.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.metadata.MethodType;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.mapper.NexusLogMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.util.NexusUtils;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2020/12/9
 */
@Component
public class NexusFilter implements Filter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String LOG_TEMPLATE = "%s(%s)下载了%s包【%s】";

    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Autowired
    private NexusProxyConfigProperties nexusProxyConfigProperties;

    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    @Autowired
    private NexusLogMapper nexusLogMapper;

    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 对下载jar包的请求进行过滤拦截操作
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
        String servletUri = NexusUtils.getServletUri(httpServletRequest, nexusProxyConfigProperties);
        LOGGER.info("The uri of the request servlet :{}", servletUri);

        //2.提取拉取制品包的地址和包的名字，仓库的名字 解析用户名和密码 Basic MjUzMjg6V2FuZz==
        if ((StringUtils.endsWithIgnoreCase(servletUri, ".jar") || StringUtils.endsWithIgnoreCase(servletUri, ".tgz"))
                && !StringUtils.isEmpty(httpServletRequest.getHeader("authorization"))
                && org.apache.commons.lang3.StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "get")) {
            //仓库名字在整个nexus中唯一存在
            NexusRepository repository = null;
            String repositoryName = getRepositoryName(servletUri);
            LOGGER.info("The name of the repository is : {}", repositoryName);
            if (!StringUtils.isEmpty(repositoryName)) {
                NexusRepository nexusRepository = new NexusRepository();
                nexusRepository.setNeRepositoryName(repositoryName);
                repository = nexusRepositoryMapper.selectOne(nexusRepository);
            }
            if (!Objects.isNull(repository)) {
                String packageName = getPackageName(servletUri);
                UserDTO userDTO = getUserDTO(httpServletRequest);
                //3.记录用户在哪个仓库下下载了哪个jar包的记录
                NexusLog nexusLog = generateLog(repository, userDTO, packageName, servletUri);
                nexusLogMapper.insert(nexusLog);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private NexusLog generateLog(NexusRepository repository, UserDTO userDTO, String packageName, String servletUri) {
        NexusLog nexusLog = new NexusLog();
        nexusLog.setOperatorId(userDTO.getId());
        nexusLog.setOperateType(NexusConstants.LogOperateType.AUTH_PULL);
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
        nexusLog.setContent(String.format(LOG_TEMPLATE, userDTO.getRealName(), userDTO.getLoginName(), repo, packageName));
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
        if (Objects.isNull(userDTO)) {
            throw new CommonException("user " + user[0] + " not exist");
        }
        return userDTO;
    }
}
