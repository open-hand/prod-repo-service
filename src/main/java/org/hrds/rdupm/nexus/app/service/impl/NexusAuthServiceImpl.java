package org.hrds.rdupm.nexus.app.service.impl;

import com.google.common.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.job.ExpiredNexusAuthJob;
import org.hrds.rdupm.nexus.app.service.NexusApiService;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.annotation.NexusOperateLog;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.mapper.NexusAuthMapper;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus权限表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@Service
public class NexusAuthServiceImpl implements NexusAuthService, AopProxy<NexusAuthService> {
    private static final Logger logger = LoggerFactory.getLogger(NexusAuthServiceImpl.class);

    @Autowired
    private NexusAuthMapper nexusAuthMapper;

    @Autowired
    private BaseFeignClient baseFeignClient;
    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private ProdUserRepository prodUserRepository;
    @Autowired
    private ProdUserService prodUserService;
    @Autowired
    private NexusRoleRepository nexusRoleRepository;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusServerConfigService configService;
    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;
    @Autowired
    private NexusApiService nexusApiService;


    @Override
    public Page<NexusAuth> pageList(PageRequest pageRequest, NexusAuth nexusAuth) {
        NexusServerConfig serverConfig = nexusServerConfigRepository.queryEnableServiceConfig(nexusAuth.getProjectId());
        nexusAuth.setConfigId(serverConfig.getConfigId());
        return this.pageListOrg(pageRequest, nexusAuth);
    }

    @Override
    public Page<NexusAuth> export(PageRequest pageRequest, NexusAuth nexusAuth, ExportParam exportParam, HttpServletResponse response) {
        return this.pageList(pageRequest, nexusAuth);
    }

    @Override
    public Page<NexusAuth> pageListOrg(PageRequest pageRequest, NexusAuth nexusAuth) {
        return this.pageInfo(pageRequest, nexusAuth);
    }

    @Override
    @ExcelExport(NexusAuth.class)
    public Page<NexusAuth> exportOrg(PageRequest pageRequest, NexusAuth nexusAuth, ExportParam exportParam, HttpServletResponse response) {
        return this.pageListOrg(pageRequest, nexusAuth);
    }


    private Page<NexusAuth> pageInfo (PageRequest pageRequest, NexusAuth nexusAuth) {
        Page<NexusAuth> authPage = PageHelper.doPageAndSort(pageRequest, () -> nexusAuthMapper.list(nexusAuth));
        List<NexusAuth> dataList = authPage.getContent();
        if(CollectionUtils.isEmpty(dataList)){
            return authPage;
        }

        //查询成员角色
        Map<Long,List<NexusAuth>> dataListMap = dataList.stream().collect(Collectors.groupingBy(NexusAuth::getProjectId));
        Map<Long, UserWithGitlabIdDTO> userDtoMap = new HashMap<>(16);
        for(Map.Entry<Long,List<NexusAuth>> entry : dataListMap.entrySet()){
            Long projectId = entry.getKey();
            List<NexusAuth> list = entry.getValue();
            Set<Long> userIdSet = list.stream().map(NexusAuth::getUserId).collect(Collectors.toSet());
            ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
            userDtoMap.putAll(Objects.requireNonNull(responseEntity.getBody()).stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId, dto->dto)));
        }

        // 项目名称查询
        Set<Long> projectIdSet = dataList.stream().map(NexusAuth::getProjectId).collect(Collectors.toSet());
        Map<Long, ProjectDTO> projectDtoMap = c7nBaseService.queryProjectByIds(projectIdSet);

        dataList.forEach(auth->{
            UserWithGitlabIdDTO userDto = userDtoMap.get(auth.getUserId());
            if(userDto != null){
                auth.setRealName(userDto.getRealName());
                auth.setUserImageUrl(userDto.getImageUrl());

                List<RoleDTO> roleDTOList = userDto.getRoles();
                if(CollectionUtils.isNotEmpty(roleDTOList)){
                    StringBuilder memberRole = new StringBuilder();
                    for(RoleDTO roleDTO : roleDTOList){
                        memberRole.append(roleDTO.getName()).append(" ");
                    }
                    auth.setMemberRole(memberRole.toString());
                }
            }

            // 项目名
            ProjectDTO projectDTO = projectDtoMap.get(auth.getProjectId());
            if (projectDTO != null) {
                auth.setProjectName(projectDTO.getName());
            }
        });

        return authPage;
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_CREATE, content = "%s 分配 %s 【%s】仓库的权限角色为 【%s】,过期日期为【%s】")
    @Saga(code = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE, description = "nexus分配权限", inputSchemaClass = List.class)
    @Transactional(rollbackFor = Exception.class)
    public void create(Long projectId, List<NexusAuth> nexusAuthList) {
        /// 设置用户权限、获取用户信息
        List<ProdUser> prodUserList = new ArrayList<>();
        this.setAuthInfoAndProdUser(nexusAuthList, prodUserList);

        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("createMavenAuth")
                        .withSourceId(projectId),
                startSagaBuilder -> {
                    nexusAuthRepository.batchInsert(nexusAuthList);
                    prodUserService.saveMultiUser(prodUserList);
                    startSagaBuilder.withPayloadAndSerialize(nexusAuthList).withSourceId(projectId);
                });
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_UPDATE, content = "%s 更新 %s 【%s】仓库的权限角色为 【%s】,过期日期为【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void update(NexusAuth nexusAuth) {

        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if (existAuth == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (NexusConstants.Flag.Y.equals(existAuth.getLocked())) {
            throw new CommonException(NexusMessageConstants.NEXUS_AUTH_OWNER_NOT_UPDATE);
        }

        // 设置并返回当前nexus服务信息
        configService.setNexusInfoByRepositoryId(nexusClient, existAuth.getRepositoryId());


        // 校验
        List<String> validateRoleCode = new ArrayList<>();
        validateRoleCode.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        this.validateRoleAuth(existAuth.getRepositoryId(), validateRoleCode);
        // 仓库角色查询
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, existAuth.getRepositoryId()).stream().findFirst().orElse(null);
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);
        nexusAuthRepository.updateOptional(nexusAuth, NexusAuth.FIELD_ROLE_CODE, NexusAuth.FIELD_END_DATE, NexusAuth.FIELD_NE_ROLE_ID);


        ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID, existAuth.getUserId()).stream().findFirst().orElse(null);
        if (prodUser == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        String password = null;
        if (prodUser.getPwdUpdateFlag() == 1) {
            password = DESEncryptUtil.decode(prodUser.getPassword());
        } else {
            password = prodUser.getPassword();
        }
        // 创建用户
        NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), password, Collections.singletonList(nexusAuth.getNeRoleId()));
        nexusApiService.createAndUpdateUser(nexusServerUser, Collections.singletonList(nexusAuth.getNeRoleId()), Collections.singletonList(existAuth.getNeRoleId()));

        nexusClient.removeNexusServerInfo();
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_DELETE, content = "%s 删除 %s 【%s】仓库的权限角色 【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void delete(NexusAuth nexusAuth) {

        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if (existAuth == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (NexusConstants.Flag.Y.equals(existAuth.getLocked())) {
            throw new CommonException(NexusMessageConstants.NEXUS_AUTH_OWNER_NOT_DELETE);
        }
        // 设置并返回当前nexus服务信息
        configService.setNexusInfoByRepositoryId(nexusClient, existAuth.getRepositoryId());

        // 校验
        List<String> validateRoleCode = new ArrayList<>();
        validateRoleCode.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        this.validateRoleAuth(existAuth.getRepositoryId(), validateRoleCode);

        this.deleteNexusServerAuth(nexusAuth);

        nexusClient.removeNexusServerInfo();
    }

    @Override
    public void validateRoleAuth(Long repositoryId, List<String> validateRoleCode) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        NexusAuth query = new NexusAuth();
        query.setRepositoryId(repositoryId);
        query.setUserId(userDetails.getUserId());
        List<NexusAuth> nexusAuthList = nexusAuthRepository.select(query);
        List<String> roleCodeList = nexusAuthList.stream().map(NexusAuth::getRoleCode).collect(Collectors.toList());

        boolean flag = false;
        for (String role : validateRoleCode) {
            if (roleCodeList.contains(role)) {
                // 当前用户有该权限角色
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new CommonException(NexusMessageConstants.NEXUS_USER_FORBIDDEN);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<NexusAuth> createNexusAuth(List<Long> userIds, Long repositoryId, String roleCode) {
        List<NexusAuth> nexusAuthList = new ArrayList<>();
        userIds.forEach(userId -> {
            NexusAuth nexusAuth = new NexusAuth();
            nexusAuth.setUserId(userId);
            nexusAuth.setRepositoryId(repositoryId);
            nexusAuth.setRoleCode(roleCode);
            nexusAuth.setLocked(NexusConstants.Flag.Y);
            nexusAuthList.add(nexusAuth);
        });
        /// 设置用户权限、获取用户信息
        List<ProdUser> prodUserList = new ArrayList<>();
        this.setAuthInfoAndProdUser(nexusAuthList, prodUserList);

        // 数据保存
        nexusAuthRepository.batchInsert(nexusAuthList);
        prodUserService.saveMultiUser(prodUserList);
        return nexusAuthList;
    }

    @Override
    public void expiredBatchNexusAuth() {
        // 数据查询
        Condition condition = new Condition(NexusAuth.class);
        condition.createCriteria().andLessThanOrEqualTo(NexusAuth.FIELD_END_DATE, new Date());
        List<NexusAuth> nexusAuthList = nexusAuthRepository.selectByCondition(condition);

        nexusAuthList.forEach(nexusAuth -> {
            try {
                self().expiredNexusAuth(nexusAuth);
            } catch (Exception e) {
                logger.error("expired nexus auth error, authId: " + nexusAuth.getAuthId(), e);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void expiredNexusAuth(NexusAuth nexusAuth) {
        configService.setNexusInfoByRepositoryId(nexusClient, nexusAuth.getRepositoryId());
        if (!NexusConstants.Flag.Y.equals(nexusAuth.getLocked())) {
            this.deleteNexusServerAuth(nexusAuth);
        }
        nexusClient.removeNexusServerInfo();
    }

    @Override
    public void deleteNexusServerAuth(NexusAuth nexusAuth) {
        nexusAuthRepository.deleteByPrimaryKey(nexusAuth);
        nexusApiService.updateUser(nexusAuth.getLoginName(), new ArrayList<>(), Collections.singletonList(nexusAuth.getNeRoleId()));
    }


    /**
     * 设置用户权限与用信息
     * @param nexusAuthList 权限信息
     * @param prodUserList 用户信息
     */
    private void setAuthInfoAndProdUser(List<NexusAuth> nexusAuthList, List<ProdUser> prodUserList) {
        if (CollectionUtils.isEmpty(nexusAuthList)) {
            return;
        }
        List<Long> repositoryIds = nexusAuthList.stream().map(NexusAuth::getRepositoryId).distinct().collect(Collectors.toList());
        if (repositoryIds.size() > 1) {
            throw new CommonException(NexusMessageConstants.NEXUS_AUTH_REPOSITORY_ID_IS_NOT_UNIQUE);
        }
        Long repositoryId = repositoryIds.get(0);
        NexusRepository nexusRepository = nexusRepositoryRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId).stream().findFirst().orElse(null);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        // 仓库角色查询
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, repositoryId).stream().findFirst().orElse(null);

        //校验是否已分配权限
        List<NexusAuth> existList = nexusAuthRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId);
        Map<Long, NexusAuth> nexusAuthMap = existList.stream().collect(Collectors.toMap(NexusAuth::getUserId, dto->dto));

        //设置loginName、realName
        ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(nexusAuthList.stream().map(NexusAuth::getUserId).distinct().toArray(Long[]::new),true);
        Map<Long,UserDTO> userDtoMap = userDtoResponseEntity == null ? new HashMap<>(2) : Objects.requireNonNull(userDtoResponseEntity.getBody()).stream().collect(Collectors.toMap(UserDTO::getId, dto->dto));

        //List<ProdUser> prodUserList = new ArrayList<>();

        nexusAuthList.forEach(nexusAuth -> {
            UserDTO userDTO = userDtoMap.get(nexusAuth.getUserId());
            nexusAuth.setLoginName(userDTO == null ? null : userDTO.getLoginName());
            nexusAuth.setRealName(userDTO == null ? null : userDTO.getRealName());

            if (nexusAuth.getLoginName() == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }

            NexusAuth existAuth = nexusAuthMap.get(nexusAuth.getUserId());
            if (existAuth != null) {
                throw new CommonException(NexusMessageConstants.NEXUS_AUTH_ALREADY_EXIST, existAuth.getRealName());
            }

            nexusAuth.setProjectId(nexusRepository.getProjectId());
            nexusAuth.setOrganizationId(nexusRepository.getOrganizationId());
            // 设置角色
            nexusAuth.setNeRoleIdByRoleCode(nexusRole);

            String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
            ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password,0);
            prodUserList.add(prodUser);
        });
    }
}
