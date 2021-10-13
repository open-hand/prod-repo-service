package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.common.api.vo.UserNexusInfo;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.api.vo.NexusServerConfigVO;
import org.hrds.rdupm.nexus.app.service.NexusApiService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.entity.NexusProjectService;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusProjectServiceRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.mapper.NexusLogMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 制品库_nexus服务信息配置表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Service
public class NexusServerConfigServiceImpl implements NexusServerConfigService {


    private final String LOG_PULL_TEMPLATE = "%s(%s)下载了%s包【%s】";
    private final String LOG_PUSH_TEMPLATE = "%s(%s)上传了%s包【%s】";

    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;
    @Autowired
    private ProdUserRepository prodUserRepository;
    @Autowired
    private NexusProjectServiceRepository nexusProjectServiceRepository;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    @Lazy
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusApiService nexusApiService;
    @Autowired
    private C7nBaseService c7nBaseService;
    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;
    @Autowired
    private NexusLogMapper nexusLogMapper;

    @Override
    public NexusServerConfig setNexusInfo(NexusClient nexusClient, Long projectId) {
        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryEnableServiceConfig(projectId);
        if (nexusServerConfig == null) {
            throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
        }
        NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                nexusServerConfig.getUserName(),
                DESEncryptUtil.decode(nexusServerConfig.getPassword()));
        nexusClient.setNexusServerInfo(nexusServer);
        return nexusServerConfig;
    }

    @Override
    public NexusServerConfig setNexusDefaultInfo(NexusClient nexusClient) {
        NexusServerConfig queryConfig = new NexusServerConfig();
        queryConfig.setDefaultFlag(BaseConstants.Flag.YES);
        NexusServerConfig defaultInfo = nexusServerConfigRepository.selectOne(queryConfig);
        NexusServer nexusServer = new NexusServer(defaultInfo.getServerUrl(),
                defaultInfo.getUserName(),
                DESEncryptUtil.decode(defaultInfo.getPassword()));
        nexusClient.setNexusServerInfo(nexusServer);
        return defaultInfo;
    }

    @Override
    public NexusServerConfig setNexusInfoByConfigId(NexusClient nexusClient, Long configId) {
        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectByPrimaryKey(configId);
        if (nexusServerConfig == null) {
            throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
        }
        NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                nexusServerConfig.getUserName(),
                DESEncryptUtil.decode(nexusServerConfig.getPassword()));
        nexusClient.setNexusServerInfo(nexusServer);
        return nexusServerConfig;
    }

    @Override
    public NexusServerConfig setNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId) {
        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryServiceConfigByRepositoryId(repositoryId);
        if (nexusServerConfig == null) {
            throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
        }
        NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                nexusServerConfig.getUserName(),
                DESEncryptUtil.decode(nexusServerConfig.getPassword()));
        nexusClient.setNexusServerInfo(nexusServer);
        return nexusServerConfig;
    }

    @Override
    public NexusServer setCurrentNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID, userId).stream().findFirst().orElse(null);
        if (prodUser == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        String password = null;
        if (prodUser.getPwdUpdateFlag() == 1) {
            password = DESEncryptUtil.decode(prodUser.getPassword());
        } else {
            password = prodUser.getPassword();
        }

        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryServiceConfigByRepositoryId(repositoryId);
        if (nexusServerConfig == null) {
            throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
        }
        NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                prodUser.getLoginName(),
                password);
        nexusClient.setNexusServerInfo(nexusServer);
        return nexusServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NexusServerConfig createServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {

        // 参数校验
        nexusServerConfig.validParam(nexusClient);
        nexusServerConfig.setDefaultFlag(BaseConstants.Flag.NO);
        nexusServerConfig.setTenantId(organizationId);
        nexusServerConfig.setPassword(DESEncryptUtil.encode(nexusServerConfig.getPassword()));
        nexusServerConfigRepository.insertSelective(nexusServerConfig);

        NexusProjectService nexusProjectService = new NexusProjectService();
        nexusProjectService.setConfigId(nexusServerConfig.getConfigId());
        nexusProjectService.setOrganizationId(organizationId);
        nexusProjectService.setProjectId(projectId);
        nexusProjectService.setEnableFlag(BaseConstants.Flag.NO);
        nexusProjectServiceRepository.insertSelective(nexusProjectService);

        nexusServerConfig.setProjectServiceId(nexusProjectService.getProjectServiceId());
        nexusServerConfig.setProjectId(nexusProjectService.getProjectId());

        // 初始化数据
        nexusClient.initData();

        nexusClient.removeNexusServerInfo();
        return nexusServerConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NexusServerConfig updateServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {

        NexusServerConfig existConfig = nexusServerConfigRepository.queryServiceConfig(nexusServerConfig.getConfigId(), projectId);
        if (existConfig == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        String newPassword = null;
        if (!nexusServerConfig.getPassword().equals(existConfig.getPassword())) {
            newPassword = nexusServerConfig.getPassword();
        } else {
            newPassword = DESEncryptUtil.decode(existConfig.getPassword());
        }

        nexusServerConfig.setPassword(newPassword);

        nexusServerConfig.validParam(nexusClient);

        // 只更新，密码
        String encryptPassword = DESEncryptUtil.encode(newPassword);

        nexusServerConfig.setPassword(encryptPassword);
        nexusServerConfigRepository.updateOptional(nexusServerConfig, NexusServerConfig.FIELD_PSW,
                NexusServerConfig.FIELD_ENABLE_ANONYMOUS_FLAG, NexusServerConfig.FIELD_ANONYMOUS, NexusServerConfig.FIELD_ANONYMOUS_ROLE);

        if (nexusServerConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
            // 开启匿名访问
            List<String> addPrivileges = new ArrayList<>();
            List<String> deletePrivileges = new ArrayList<>();
            Condition condition = Condition.builder(NexusRepository.class).where(
                    Sqls.custom().andEqualTo(NexusRepository.FIELD_CONFIG_ID, existConfig.getConfigId())).build();
            List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(condition);
            nexusRepositoryList.forEach(nexusRepository -> {
                NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
                if (nexusServerRepository != null) {
                    List<String> privilegeList = NexusServerRole.getAnonymousPrivileges(nexusRepository.getNeRepositoryName(), nexusServerRepository.getFormat());
                    if (nexusRepository.getAllowAnonymous().equals(BaseConstants.Flag.YES)) {
                        addPrivileges.addAll(privilegeList);
                    } else {
                        deletePrivileges.addAll(privilegeList);
                    }
                }
            });
            nexusApiService.updateRole(nexusServerConfig.getAnonymousRole(), addPrivileges, deletePrivileges);
        }

        // 初始化数据
        nexusClient.initData();

        nexusClient.removeNexusServerInfo();
        return nexusServerConfig;
    }

    @Override
    public List<NexusServerConfig> listServerConfig(Long organizationId, Long projectId) {
        // 默认nexus服务信息查询
        NexusServerConfig defaultQuery = new NexusServerConfig();
        defaultQuery.setDefaultFlag(BaseConstants.Flag.YES);
        NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(defaultQuery);
        if (defaultConfig == null) {
            throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
        }
        defaultConfig.setProjectId(projectId);

        // 项目下，自定义的nexus服务信息
        List<NexusServerConfig> nexusServerConfigList = nexusServerConfigRepository.queryList(organizationId, projectId);

        Integer enableFlag = nexusServerConfigList.stream().map(NexusServerConfig::getEnableFlag).filter(enableFlagValue -> enableFlagValue.equals(BaseConstants.Flag.YES)).findFirst().orElse(null);
        if (enableFlag == null) {
            // 没有启用的自定义的nexus服务, 设置默认的为启用
            defaultConfig.setEnableFlag(BaseConstants.Flag.YES);
        } else {
            defaultConfig.setEnableFlag(BaseConstants.Flag.NO);
        }
        List<NexusServerConfig> result = new ArrayList<>();
        result.add(defaultConfig);
        result.addAll(nexusServerConfigList);

        //result = result.stream().peek(nexusServer -> nexusServer.setPassword(null)).collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableProjectServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {
        NexusServerConfig existConfig = nexusServerConfigRepository.selectByPrimaryKey(nexusServerConfig.getConfigId());
        if (existConfig == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        if (existConfig.getDefaultFlag().equals(BaseConstants.Flag.YES)) {
            // 启用默认的服务
            // 直接更新该项目下所有服务为不启用
            nexusProjectServiceRepository.disAbleByProjectId(projectId, DetailsHelper.getUserDetails().getUserId());
        } else {
            // 启用自定义的服务
            // 将项目下的所有nexus服务都设置为不启用，启用该服务
            NexusServerConfig existConfigProject = nexusServerConfigRepository.queryServiceConfig(nexusServerConfig.getConfigId(), projectId);
            if (existConfigProject == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }

            NexusProjectService query = new NexusProjectService();
            query.setProjectServiceId(existConfigProject.getProjectServiceId());
            query.setProjectId(projectId);
            query.setOrganizationId(organizationId);
            NexusProjectService nexusProjectService = nexusProjectServiceRepository.selectOne(query);
            if (nexusProjectService == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }

            // 所有设为不启用
            nexusProjectServiceRepository.disAbleByProjectId(projectId, DetailsHelper.getUserDetails().getUserId());
            // 启用
            nexusProjectService.setEnableFlag(BaseConstants.Flag.YES);
            nexusProjectServiceRepository.updateOptional(nexusProjectService, NexusProjectService.FIELD_ENABLE_FLAG);
        }
    }

    @Override
    public NexusServerConfigVO queryNexusServiceConfigById(Long nexusServiceConfigId) {
        NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectByPrimaryKey(nexusServiceConfigId);
        if (nexusServerConfig == null) {
            return null;
        } else {
            //去除敏感字段
            NexusServerConfigVO nexusServerConfigVO = new NexusServerConfigVO();
            BeanUtils.copyProperties(nexusServerConfig, nexusServerConfigVO);
            return nexusServerConfigVO;
        }
    }

    @Override
    public void auditNexusLog(UserNexusInfo userNexusInfo) {
        UserDTO userDTO = c7nBaseService.queryByLoginName(userNexusInfo.getUserName());
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setNeRepositoryName(userNexusInfo.getRepositoryName());
        nexusRepository.setConfigId(userNexusInfo.getConfigId());
        NexusRepository repository = nexusRepositoryMapper.selectOne(nexusRepository);
        userNexusInfo.getRepositoryName();
        NexusLog nexusLog = generateLog(userDTO, repository, userNexusInfo);
        nexusLogMapper.insert(nexusLog);
    }


    private NexusLog generateLog(UserDTO userDTO, NexusRepository nexusRepository, UserNexusInfo userNexusInfo) {
        NexusLog nexusLog = new NexusLog();
        nexusLog.setOperatorId(userDTO.getId());
        nexusLog.setOperateType(userNexusInfo.getRepoType());
        nexusLog.setProjectId(nexusRepository.getProjectId());
        nexusLog.setOrganizationId(nexusRepository.getOrganizationId());
        nexusLog.setRepositoryId(nexusRepository.getRepositoryId());
        String repo = userNexusInfo.getRepoType();

        String content = "";
        if (NexusConstants.LogOperateType.AUTH_PULL.equals(userNexusInfo.getOperateType())) {
            content = String.format(LOG_PULL_TEMPLATE, userDTO.getRealName(), userDTO.getLoginName(), repo, userNexusInfo.getPackageName());
        } else if (NexusConstants.LogOperateType.AUTH_PUSH.equals(userNexusInfo.getOperateType())) {
            content = String.format(LOG_PUSH_TEMPLATE, userDTO.getRealName(), userDTO.getLoginName(), repo, userNexusInfo.getPackageName());
        }
        nexusLog.setContent(content);
        nexusLog.setOperateTime(new Date());
        return nexusLog;
    }
}
