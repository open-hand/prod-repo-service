package org.hrds.rdupm.nexus.app.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.*;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * nexus仓库失效/启用 处理类
 * @author weisen.yang@hand-china.com 2020/6/9
 */
@Component
public class NexusRepoEnableHandler {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusRepositoryService nexusRepositoryService;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusServerConfigService configService;
    @Autowired
    private NexusRoleRepository nexusRoleRepository;
    @Autowired
    private NexusUserRepository nexusUserRepository;

    @SagaTask(code = NexusSagaConstants.NexusRepoEnableAndDisable.NEXUS_REPO_ENABLE_AND_DISABLE_AUTH,
            description = "nexus仓库生效/失效",
            sagaCode = NexusSagaConstants.NexusRepoEnableAndDisable.NEXUS_REPO_ENABLE_AND_DISABLE,
            maxRetryCount = 3,
            seq = 1)
    public void nexusRepoEnableAndDisAble(String message) {

        NexusRepository nexusRepository = null;
        try {
            nexusRepository = objectMapper.readValue(message, NexusRepository.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }
        NexusRepository exist = nexusRepositoryRepository.selectByPrimaryKey(nexusRepository);
        if (exist == null) {
            throw new CommonException("nexus repository is not create, repoName is " + nexusRepository.getNeRepositoryName());
        }

        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, exist.getRepositoryId());

        if (exist.getEnableFlag().equals(NexusConstants.Flag.Y)) {
            // 生效
            this.nexusRepoEnable(exist, serverConfig);
        } else if (exist.getEnableFlag().equals(NexusConstants.Flag.N)) {
            // 失效
            this.nexusRepoDisAble(exist, serverConfig);
        }


        nexusClient.removeNexusServerInfo();
    }

    /**
     * 仓库生效
     * @param nexusRepository 参数
     */
    private void nexusRepoEnable(NexusRepository nexusRepository, NexusServerConfig serverConfig) {
        GetNexusUserAndRole getNexusUserAndRole = new GetNexusUserAndRole(nexusRepository).invoke();
        List<NexusAuth> nexusAuthList = getNexusUserAndRole.getNexusAuthList();
        NexusUser nexusUser = getNexusUserAndRole.getNexusUser();
        NexusRole nexusRole = getNexusUserAndRole.getNexusRole();

        // 去除nexus用户对应角色权限
        nexusAuthList.forEach(nexusAuth -> {
            List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
            if (CollectionUtils.isNotEmpty(existUserList)) {
                // 更新用户
                NexusServerUser nexusServerUser = existUserList.get(0);
                // 删除旧角色
                nexusServerUser.getRoles().remove(nexusAuth.getNeRoleId());
                nexusClient.getNexusUserApi().updateUser(nexusServerUser);
            }
        });

        // 默认拉取用户
        List<NexusServerUser> pullUserList = nexusClient.getNexusUserApi().getUsers(nexusUser.getNePullUserId());
        if (CollectionUtils.isNotEmpty(pullUserList)) {
            // 更新用户
            NexusServerUser nexusServerUser = pullUserList.get(0);
            // 删除旧角色
            nexusServerUser.getRoles().remove(nexusRole.getNePullRoleId());
            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        }

        // 不允许匿名
        NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
        if (anonymousRole == null) {
            throw new CommonException("default anonymous role not found:" + serverConfig.getAnonymousRole());
        }
        anonymousRole.setPullPri(nexusRepository.getNeRepositoryName(), 0, nexusRepositoryService.convertRepoTypeToFormat(nexusRepository.getRepoType()));
        nexusClient.getNexusRoleApi().updateRole(anonymousRole);

    }

    /**
     * 仓库失效
     * @param nexusRepository 参数
     */
    private void nexusRepoDisAble(NexusRepository nexusRepository, NexusServerConfig serverConfig) {
        GetNexusUserAndRole getNexusUserAndRole = new GetNexusUserAndRole(nexusRepository).invoke();
        List<NexusAuth> nexusAuthList = getNexusUserAndRole.getNexusAuthList();
        NexusUser nexusUser = getNexusUserAndRole.getNexusUser();
        NexusRole nexusRole = getNexusUserAndRole.getNexusRole();

        // 添加nexus用户对应角色权限
        nexusAuthList.forEach(nexusAuth -> {
            List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
            if (CollectionUtils.isEmpty(existUserList)) {
                // 更新用户
                NexusServerUser nexusServerUser = existUserList.get(0);
                nexusServerUser.getRoles().add(nexusAuth.getNeRoleId());
                nexusClient.getNexusUserApi().updateUser(nexusServerUser);
            }
        });

        // 默认拉取用户
        List<NexusServerUser> pullUserList = nexusClient.getNexusUserApi().getUsers(nexusUser.getNePullUserId());
        if (CollectionUtils.isNotEmpty(pullUserList)) {
            // 更新用户
            NexusServerUser nexusServerUser = pullUserList.get(0);
            // 添加角色
            nexusServerUser.getRoles().add(nexusRole.getNePullRoleId());
            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        }

        // 设置用户匿名权限
        if (serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
            NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
            if (anonymousRole == null) {
                throw new CommonException("default anonymous role not found:" + serverConfig.getAnonymousRole());
            }
            anonymousRole.setPullPri(nexusRepository.getNeRepositoryName(), nexusRepository.getAllowAnonymous(), nexusRepositoryService.convertRepoTypeToFormat(nexusRepository.getRepoType()));
            nexusClient.getNexusRoleApi().updateRole(anonymousRole);
        }

    }

    private class GetNexusUserAndRole {
        private NexusRepository nexusRepository;
        private NexusUser nexusUser;
        private NexusRole nexusRole;
        private List<NexusAuth> nexusAuthList;

        public GetNexusUserAndRole(NexusRepository nexusRepository) {
            this.nexusRepository = nexusRepository;
        }

        public NexusUser getNexusUser() {
            return nexusUser;
        }

        public NexusRole getNexusRole() {
            return nexusRole;
        }

        public List<NexusAuth> getNexusAuthList() {
            return nexusAuthList;
        }

        public GetNexusUserAndRole invoke() {
            nexusUser = nexusUserRepository.select(NexusUser.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()).stream().findFirst().orElse(null);
            if (nexusUser == null) {
                throw new CommonException("nexusUser is not find, repositoryId is " + nexusRepository.getRepositoryId());
            }
            nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()).stream().findFirst().orElse(null);
            if (nexusRole == null) {
                throw new CommonException("nexusRole is not find, repositoryId is " + nexusRepository.getRepositoryId());
            }
            nexusAuthList = nexusAuthRepository.select(NexusAuth.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId());
            return this;
        }
    }
}
