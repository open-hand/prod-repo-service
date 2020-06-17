package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.service.NexusApiService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hzero.core.base.AopProxy;
import org.hzero.lock.annotation.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * nexus服务,api调用处理
 * @author weisen.yang@hand-china.com
 */
@Service
public class NexusApiServiceImpl implements NexusApiService, AopProxy<NexusApiService>  {

    @Autowired
    private NexusClient nexusClient;

    @Override
    @Lock(keys = {"#nexusUser.userId"})
    public void updateUser(String neUserId, List<String> addRoles, List<String> deleteRoles) {

        NexusServerUser existUser = nexusClient.getNexusUserApi().getUsers(neUserId);
        if (existUser != null) {
            if (CollectionUtils.isNotEmpty(deleteRoles)) {
                existUser.getRoles().removeAll(deleteRoles);
            }
            if (CollectionUtils.isNotEmpty(addRoles)) {
                existUser.getRoles().addAll(addRoles);
            }
            if (CollectionUtils.isEmpty(existUser.getRoles())) {
                // 为空时，给默认值
                existUser.getRoles().add(NexusApiConstants.defaultRole.DEFAULT_ROLE);
            }
            nexusClient.getNexusUserApi().updateUser(existUser);
        }

    }

    @Override
    @Lock(keys = {"#nexusUser.userId"})
    public void createAndUpdateUser(NexusServerUser nexusUser, List<String> addRoles, List<String> deleteRoles) {
        NexusServerUser existUser = nexusClient.getNexusUserApi().getUsers(nexusUser.getUserId());
        if (existUser == null) {
            // 创建用户
            nexusClient.getNexusUserApi().createUser(nexusUser);
        } else {
            // 更新用户
            self().updateUser(nexusUser.getUserId(), addRoles, deleteRoles);
        }
    }

    @Override
    @Lock(keys = {"#nexusUser.userId"})
    public void updateRole(String roleId, List<String> addPrivileges, List<String> deletePrivileges) {
        NexusServerRole existRole = nexusClient.getNexusRoleApi().getRoleById(roleId);
        if (existRole == null) {
            throw new CommonException("role not found:" + roleId);
        }
        if (CollectionUtils.isNotEmpty(deletePrivileges)) {
            existRole.getPrivileges().removeAll(deletePrivileges);
        }
        if (CollectionUtils.isNotEmpty(addPrivileges)) {
            existRole.getPrivileges().addAll(addPrivileges);
        }
        nexusClient.getNexusRoleApi().updateRole(existRole);
    }
}
