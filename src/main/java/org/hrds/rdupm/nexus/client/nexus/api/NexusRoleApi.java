package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;

import java.util.List;

/**
 * nexus 角色API
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public interface NexusRoleApi {
	/**
	 * 获取nexus服务,角色信息
	 * @return List<NexusRole>
	 */
	List<NexusServerRole> getRoles();

	/**
	 * 获取nexus服务,角色信息
	 * @param roleId 角色Id
	 * @return NexusRole
	 */
	NexusServerRole getRoleById(String roleId);

	/**
	 * 删除角色信息
	 * @param roleId 角色Id
	 */
	void deleteRole(String roleId);

	/**
	 * 角色创建
	 * @param nexusRole 创建信息
	 */
	void createRole(NexusServerRole nexusRole);

	/**
	 * 角色更新
	 * @param nexusRole 更新信息
	 */
	void updateRole(NexusServerRole nexusRole);
}
