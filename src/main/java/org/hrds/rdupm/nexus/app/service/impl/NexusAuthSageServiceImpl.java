package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.*;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 删除成员， nexus服务， 权限处理
 *
 * @author weisen.yang@hand-china.com 2020-03-27
 */
@Service
public class NexusAuthSageServiceImpl implements NexusAuthSageService, AopProxy<NexusAuthSageService> {
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private NexusServerConfigService nexusServerConfigService;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private C7nBaseService c7nBaseService;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private NexusRoleRepository nexusRoleRepository;
    @Autowired
    private NexusAuthService nexusAuthService;
    @Autowired
    private ProdUserService prodUserService;
    @Autowired
    private ProdUserRepository prodUserRepository;
    @Autowired
    private NexusApiService nexusApiService;


    @Saga(code = NexusSagaConstants.NexusAuthDeleteUserHandle.NEXUS_AUTH_DELETE_USER_HANDLE,
            description = "删除团队成员， nexus（maven/npm）仓库权限处理",
            inputSchemaClass = NexusRepositoryCreateDTO.class)
    public void handlerRepo(NexusRepository nexusRepository, Long userId) {
        nexusRepository.setDeleteUserId(userId);
        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusAuthDeleteUserHandle.NEXUS_AUTH_DELETE_USER_HANDLE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("nexusAuthDeleteUser")
                        .withSourceId(nexusRepository.getProjectId()),
                builder -> {
                    builder.withPayloadAndSerialize(nexusRepository)
                            .withRefId(String.valueOf(nexusRepository.getRepositoryId()))
                            .withSourceId(nexusRepository.getProjectId());
                });


    }

    @Transactional(rollbackFor = Exception.class)
    public void handlerRepoAuth(NexusRepository nexusRepository) {
        NexusServerConfig serverConfig = nexusServerConfigService.setNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        try {
            this.createNewOwner(nexusRepository);
            //删除权限角色
            nexusAuthRepository.selectByCondition(Condition.builder(NexusAuth.class)
                    .where(Sqls.custom()
                            .andEqualTo(NexusAuth.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId())
                            .andEqualTo(NexusAuth.FIELD_USER_ID, nexusRepository.getDeleteUserId())
                    ).build()).stream().findFirst().ifPresent(this::deleteAuth);
        } finally {
            nexusClient.removeNexusServerInfo();
        }
    }


    public void createNewOwner(NexusRepository nexusRepository) {
        //为当前的这个仓库创建一个新的仓库管理员，新的仓库管理员从项目下的项目所有者中挑选一位
        Long projectId = nexusRepository.getProjectId();
        //可能为空
        Map<Long, UserDTO> userDTOMap = c7nBaseService.listProjectOwnerById(projectId);
        List<NexusAuth> nexusAuthList = new ArrayList<>();
        Long repositoryId = nexusRepository.getRepositoryId();
        //查询权限列表中属于项目所有者的信息
        if (!MapUtils.isEmpty(userDTOMap)) {
            nexusAuthList = nexusAuthRepository.selectByCondition(Condition.builder(NexusAuth.class)
                    .where(Sqls.custom()
                            .andEqualTo(NexusAuth.FIELD_REPOSITORY_ID, repositoryId)
                            .andIn(NexusAuth.FIELD_USER_ID, userDTOMap.keySet())
                            .andNotEqualTo(NexusAuth.FIELD_USER_ID, nexusRepository.getDeleteUserId())
                    ).build());
        }

        //无项目所有者权限，则创建 可能为空
        UserDTO userDTO = c7nBaseService.getProjectOwnerById(projectId);
        if (CollectionUtils.isEmpty(nexusAuthList) && !Objects.isNull(userDTO)) {
            saveAuth(nexusRepository, userDTO);
        } else {
            //有项目所有者权限，但没有仓库管理员，则选择其中一个所有者进行更新
            List<NexusAuth> filterList = nexusAuthList.stream().filter(dto -> NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode().equals(dto.getRoleCode()) && !dto.getUserId().equals(nexusRepository.getDeleteUserId())).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(filterList) && !CollectionUtils.isEmpty(nexusAuthList) && !Objects.isNull(nexusAuthList.get(0))) {
                updateAuth(nexusRepository, nexusAuthList.get(0));
            }
        }
    }

    private void saveAuth(NexusRepository nexusRepository, UserDTO userDTO) {

        // 仓库角色查询
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()).stream().findFirst().orElse(null);

        //设置权限信息
        NexusAuth nexusAuth = new NexusAuth();
        nexusAuth.setUserId(userDTO.getId());
        nexusAuth.setRepositoryId(nexusRepository.getRepositoryId());
        nexusAuth.setLoginName(userDTO.getLoginName());
        nexusAuth.setRealName(userDTO.getRealName());
        nexusAuth.setProjectId(nexusRepository.getProjectId());
        nexusAuth.setOrganizationId(nexusRepository.getOrganizationId());
        nexusAuth.setRoleCode(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        nexusAuth.setLocked(NexusConstants.Flag.Y);
        // 设置角色
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);

        //创建账号
        String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
        ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password, 0);
        ProdUser dbProdUser = prodUserService.saveOneUser(prodUser);
        String newPassword = dbProdUser.getPwdUpdateFlag() == 1 ? DESEncryptUtil.decode(dbProdUser.getPassword()) : dbProdUser.getPassword();


        nexusAuthRepository.insertSelective(nexusAuth);

        NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), newPassword, Collections.singletonList(nexusAuth.getNeRoleId()));
        nexusApiService.createAndUpdateUser(nexusServerUser, Collections.singletonList(nexusAuth.getNeRoleId()), new ArrayList<>());
    }

    private void updateAuth(NexusRepository nexusRepository, NexusAuth nexusAuth) {
        if (Objects.isNull(nexusAuth)) {
            return;
        }
        String oldRoleCode = nexusAuth.getRoleCode();
        String oldNeRoleId = nexusAuth.getNeRoleId();

        nexusAuth.setRoleCode(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, nexusAuth.getRepositoryId()).stream().findFirst().orElse(null);
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);
        nexusAuth.setEndDate(null);
        nexusAuth.setLocked(NexusConstants.Flag.Y);
        nexusAuthRepository.updateOptional(nexusAuth, NexusAuth.FIELD_ROLE_CODE, NexusAuth.FIELD_END_DATE,
                NexusAuth.FIELD_NE_ROLE_ID, NexusAuth.FIELD_LOCKED);


        ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID, nexusAuth.getUserId()).stream().findFirst().orElse(null);
        String password = null;
        if (prodUser.getPwdUpdateFlag() == 1) {
            password = DESEncryptUtil.decode(prodUser.getPassword());
        } else {
            password = prodUser.getPassword();
        }
        // 创建用户
        NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), password, Collections.singletonList(nexusAuth.getNeRoleId()));
        nexusApiService.createAndUpdateUser(nexusServerUser, Collections.singletonList(nexusAuth.getNeRoleId()), Collections.singletonList(oldNeRoleId));
    }

    private void deleteAuth(NexusAuth nexusAuth) {
        nexusAuthService.deleteNexusServerAuth(nexusAuth);
    }
}
