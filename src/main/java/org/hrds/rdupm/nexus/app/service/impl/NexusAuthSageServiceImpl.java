package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusAuthSageService;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.annotation.NexusOperateLog;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 删除成员， nexus服务， 权限处理
 * @author weisen.yang@hand-china.com 2020-03-27
 */
@Service
public class NexusAuthSageServiceImpl implements NexusAuthSageService, AopProxy<NexusRepositoryService> {
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

        Long projectId = nexusRepository.getProjectId();
        Map<Long, UserDTO> userDTOMap = c7nBaseService.listProjectOwnerById(projectId);

        //查询权限列表中属于项目所有者的信息
        Long repositoryId = nexusRepository.getRepositoryId();
        List<NexusAuth> nexusAuthList = nexusAuthRepository.selectByCondition(Condition.builder(NexusAuth.class)
                .where(Sqls.custom()
                        .andEqualTo(NexusAuth.FIELD_REPOSITORY_ID, repositoryId)
                        .andIn(NexusAuth.FIELD_USER_ID, userDTOMap.keySet())
                        .andNotEqualTo(NexusAuth.FIELD_USER_ID, nexusRepository.getDeleteUserId())
                ).build());
        //无项目所有者权限，则创建
        UserDTO userDTO = c7nBaseService.getProjectOwnerById(projectId);
        if (CollectionUtils.isEmpty(nexusAuthList)) {
            saveAuth(nexusRepository, userDTO);
        } else {
            //有项目所有者权限，但没有仓库管理员，则选择其中一个所有者进行更新
            List<NexusAuth> filterList = nexusAuthList.stream().filter(dto-> NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode().equals(dto.getRoleCode()) && !dto.getUserId().equals(nexusRepository.getDeleteUserId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filterList)) {
                updateAuth(nexusRepository, nexusAuthList.get(0));
            }
        }
    }

    public void saveAuth(NexusRepository nexusRepository, UserDTO userDTO) {

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
        // 设置角色
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);

        //创建账号
        String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
        ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password,0);
        ProdUser dbProdUser = prodUserService.saveOneUser(prodUser);
        String newPassword = dbProdUser.getPwdUpdateFlag() == 1 ? DESEncryptUtil.decode(dbProdUser.getPassword()) : dbProdUser.getPassword();


        nexusAuthRepository.insertSelective(nexusAuth);

        List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
        if (CollectionUtils.isEmpty(existUserList)) {
            // 创建用户
            NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), newPassword, Collections.singletonList(nexusAuth.getNeRoleId()));
            nexusClient.getNexusUserApi().createUser(nexusServerUser);
        } else {
            // 更新用户
            NexusServerUser nexusServerUser = existUserList.get(0);
            nexusServerUser.getRoles().add(nexusAuth.getNeRoleId());
            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        }
    }

    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_UPDATE, content = "%s 更新 %s 【%s】仓库的权限角色为 【%s】,过期日期为【%s】")
    private void updateAuth(NexusRepository nexusRepository, NexusAuth nexusAuth) {
        String oldRoleCode = nexusAuth.getRoleCode();
        String oldNeRoleId = nexusAuth.getNeRoleId();

        nexusAuth.setRoleCode(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, nexusAuth.getRepositoryId()).stream().findFirst().orElse(null);
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);
        nexusAuth.setEndDate(null);
        nexusAuthRepository.updateOptional(nexusAuth, NexusAuth.FIELD_ROLE_CODE, NexusAuth.FIELD_END_DATE, NexusAuth.FIELD_NE_ROLE_ID);

        List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
        if (CollectionUtils.isNotEmpty(existUserList)) {
            // 更新用户
            NexusServerUser nexusServerUser = existUserList.get(0);
            // 删除旧角色
            nexusServerUser.getRoles().remove(oldNeRoleId);
            // 添加新角色
            nexusServerUser.getRoles().add(nexusAuth.getNeRoleId());

            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        } else {
            ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_USER_ID, nexusAuth.getUserId()).stream().findFirst().orElse(null);
            String password = null;
            if (prodUser.getPwdUpdateFlag() == 1) {
                password = DESEncryptUtil.decode(prodUser.getPassword());
            } else {
                password = prodUser.getPassword();
            }
            // 创建用户
            NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), password, Collections.singletonList(nexusAuth.getNeRoleId()));
            nexusClient.getNexusUserApi().createUser(nexusServerUser);
        }
    }

    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_DELETE, content = "%s 删除 %s 【%s】仓库的权限角色 【%s】")
    private void deleteAuth(NexusAuth nexusAuth) {
        nexusAuthService.deleteNexusServerAuth(nexusAuth);
    }
}
