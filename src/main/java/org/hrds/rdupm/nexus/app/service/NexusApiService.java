package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;

import java.util.List;

/**
 * nexus服务,api调用处理
 * @author weisen.yang@hand-china.com
 */
public interface NexusApiService {

    /**
     * 用户更新角色
     * @param neUserId nexus服务用户Id
     * @param addRoles 新加的角色信息
     * @param deleteRoles 删除的角色信息
     */
    void updateUser(String neUserId, List<String> addRoles, List<String> deleteRoles);

    /**
     * 用户创建与更新， 用户不存在则创建: 使用nexusUser。 存在则更新角色：使用addRoles、deleteRoles
     * @param nexusUser nexus服务用户信息
     * @param addRoles 新加的角色信息
     * @param deleteRoles 删除的角色信息
     */
    void createAndUpdateUser(NexusServerUser nexusUser, List<String> addRoles, List<String> deleteRoles);

    /**
     * 用户更新角色
     * @param roleId nexus服务角色Id
     * @param addPrivileges 新加的权限信息
     * @param deletePrivileges 删除的权限信息
     */
    void updateRole(String roleId, List<String> addPrivileges, List<String> deletePrivileges);
}
