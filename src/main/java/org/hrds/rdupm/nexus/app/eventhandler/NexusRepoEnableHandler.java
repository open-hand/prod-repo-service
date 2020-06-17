package org.hrds.rdupm.nexus.app.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import net.bytebuddy.asm.Advice;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusApiService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    @Autowired
    private NexusApiService nexusApiService;

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
     * 仓库失效
     * @param nexusRepository 参数
     */
    private void nexusRepoDisAble(NexusRepository nexusRepository, NexusServerConfig serverConfig) {
        GetNexusUserAndRole getNexusUserAndRole = new GetNexusUserAndRole(nexusRepository).invoke();
        List<NexusAuth> nexusAuthList = getNexusUserAndRole.getNexusAuthList();
        NexusUser nexusUser = getNexusUserAndRole.getNexusUser();
        NexusRole nexusRole = getNexusUserAndRole.getNexusRole();

        // 去除nexus用户对应角色权限
        nexusAuthList.forEach(nexusAuth -> {
            nexusApiService.updateUser(nexusAuth.getLoginName(), new ArrayList<>(), Collections.singletonList(nexusAuth.getNeRoleId()));
        });

        // 默认拉取用户, 删除角色
        nexusApiService.updateUser(nexusUser.getNePullUserId(), new ArrayList<>(), Collections.singletonList(nexusRole.getNePullRoleId()));

        // 默认发布用户， 删除角色
        nexusApiService.updateUser(nexusUser.getNeUserId(), new ArrayList<>(), Collections.singletonList(nexusRole.getNeRoleId()));

        if (serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
            // 不允许匿名
            List<String> privileges = NexusServerRole.getAnonymousPrivileges(nexusRepository.getNeRepositoryName(), nexusRepositoryService.convertRepoTypeToFormat(nexusRepository.getRepoType()));
            nexusApiService.updateRole(serverConfig.getAnonymousRole(), new ArrayList<>(), privileges);
        }
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

        // 添加nexus用户对应角色权限
        nexusAuthList.forEach(nexusAuth -> {
            nexusApiService.updateUser(nexusAuth.getLoginName(), Collections.singletonList(nexusAuth.getNeRoleId()), new ArrayList<>());
        });

        // 默认拉取用户, 添加角色
        nexusApiService.updateUser(nexusUser.getNePullUserId(), Collections.singletonList(nexusRole.getNePullRoleId()), new ArrayList<>());
        // 默认发布用户， 添加角色
        nexusApiService.updateUser(nexusUser.getNeUserId(), Collections.singletonList(nexusRole.getNeRoleId()), new ArrayList<>());

        // 设置用户匿名权限
        if (serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
            List<String> privileges = NexusServerRole.getAnonymousPrivileges(nexusRepository.getNeRepositoryName(), nexusRepositoryService.convertRepoTypeToFormat(nexusRepository.getRepoType()));
            if (nexusRepository.getAllowAnonymous().equals(BaseConstants.Flag.YES)) {
                nexusApiService.updateRole(serverConfig.getAnonymousRole(), privileges, new ArrayList<>());
            } else {
                nexusApiService.updateRole(serverConfig.getAnonymousRole(), new ArrayList<>(), privileges);
            }
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
