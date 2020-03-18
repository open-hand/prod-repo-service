package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusUser;

import java.util.List;

/**
 * nexus 用户API
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public interface NexusUserApi {
	/**
	 * 获取nexus服务,用户信息
	 * @param userId 用户ID 不必传
	 * @return List<NexusUser>
	 */
	List<NexusUser> getUsers(String userId);

	/**
	 * 删除用户信息
	 * @param userId 角色Id
	 */
	void deleteUser(String userId);

	/**
	 * 用户创建
	 * @param nexusUser 创建信息
	 */
	void createUser(NexusUser nexusUser);

	/**
	 * 用户更新
	 * @param nexusUser 更新信息
	 */
	void updateUser(NexusUser nexusUser);

	/**
	 * 用户更新角色密码
	 * @param userId 用户Id
	 * @param newPassword 新密码
	 * @param oldPassword 旧密码
	 */
	void changePassword(String userId, String newPassword, String oldPassword);
}
