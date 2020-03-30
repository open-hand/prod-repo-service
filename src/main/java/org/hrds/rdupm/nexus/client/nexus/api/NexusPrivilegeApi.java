package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerPrivilege;

import java.util.List;

/**
 * 权限API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusPrivilegeApi {

	/**
	 * 查询权限列表
	 * @return List<NexusPrivilege>
	 */
	List<NexusServerPrivilege> getPrivileges();

	/**
	 * 查询权限列表
	 * @param name 权限名称
	 * @return List<NexusPrivilege>
	 */
	List<NexusServerPrivilege> getPrivileges(String name);
}
