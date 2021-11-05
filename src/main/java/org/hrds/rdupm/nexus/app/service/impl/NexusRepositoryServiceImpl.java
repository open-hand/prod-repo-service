package org.hrds.rdupm.nexus.app.service.impl;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.core.domain.Page;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.init.config.NexusDefaultInitConfiguration;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.api.vo.NexusRepositoryVO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.eventhandler.payload.NexusRepositoryDeletePayload;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.*;
import org.hrds.rdupm.nexus.domain.repository.*;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusLogMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.omg.CORBA.COMM_FAILURE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus仓库信息表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@Service
public class NexusRepositoryServiceImpl implements NexusRepositoryService, AopProxy<NexusRepositoryService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NexusRepositoryServiceImpl.class);

    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusRoleRepository nexusRoleRepository;
    @Autowired
    private NexusUserRepository nexusUserRepository;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusServerConfigService configService;
    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private NexusPushRepository nexusPushRepository;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private NexusAuthService nexusAuthService;
    @Autowired
    private C7nBaseService c7nBaseService;
    @Autowired
    private NexusProxyConfigProperties nexusProxyConfigProperties;
    @Autowired
    private NexusLogMapper nexusLogMapper;
    @Value("${nexus.filter.mavenRepo:market-repo}")
    private String marketMavenRepo;
    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;
    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;

    @Autowired
    private NexusDefaultInitConfiguration nexusDefaultInitConfiguration;

    @Override
    public NexusRepositoryDTO getRepo(Long organizationId, Long projectId, Long repositoryId) {

        NexusRepository query = new NexusRepository();
        query.setRepositoryId(repositoryId);
        query.setOrganizationId(organizationId);
        query.setProjectId(projectId);

        NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, repositoryId);

        NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        //将成员列表重名的去重
        if (CollectionUtils.isNotEmpty(nexusServerRepository.getRepoMemberList())) {
            nexusServerRepository.setRepoMemberList(nexusServerRepository.getRepoMemberList().stream().distinct().collect(Collectors.toList()));
        }

        if (nexusServerRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        nexusRepository.setEnableAnonymousFlag(serverConfig.getEnableAnonymousFlag());
        NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
        nexusRepositoryDTO.convert(nexusRepository, nexusServerRepository);
        //Saas组织 默认仓库 注册组织默认仓库  则不展示URL
        ExternalTenantVO externalTenantVO = c7nBaseService.queryTenantByIdWithExternalInfo(organizationId);
        if (Objects.isNull(externalTenantVO)) {
            throw new CommonException("tenant not exists");
        }
        if (isRegister(externalTenantVO) || isSaas(externalTenantVO)) {
            if (serverConfig.getDefaultFlag() == BaseConstants.Flag.YES) {
                nexusRepositoryDTO.setUrl(null);
            }
        }

        nexusClient.removeNexusServerInfo();
        return nexusRepositoryDTO;
    }

    private boolean isRegister(ExternalTenantVO externalTenantVO) {
        return externalTenantVO.getRegister() != null && externalTenantVO.getRegister();
    }

    private boolean isSaas(ExternalTenantVO externalTenantVO) {
        return externalTenantVO.getSaasLevel() != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
            description = "创建maven仓库",
            inputSchemaClass = NexusRepositoryCreateDTO.class)
    public NexusRepositoryCreateDTO createRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

        // 步骤
        // 1. 更新数据库数据
        // 2. 创建仓库
        // 3. 创建仓库默认拉取角色
        // 4. 创建仓库默认用户，默认赋予角色，上述创建的角色
        // 5. 是否允许匿名
        //     允许，赋予匿名用户权限，如： nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
        //     不允许，去除匿名用户权限，如：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse

        // 参数校验
        nexusRepoCreateDTO.validParam(baseServiceFeignClient, true);

        NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient, projectId);

        // 匿名访问控制
        if (serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.NO) && nexusRepoCreateDTO.getAllowAnonymous().equals(BaseConstants.Flag.NO)) {
            throw new CommonException(NexusMessageConstants.NEXUS_ENABLE_ANONYMOUS_FLAG_IS_NO_NOT_SET);
        }


        if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())) {
            throw new CommonException(NexusApiConstants.ErrorMessage.REPO_NAME_EXIST);
        }

        // 1. 数据库数据更新
        // 仓库
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setConfigId(serverConfig.getConfigId());
        nexusRepository.setNeRepositoryName(nexusRepoCreateDTO.getName());
        nexusRepository.setOrganizationId(organizationId);
        nexusRepository.setProjectId(projectId);
        nexusRepository.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
        nexusRepository.setRepoType(nexusRepoCreateDTO.getRepoType());
        nexusRepository.setEnableFlag(NexusConstants.Flag.Y);
        nexusRepositoryRepository.insertSelective(nexusRepository);

        nexusRepoCreateDTO.setRepositoryId(nexusRepository.getRepositoryId());

        // 角色
        NexusServerRole nexusServerRole = new NexusServerRole();
        // 发布角色
        nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName(), true, null, nexusRepoCreateDTO.getFormat());
        // 拉取角色
        NexusServerRole pullNexusServerRole = new NexusServerRole();
        pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName(), null, nexusRepoCreateDTO.getFormat());

        NexusRole nexusRole = new NexusRole();
        nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
        nexusRole.setNePullRoleId(pullNexusServerRole.getId());
        nexusRole.setNeRoleId(nexusServerRole.getId());
        nexusRoleRepository.insertSelective(nexusRole);

        // 用户
        // 发布用户
        NexusServerUser nexusServerUser = new NexusServerUser();
        nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId(), null);
        // 拉取用户
        NexusServerUser pullNexusServerUser = new NexusServerUser();
        pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId(), null);

        NexusUser nexusUser = new NexusUser();
        nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
        nexusUser.setNeUserId(nexusServerUser.getUserId());
        nexusUser.setNeUserPassword(DESEncryptUtil.encode(nexusServerUser.getPassword()));
        nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
        nexusUser.setNePullUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
        nexusUserRepository.insertSelective(nexusUser);

        // 创建用户权限
        List<NexusAuth> nexusAuthList = nexusAuthService.createNexusAuth(Collections.singletonList(DetailsHelper.getUserDetails().getUserId()), nexusRepository.getRepositoryId(), NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        nexusRepoCreateDTO.setNexusAuthList(nexusAuthList);

        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("mavenRepo")
                        .withSourceId(projectId),
                builder -> {
                    builder.withPayloadAndSerialize(nexusRepoCreateDTO)
                            .withRefId(String.valueOf(nexusRepository.getRepositoryId()))
                            .withSourceId(projectId);
                });


        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return nexusRepoCreateDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE,
            description = "平台层-nexus仓库分配",
            inputSchemaClass = NexusRepository.class)
    public NexusRepositoryCreateDTO repoDistribute(NexusRepositoryCreateDTO nexusRepoCreateDTO) {

        // 步骤
        // 1. 更新数据库数据
        // 2. 创建仓库默认角色，赋予权限：nx-repository-view-[format]-[仓库名]-*; 创建仓库拉取角色
        // 3. 创建仓库拉取用户rdupm_nexus_user，分配仓库拉取角色（用于不允许匿名拉取时的pull操作）
        // 4. 根据传入的仓库管理员，创建制品库用户rdupm_prod_user，创建nexus用户并赋予默认角色
        // 5. 是否允许匿名
        //     允许，赋予匿名用户权限：nx-repository-view-[format]-[仓库名]-read   nx-repository-view-[format]-[仓库名]-browse
        //     不允许，去除匿名用户权限：nx-repository-view-[format]-[仓库名]-read   nx-repository-view-[format]-[仓库名]-browse

        // 参数校验
        nexusRepoCreateDTO.validParam(baseServiceFeignClient, true);

        // 平台层分配，分配时, 应该是choerodon默认nexus服务的
        NexusServerConfig defaultConfig = configService.setNexusDefaultInfo(nexusClient);

        if (!nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())) {
            throw new CommonException(NexusApiConstants.ErrorMessage.RESOURCE_NOT_EXIST);
        }

        // 1. 数据库数据更新
        // 仓库
        Long adminId = nexusRepoCreateDTO.getDistributeRepoAdminId();
        NexusRepository insertRepo = new NexusRepository();
        insertRepo.setCreatedBy(adminId);
        insertRepo.setConfigId(defaultConfig.getConfigId());
        insertRepo.setNeRepositoryName(nexusRepoCreateDTO.getName());
        insertRepo.setOrganizationId(nexusRepoCreateDTO.getOrganizationId());
        insertRepo.setProjectId(nexusRepoCreateDTO.getProjectId());
        insertRepo.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
        insertRepo.setRepoType(nexusRepoCreateDTO.getRepoType());
        nexusRepositoryRepository.distributeRepoInsert(insertRepo);
        List<NexusRepository> nexusRepositories = nexusRepositoryRepository.selectByCondition(
                Condition.builder(NexusRepository.class).andWhere(Sqls.custom().andEqualTo(NexusRepository.FIELD_NE_REPOSITORY_NAME, insertRepo.getNeRepositoryName())
                        .andEqualTo(NexusRepository.FIELD_CONFIG_ID, defaultConfig.getConfigId())).build());
        NexusRepository nexusRepository = nexusRepositories.get(0);

        // 角色
        NexusServerRole nexusServerRole = new NexusServerRole();
        // 发布角色
        nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName(), true, null, nexusRepoCreateDTO.getRepoType());
        // 拉取角色
        NexusServerRole pullNexusServerRole = new NexusServerRole();
        pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName(), null, nexusRepoCreateDTO.getRepoType());

        NexusRole nexusRole = new NexusRole();
        nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
        nexusRole.setNePullRoleId(pullNexusServerRole.getId());
        nexusRole.setNeRoleId(nexusServerRole.getId());
        nexusRoleRepository.insertSelective(nexusRole);

        // 拉取用户
        NexusServerUser pullNexusServerUser = new NexusServerUser();
        pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId(), null);
        // 发布用户
        NexusServerUser nexusServerUser = new NexusServerUser();
        nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId(), null);

        NexusUser nexusUser = new NexusUser();
        nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
        nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
        nexusUser.setNePullUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
        nexusUser.setNeUserId(nexusServerUser.getUserId());
        nexusUser.setNeUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
        nexusUserRepository.insertSelective(nexusUser);

        // 获取项目“项目管理员”角色人员
        // Long projectId = nexusRepoCreateDTO.getProjectId();
        // List<UserDTO> ownerUsers = c7nBaseService.listProjectOwnerUsers(projectId);
        // List<Long> userIds = ownerUsers.stream().map(UserDTO::getId).collect(Collectors.toList());
        // 创建用户权限
        List<NexusAuth> nexusAuthList = nexusAuthService.createNexusAuth(Collections.singletonList(adminId),
                nexusRepository.getRepositoryId(), NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        nexusRepoCreateDTO.setNexusAuthList(nexusAuthList);
        nexusRepository.setNexusAuthList(nexusAuthList);

        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE)
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("nexusRepo"),
                builder -> builder.withPayloadAndSerialize(nexusRepository)
                        .withRefId(String.valueOf(nexusRepository.getRepositoryId())));

        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return nexusRepoCreateDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE,
            description = "更新maven仓库",
            inputSchemaClass = NexusRepositoryCreateDTO.class)
    public NexusRepositoryCreateDTO updateRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

        // 参数校验
        nexusRepoCreateDTO.validParam(baseServiceFeignClient, false);


        NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (!nexusRepository.getProjectId().equals(projectId)) {
            throw new CommonException(NexusMessageConstants.NEXUS_MAVEN_REPO_NOT_CHANGE_OTHER_PRO);
        }

        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, repositoryId);

        // 匿名访问控制
        if (serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.NO) && nexusRepoCreateDTO.getAllowAnonymous().equals(BaseConstants.Flag.NO)) {
            throw new CommonException(NexusMessageConstants.NEXUS_ENABLE_ANONYMOUS_FLAG_IS_NO_NOT_SET);
        }


        if (!nexusRepository.getAllowAnonymous().equals(nexusRepoCreateDTO.getAllowAnonymous())) {
            nexusRepository.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
            nexusRepositoryRepository.updateOptional(nexusRepository, NexusRepository.FIELD_ALLOW_ANONYMOUS);
        }

        nexusRepoCreateDTO.setRepositoryId(repositoryId);
        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("updateMavenRepo")
                        .withSourceId(projectId),
                builder -> {
                    builder.withPayloadAndSerialize(nexusRepoCreateDTO)
                            .withRefId(String.valueOf(nexusRepository.getRepositoryId()))
                            .withSourceId(projectId);
                });

        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return nexusRepoCreateDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE,
            description = "删除maven仓库",
            inputSchemaClass = NexusRepositoryDeletePayload.class)
    public void deleteRepo(Long organizationId, Long projectId, Long repositoryId) {
        // 仓库
        NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }


        configService.setNexusInfoByRepositoryId(nexusClient, repositoryId);
        // hosted类型仓库，查询是否还有组件包
        NexusComponentCountParam countParam = new NexusComponentCountParam();
        countParam.setRepositoryName(nexusRepository.getNeRepositoryName());
        String param = JSONObject.toJSONString(countParam);
        NexusScriptResult nexusScriptResult = nexusClient.getNexusScriptApi().runScript(NexusApiConstants.ScriptName.COMPONENT_COUNT_QUERY_DELETE, param);
        nexusClient.removeNexusServerInfo();
        if (nexusScriptResult != null && nexusScriptResult.getResult() != null && Long.parseLong(nexusScriptResult.getResult()) > 0) {
            throw new CommonException("error.nexus.count.is.null.not.delete");
        }
        // 角色
        NexusRole roleQuery = new NexusRole();
        roleQuery.setRepositoryId(repositoryId);
        NexusRole nexusRole = nexusRoleRepository.selectOne(roleQuery);
        // 用户
        NexusUser userQuery = new NexusUser();
        userQuery.setRepositoryId(repositoryId);
        NexusUser nexusUser = nexusUserRepository.selectOne(userQuery);

        // 权限
        NexusAuth authQuery = new NexusAuth();
        authQuery.setRepositoryId(repositoryId);
        List<NexusAuth> nexusAuthList = nexusAuthRepository.select(authQuery);

        // 数据库数据删除
        nexusRepositoryRepository.deleteByPrimaryKey(nexusRepository);
        nexusRoleRepository.deleteByPrimaryKey(nexusRole);
        nexusUserRepository.deleteByPrimaryKey(nexusUser);
        nexusAuthRepository.batchDeleteByPrimaryKey(nexusAuthList);


        NexusRepositoryDeletePayload deletePayload = new NexusRepositoryDeletePayload();
        deletePayload.setNexusRepository(nexusRepository);
        deletePayload.setNexusRole(nexusRole);
        deletePayload.setNexusUser(nexusUser);
        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("deleteMavenRepo")
                        .withSourceId(projectId),
                builder -> {
                    builder.withPayloadAndSerialize(deletePayload)
                            .withRefId(String.valueOf(nexusRepository.getRepositoryId()))
                            .withSourceId(projectId);
                });

    }

    @Override
    public Page<NexusRepositoryOrgDTO> listOrgRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO) {
        // 查询某个组织项目数据
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.listOrgRepo(queryDTO.getOrganizationId(), queryDTO.getRepoType());

        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            return new Page<>();
        }
        Map<String, NexusServerRepository> nexusServerRepositoryMapAll = new HashMap<>(16);
        // nexus服务仓库数据查询
        Map<Long, List<NexusRepository>> projectRepoMap = nexusRepositoryList.stream().collect(Collectors.groupingBy(NexusRepository::getConfigId));
        projectRepoMap.forEach((key, value) -> {
            // 设置并返回当前nexus服务信息
            configService.setNexusInfoByConfigId(nexusClient, key);
            List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(queryDTO.getRepoType()));
            for (NexusServerRepository nexusServerRepository : nexusServerRepositoryList) {
                // 以configId加仓库名作为key
                nexusServerRepositoryMapAll.put(key + "-" + nexusServerRepository.getName(), nexusServerRepository);
            }
        });

        this.setUserInfo(nexusRepositoryList);

        List<NexusRepositoryDTO> resultAll = new ArrayList<>();

        for (NexusRepository repository : nexusRepositoryList) {
            NexusServerRepository nexusServerRepository = nexusServerRepositoryMapAll.get(repository.getConfigId() + "-" + repository.getNeRepositoryName());
            NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
            nexusRepositoryDTO.convert(repository, nexusServerRepository);
            resultAll.add(nexusRepositoryDTO);
        }
        resultAll = this.setInfoAndQuery(resultAll, queryDTO);

        // remove配置信息
        nexusClient.removeNexusServerInfo();

        // 主键加密后，此处返回不加密
        List<NexusRepositoryOrgDTO> result = new ArrayList<>();
        resultAll.forEach(nexusRepositoryDTO -> {
            NexusRepositoryOrgDTO nexusRepositoryOrgDTO = new NexusRepositoryOrgDTO();
            BeanUtils.copyProperties(nexusRepositoryDTO, nexusRepositoryOrgDTO);
            result.add(nexusRepositoryOrgDTO);
        });

        return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), result);
    }

    @Override
    public Page<NexusRepositoryDTO> listNexusRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO) {
        // 设置并返回当前nexus服务信息
        NexusServerConfig nexusServerConfig = configService.setNexusDefaultInfo(nexusClient);
        Page<NexusRepositoryDTO> pageResult = this.queryNexusRepo(queryDTO, pageRequest, nexusServerConfig);
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return pageResult;
    }

    private Page<NexusRepositoryDTO> queryNexusRepo(NexusRepositoryQueryDTO queryDTO, PageRequest pageRequest, NexusServerConfig nexusServerConfig) {
        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(queryDTO.getRepoType()));
        List<NexusRepositoryDTO> resultAll = new ArrayList<>();
        if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
            return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
        }

        // 查询所有的nexus仓库，标识已经关联项目的仓库
        //this.mavenRepoAll(nexusServerRepositoryList, queryDTO, resultAll);

        // 所有项目仓库数据
        NexusRepository query = new NexusRepository();
        if (queryDTO.getRepoType() != null) {
            query.setRepoType(queryDTO.getRepoType());
        }
        query.setConfigId(nexusServerConfig.getConfigId());
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
        Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));
        this.setUserInfo(nexusRepositoryList);
        for (NexusServerRepository serverRepository : nexusServerRepositoryList) {
            NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
            nexusRepositoryDTO.convert(nexusRepositoryMap.get(serverRepository.getName()), serverRepository);
            resultAll.add(nexusRepositoryDTO);
        }

        if (CollectionUtils.isEmpty(resultAll)) {
            return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
        }
        // 项目名称查询
        Set<Long> projectIdSet = resultAll.stream().map(NexusRepositoryDTO::getProjectId).collect(Collectors.toSet());
        List<ProjectVO> projectVOList = baseServiceFeignClient.queryByIds(projectIdSet);
        Map<Long, ProjectVO> projectVOMap = projectVOList.stream().collect(Collectors.toMap(ProjectVO::getId, a -> a, (k1, k2) -> k1));
        resultAll.forEach(nexusRepositoryDTO -> {
            ProjectVO projectVO = projectVOMap.get(nexusRepositoryDTO.getProjectId());
            if (projectVO != null) {
                nexusRepositoryDTO.setProjectName(projectVO.getName());
                nexusRepositoryDTO.setProjectImgUrl(projectVO.getImageUrl());
            }
        });

        // 查询参数
        if (queryDTO.getRepositoryName() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getName().toLowerCase().contains(queryDTO.getRepositoryName().toLowerCase())).collect(Collectors.toList());
        }
        if (queryDTO.getDistributedQueryFlag() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO -> {
                if (Objects.nonNull(queryDTO.getDistributedQueryFlag())) {
                    return Objects.equals(queryDTO.getDistributedQueryFlag(), BaseConstants.Flag.NO) ? Objects.isNull(nexusRepositoryDTO.getRepositoryId()) :
                            Objects.nonNull(nexusRepositoryDTO.getRepositoryId());
                }
                return true;
            }).collect(Collectors.toList());
        }
        if (queryDTO.getType() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getType().toLowerCase().contains(queryDTO.getType().toLowerCase())).collect(Collectors.toList());
        }
        if (queryDTO.getVersionPolicy() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getVersionPolicy() != null && nexusRepositoryDTO.getVersionPolicy().toLowerCase().contains(queryDTO.getVersionPolicy().toLowerCase())).collect(Collectors.toList());
        }
        resultAll.sort(new NexusRepositoryDTO());
        return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
    }


    @Override
    public List<NexusRepositoryDTO> listRepoAll(NexusRepositoryQueryDTO queryDTO) {
        // 设置并返回项目当前nexus服务信息
        NexusServerConfig nexusServerConfig = configService.setNexusInfo(nexusClient, queryDTO.getProjectId());

        // 查询某个项目项目数据
        Condition.Builder builder = Condition.builder(NexusRepository.class)
                .where(Sqls.custom()
                        .andEqualTo(NexusRepository.FIELD_PROJECT_ID, queryDTO.getProjectId()));
        if (queryDTO.getRepoType() != null) {
            builder.where(Sqls.custom()
                    .andEqualTo(NexusRepository.FIELD_REPO_TYPE, queryDTO.getRepoType()));
        }
        builder.where(Sqls.custom()
                .andEqualTo(NexusRepository.FIELD_CONFIG_ID, nexusServerConfig.getConfigId()));

        Condition condition = builder.build();
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(condition);
        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            nexusClient.removeNexusServerInfo();
            return new ArrayList<>();
        }
        nexusRepositoryList.forEach(nexusRepository -> {
            nexusRepository.setEnableAnonymousFlag(nexusServerConfig.getEnableAnonymousFlag());
            //统计仓库总的下载人数和下载次数
            NexusLog nexusLog = new NexusLog();
            nexusLog.setOperateType(NexusConstants.LogOperateType.AUTH_PULL);
            nexusLog.setRepositoryId(nexusRepository.getRepositoryId());
            List<NexusLog> nexusLogs = nexusLogMapper.select(nexusLog);
            Long personTimes = 0L;
            Long downloadTimes = 0L;
            if (!CollectionUtils.isEmpty(nexusLogs)) {
                downloadTimes = Long.valueOf(nexusLogs.size());
                Map<Long, List<NexusLog>> longListMap = nexusLogs.stream().collect(Collectors.groupingBy(NexusLog::getOperatorId));
                personTimes = Long.valueOf(longListMap.keySet().size());
            }
            nexusRepository.setDownloadTimes(downloadTimes);
            nexusRepository.setPersonTimes(personTimes);
        });


        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(queryDTO.getRepoType()));
        if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
            return new ArrayList<>();
        }
        Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));


        List<NexusRepositoryDTO> resultAll = new ArrayList<>();

        this.mavenRepoConvert(resultAll, nexusRepositoryList, nexusServerRepositoryMap);
        resultAll = this.setInfoAndQuery(resultAll, queryDTO);
        // remove配置信息

        return resultAll;
    }

    /**
     * 设置用户信息
     *
     * @param nexusRepositoryList 仓库列表
     */
    private void setUserInfo(List<NexusRepository> nexusRepositoryList) {
        //创建人ID去重，并获得创建人详细信息
        Set<Long> userIdSet = nexusRepositoryList.stream().map(AuditDomain::getCreatedBy).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(userIdSet)) {
            List<UserDTO> userDTOList = baseServiceFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]), true);
            Map<Long, UserDTO> userDtoMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId, dto -> dto));
            if (CollectionUtils.isNotEmpty(userDTOList)) {
                nexusRepositoryList.forEach(repository -> {
                    //设置创建人登录名、真实名称、创建人头像
                    UserDTO userDTO = userDtoMap.get(repository.getCreatedBy());
                    if (userDTO != null) {
                        repository.setCreatorImageUrl(userDTO.getImageUrl());
                        repository.setCreatorLoginName(userDTO.getLoginName());
                        repository.setCreatorRealName(userDTO.getRealName());
                    }
                });
            }
        }
    }

    private void mavenRepoConvert(List<NexusRepositoryDTO> resultAll,
                                  List<NexusRepository> nexusRepositoryList,
                                  Map<String, NexusServerRepository> nexusServerRepositoryMap) {
        // 设置用户信息
        this.setUserInfo(nexusRepositoryList);
        nexusRepositoryList.forEach(repository -> {
            NexusServerRepository nexusServerRepository = nexusServerRepositoryMap.get(repository.getNeRepositoryName());
            NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
            nexusRepositoryDTO.convert(repository, nexusServerRepository);
            resultAll.add(nexusRepositoryDTO);

        });
    }

    private List<NexusRepositoryDTO> setInfoAndQuery(List<NexusRepositoryDTO> resultAll, NexusRepositoryQueryDTO queryDTO) {
        // 项目名称查询
        Set<Long> projectIdSet = resultAll.stream().map(NexusRepositoryDTO::getProjectId).collect(Collectors.toSet());
        List<ProjectVO> projectVOList = baseServiceFeignClient.queryByIds(projectIdSet);
        Map<Long, ProjectVO> projectVOMap = projectVOList.stream().collect(Collectors.toMap(ProjectVO::getId, a -> a, (k1, k2) -> k1));
        resultAll.forEach(nexusRepositoryDTO -> {
            ProjectVO projectVO = projectVOMap.get(nexusRepositoryDTO.getProjectId());
            if (projectVO != null) {
                nexusRepositoryDTO.setProjectName(projectVO.getName());
                nexusRepositoryDTO.setProjectImgUrl(projectVO.getImageUrl());
            }
        });

        // 查询参数
        if (queryDTO.getRepositoryName() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getNeRepositoryName().toLowerCase().contains(queryDTO.getRepositoryName().toLowerCase())).collect(Collectors.toList());
        }
        if (queryDTO.getType() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getType().toLowerCase().contains(queryDTO.getType().toLowerCase())).collect(Collectors.toList());
        }
        if (queryDTO.getVersionPolicy() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    nexusRepositoryDTO.getVersionPolicy() != null && nexusRepositoryDTO.getVersionPolicy().toLowerCase().contains(queryDTO.getVersionPolicy().toLowerCase())).collect(Collectors.toList());
        }
        if (queryDTO.getProjectId() != null) {
            resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
                    Objects.equals(queryDTO.getProjectId(), nexusRepositoryDTO.getProjectId())).collect(Collectors.toList());
        }
        return resultAll;
    }

    @Override
    public List<NexusRepositoryListDTO> listRepoNameAll(Long projectId, String repoType) {
        NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient, projectId);

        // 所有nexus服务仓库数据
        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
        if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
            return new ArrayList<>();
        }
        // 所有项目仓库数据
        List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(null, repoType, serverConfig.getConfigId());

        List<NexusRepositoryListDTO> resultAll = new ArrayList<>();
        nexusServerRepositoryList.forEach(serverRepository -> {
            if (!repositoryNameList.contains(serverRepository.getName())) {
                NexusRepositoryListDTO nexusRepositoryDTO = new NexusRepositoryListDTO();
                nexusRepositoryDTO.setName(serverRepository.getName());
                resultAll.add(nexusRepositoryDTO);
            }
        });

        // remove配置信息
        nexusClient.removeNexusServerInfo();
        //去除应用市场的仓库
        List<NexusRepositoryListDTO> nexusRepositoryListDTOS = resultAll.stream().filter(nexusRepositoryListDTO -> !StringUtils.equalsIgnoreCase(nexusRepositoryListDTO.getName().trim(), marketMavenRepo)).collect(Collectors.toList());
        return nexusRepositoryListDTOS;
    }

    @Override
    public List<NexusRepositoryListDTO> listRepoName(NexusRepository query, String repoType) {
        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient, query.getProjectId());

        // nexus服务，仓库数据
        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
        if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
            return new ArrayList<>();
        }
        List<String> serverRepositoryNameList = nexusServerRepositoryList.stream().map(NexusServerRepository::getName).collect(Collectors.toList());

        // 仓库数据
        query.setRepoType(repoType);
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.listRepositoryByProject(query, serverConfig.getConfigId());
        List<String> repositoryNameList = nexusRepositoryList.stream().map(NexusRepository::getNeRepositoryName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(repositoryNameList)) {
            return new ArrayList<>();
        }

        List<NexusRepositoryListDTO> resultAll = new ArrayList<>();
        repositoryNameList.forEach(repositoryName -> {
            if (serverRepositoryNameList.contains(repositoryName)) {
                NexusRepositoryListDTO nexusRepositoryDTO = new NexusRepositoryListDTO();
                nexusRepositoryDTO.setName(repositoryName);
                resultAll.add(nexusRepositoryDTO);
            }
        });
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return resultAll;
    }

    @Override
    public List<NexusRepositoryOrgDTO> listOrgRepoName(NexusRepository query, String repoType) {
        List<NexusRepositoryOrgDTO> resultAll = new ArrayList<>();
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.listOrgRepo(query.getOrganizationId(), repoType);
        for (NexusRepository repository : nexusRepositoryList) {
            NexusRepositoryOrgDTO nexusRepositoryDTO = new NexusRepositoryOrgDTO();
            BeanUtils.copyProperties(repository, nexusRepositoryDTO);
            repository.setName(repository.getNeRepositoryName());
            resultAll.add(nexusRepositoryDTO);
        }
        return resultAll;
    }

    @Override
    public List<NexusRepositoryDTO> listComponentRepo(Long projectId, String repoType) {
        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient, projectId);

        // nexus服务，仓库数据
        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
        if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
            return new ArrayList<>();
        }
        Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));


        // 当前项目仓库数据
        List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId, repoType, serverConfig.getConfigId());
        if (CollectionUtils.isEmpty(repositoryNameList)) {
            return new ArrayList<>();
        }

        List<NexusRepositoryDTO> resultAll = new ArrayList<>();
        repositoryNameList.forEach(repositoryName -> {
            NexusServerRepository serverRepository = nexusServerRepositoryMap.get(repositoryName);
            if (NexusConstants.RepoType.MAVEN.equals(repoType)) {
                if (serverRepository != null && NexusApiConstants.VersionPolicy.RELEASE.equals(serverRepository.getVersionPolicy())
                        && NexusApiConstants.RepositoryType.HOSTED.equals(serverRepository.getType())) {
                    // 包上传时，需要限制为RELEASE
                    NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
                    nexusRepositoryDTO.setName(repositoryName);
                    resultAll.add(nexusRepositoryDTO);
                }
            } else if (NexusConstants.RepoType.NPM.equals(repoType)) {
                if (serverRepository != null && NexusApiConstants.RepositoryType.HOSTED.equals(serverRepository.getType())) {
                    // 包上传时，需要限制为hosted
                    NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
                    nexusRepositoryDTO.setName(repositoryName);
                    resultAll.add(nexusRepositoryDTO);
                }
            }

        });
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return resultAll;
    }

    @Override
    public NexusGuideDTO mavenRepoGuide(Long repositoryId, String repositoryName, Boolean showPushFlag) {
        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, repositoryId);

        NexusRepository query = new NexusRepository();
        query.setNeRepositoryName(repositoryName);
        query.setRepositoryId(repositoryId);
        NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        NexusUser queryUser = new NexusUser();
        queryUser.setRepositoryId(nexusRepository.getRepositoryId());
        NexusUser nexusUser = nexusUserRepository.selectOne(queryUser);


        NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(repositoryName);
        if (nexusServerRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        // 返回信息
        NexusGuideDTO nexusGuideDTO = new NexusGuideDTO();
        // 设置拉取配置信息
        nexusGuideDTO.handlePullGuideValue(nexusServerRepository, nexusRepository, nexusUser, serverConfig, nexusProxyConfigProperties);
        // 设置发布配置信息
        nexusGuideDTO.handlePushGuideValue(nexusServerRepository, serverConfig, nexusUser, showPushFlag, nexusProxyConfigProperties);
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return nexusGuideDTO;
    }

    /**
     * 制品库类型，转换为nexus format
     *
     * @return
     */
    @Override
    public String convertRepoTypeToFormat(String repoType) {
        if (NexusConstants.RepoType.MAVEN.equals(repoType)) {
            return NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT;
        } else if (NexusConstants.RepoType.NPM.equals(repoType)) {
            return NexusApiConstants.NexusRepoFormat.NPM_FORMAT;
        } else {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = NexusSagaConstants.NexusRepoEnableAndDisable.NEXUS_REPO_ENABLE_AND_DISABLE,
            description = "项目层-nexus仓库生效与失效",
            inputSchemaClass = NexusRepository.class)
    public void nexusRepoEnableAndDisAble(Long organizationId, Long projectId, Long repositoryId, String enableFlag) {
        NexusRepository repository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
        if (repository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        if (enableFlag.equals(NexusConstants.Flag.Y)) {
            if (repository.getEnableFlag().equals(enableFlag)) {
                throw new CommonException(NexusMessageConstants.NEXUS_REPO_IS_ENABLE);
            }

        } else if (enableFlag.equals(NexusConstants.Flag.N)) {
            if (repository.getEnableFlag().equals(enableFlag)) {
                throw new CommonException(NexusMessageConstants.NEXUS_REPO_IS_DISABLE);
            }
        } else {
            throw new CommonException(NexusMessageConstants.NEXUS_PARAM_ERROR);
        }

        repository.setEnableFlag(enableFlag);
        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusRepoEnableAndDisable.NEXUS_REPO_ENABLE_AND_DISABLE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("nexusRepoEnableAndDisAble")
                        .withSourceId(projectId),
                startSagaBuilder -> {
                    nexusRepositoryRepository.updateOptional(repository, NexusRepository.FIELD_ENABLE_FLAG);
                    startSagaBuilder.withPayloadAndSerialize(repository).withSourceId(repository.getRepositoryId());
                });
    }

    @Override
    public List<NexusRepoDTO> getRepoByProject(Long organizationId, Long projectId, String repoType, String type) {
        NexusServerConfig nexusServerConfig = configService.setNexusInfo(nexusClient, projectId);

        NexusRepository query = new NexusRepository();
        query.setRepoType(repoType);
        query.setProjectId(projectId);
        query.setConfigId(nexusServerConfig.getConfigId());
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            return new ArrayList<>();
        }
        List<NexusRepoDTO> resultAll = new ArrayList<>();

        List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
        Map<String, NexusServerRepository> repoMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, k -> k));
        nexusRepositoryList.forEach(nexusRepository -> {
            if (repoMap.containsKey(nexusRepository.getNeRepositoryName())) {
                NexusServerRepository nexusServerRepository = repoMap.get(nexusRepository.getNeRepositoryName());
                NexusRepoDTO nexusRepoDTO = new NexusRepoDTO();
                nexusRepoDTO.setRepositoryId(nexusRepository.getRepositoryId());
                nexusRepoDTO.setName(nexusRepository.getNeRepositoryName());
                nexusRepoDTO.setType(nexusServerRepository.getType());
                nexusRepoDTO.setUrl(nexusServerRepository.getUrl());
                nexusRepoDTO.setVersionPolicy(nexusServerRepository.getVersionPolicy());
                resultAll.add(nexusRepoDTO);
            }
        });
        nexusClient.removeNexusServerInfo();

        List<NexusRepoDTO> result;
        if (type != null) {
            result = resultAll.stream().filter(nexusRepoDTO -> type.equals(nexusRepoDTO.getType())).collect(Collectors.toList());
        } else {
            result = resultAll;
        }
        return result;
    }

    @Override
    public List<NexusRepoDTO> getRepoUserByProject(Long organizationId, Long projectId, List<Long> repositoryIds) {

        List<NexusRepoDTO> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(repositoryIds)) {
            return result;
        }
        List<NexusRepoDTO> nexusRepositoryList = nexusRepositoryRepository.selectInfoByIds(repositoryIds);
        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            return result;
        }

        // nexus服务仓库数据查询
        Map<String, NexusServerRepository> nexusServerRepositoryMapAll = new HashMap<>(16);
        Map<Long, List<NexusRepoDTO>> projectRepoMap = nexusRepositoryList.stream().collect(Collectors.groupingBy(NexusRepoDTO::getConfigId));
        projectRepoMap.forEach((key, value) -> {
            // 设置并返回当前nexus服务信息
            configService.setNexusInfoByConfigId(nexusClient, key);
            List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(null);
            for (NexusServerRepository nexusServerRepository : nexusServerRepositoryList) {
                // 以configId加仓库名作为key
                nexusServerRepositoryMapAll.put(key + "-" + nexusServerRepository.getName(), nexusServerRepository);
            }
        });
        nexusRepositoryList.forEach(nexusRepoDTO -> {
            String key = nexusRepoDTO.getConfigId() + "-" + nexusRepoDTO.getName();
            if (nexusServerRepositoryMapAll.containsKey(key)) {
                NexusServerRepository nexusServerRepository = nexusServerRepositoryMapAll.get(key);
                nexusRepoDTO.setType(nexusServerRepository.getType());
                //如果是默认仓库，并且组织属于注册或者试用组织 则返回回代理的地址
                NexusServerConfig nexusServerConfig = nexusServerConfigMapper.selectByPrimaryKey(nexusRepoDTO.getConfigId());
                ExternalTenantVO externalTenantVO = c7nBaseService.queryTenantByIdWithExternalInfo(organizationId);
                if (Objects.isNull(externalTenantVO)) {
                    throw new CommonException("tenant not exists");
                }
                if (nexusServerConfig.getDefaultFlag().equals(BaseConstants.Flag.YES) && isRegisterOrSaasOrganization(externalTenantVO)) {
                    nexusRepoDTO.setUrl(nexusServerRepository.getUrl().replace(nexusDefaultInitConfiguration.getServerUrl(), nexusProxyConfigProperties.getUrl() + nexusProxyConfigProperties.getUriPrefix() + BaseConstants.Symbol.SLASH + nexusServerConfig.getConfigId()));
                } else {
                    nexusRepoDTO.setUrl(nexusServerRepository.getUrl());
                }
                nexusRepoDTO.setVersionPolicy(nexusServerRepository.getVersionPolicy());
                if (nexusRepoDTO.getNeUserPassword() != null) {
                    nexusRepoDTO.setNeUserPassword(DESEncryptUtil.decode(nexusRepoDTO.getNeUserPassword()));
                }
                if (nexusRepoDTO.getNePullUserPassword() != null) {
                    nexusRepoDTO.setNePullUserPassword(DESEncryptUtil.decode(nexusRepoDTO.getNePullUserPassword()));
                }
                result.add(nexusRepoDTO);
            }
        });
        nexusClient.removeNexusServerInfo();
        return result;
    }

    private Boolean isRegisterOrSaasOrganization(ExternalTenantVO externalTenantVO) {
        if (externalTenantVO.getRegister() == null && externalTenantVO.getSaasLevel() == null) {
            return false;
        }
        return true;
    }

    @Override
    public NexusRepositoryRelatedDTO relatedRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {

        NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient, projectId);
        // 参数校验
        nexusRepositoryRelatedDTO.validParam(nexusClient, serverConfig, nexusRepositoryRelatedDTO.getRepoType(), this, nexusRepositoryRepository);

        List<String> errorNameList = new ArrayList<>();
        List<String> nexusRepositoryNameList = nexusRepositoryRelatedDTO.getRepositoryList().stream().distinct().collect(Collectors.toList());
        nexusRepositoryNameList.forEach(repositoryName -> {
            try {
                self().selfRelatedMavenRepo(organizationId, projectId, nexusRepositoryRelatedDTO.getRepoType(), repositoryName, serverConfig);
            } catch (Exception e) {
                LOGGER.error("关联仓库失败", e);
                errorNameList.add(repositoryName);
            }
        });
        if (CollectionUtils.isNotEmpty(errorNameList)) {
            throw new CommonException(NexusMessageConstants.NEXUS_REPO_RELATED_ERROR, StringUtils.join(errorNameList, ", "));
        }
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return nexusRepositoryRelatedDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Saga(code = NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED,
            description = "关联maven仓库",
            inputSchemaClass = NexusRepository.class)
    public void selfRelatedMavenRepo(Long organizationId, Long projectId, String repoType, String repositoryName, NexusServerConfig serverConfig) {
        // 1. 数据库数据更新
        // 仓库
        Long adminId = DetailsHelper.getUserDetails().getUserId();
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setConfigId(serverConfig.getConfigId());
        nexusRepository.setNeRepositoryName(repositoryName);
        nexusRepository.setOrganizationId(organizationId);
        nexusRepository.setProjectId(projectId);
        nexusRepository.setAllowAnonymous(BaseConstants.Flag.YES);
        nexusRepository.setRepoType(repoType);
        nexusRepository.setEnableFlag(NexusConstants.Flag.Y);
        nexusRepositoryRepository.insertSelective(nexusRepository);

        // 角色
        NexusServerRole nexusServerRole = new NexusServerRole();
        // 发布角色
        nexusServerRole.createDefPushRole(repositoryName, true, null, repoType);
        // 拉取角色
        NexusServerRole pullNexusServerRole = new NexusServerRole();
        pullNexusServerRole.createDefPullRole(repositoryName, null, repoType);

        NexusRole nexusRole = new NexusRole();
        nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
        nexusRole.setNePullRoleId(pullNexusServerRole.getId());
        nexusRole.setNeRoleId(nexusServerRole.getId());
        nexusRoleRepository.insertSelective(nexusRole);

        // 拉取用户
        NexusServerUser pullNexusServerUser = new NexusServerUser();
        pullNexusServerUser.createDefPullUser(repositoryName, pullNexusServerRole.getId(), null);
        // 发布用户
        NexusServerUser nexusServerUser = new NexusServerUser();
        nexusServerUser.createDefPushUser(repositoryName, nexusServerRole.getId(), null);

        NexusUser nexusUser = new NexusUser();
        nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
        nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
        nexusUser.setNePullUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
        nexusUser.setNeUserId(nexusServerUser.getUserId());
        nexusUser.setNeUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
        nexusUserRepository.insertSelective(nexusUser);

        List<NexusAuth> nexusAuthList = nexusAuthService.createNexusAuth(Collections.singletonList(adminId),
                nexusRepository.getRepositoryId(), NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        nexusRepository.setNexusAuthList(nexusAuthList);

        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("relatedMavenRepo")
                        .withSourceId(projectId),
                builder -> {
                    builder.withPayloadAndSerialize(nexusRepository)
                            .withRefId(String.valueOf(nexusRepository.getRepositoryId()))
                            .withSourceId(projectId);
                });
    }

    @Override
    public NexusRepositoryVO queryNexusRepositoryByName(Long nexusServiceConfigId, String repositoryName) {
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setNeRepositoryName(repositoryName);
        nexusRepository.setConfigId(nexusServiceConfigId);
        NexusRepository repository = nexusRepositoryRepository.selectOne(nexusRepository);
        NexusRepositoryVO nexusRepositoryVO = new NexusRepositoryVO();
        BeanUtils.copyProperties(repository, nexusRepositoryVO);
        return nexusRepositoryVO;
    }

    @Override
    public Long queryNexusProjectCapacity(Long repositoryId) {
        NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
        if (nexusRepository == null) {
            return Long.valueOf(BaseConstants.Digital.NEGATIVE_ONE);
        }
        NexusAssets record = new NexusAssets();
        record.setProjectId(nexusRepository.getProjectId());
        List<NexusAssets> nexusAssets = nexusAssetsMapper.select(record);
        if (CollectionUtils.isEmpty(nexusAssets)) {
            return Long.valueOf(BaseConstants.Digital.ZERO);
        } else {
            return nexusAssets.stream().map(NexusAssets::getSize).reduce((aLong, aLong2) -> aLong + aLong2).orElseGet(() -> 0L);
        }
    }

}
