package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;

import java.util.List;

/**
 * nexus 用户API
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public interface NexusUserApi {
	/**
	 * 获取nexus服务,用户信息
	 * @param userId 用户ID 不必传
	 * @return NexusServerUser
	 */
	NexusServerUser getUsers(String userId);

	/**
	 * 删除用户信息
	 * @param userId 角色Id
	 */
	void deleteUser(String userId);

	/**
	 * 用户创建
	 * @param nexusUser 创建信息
	 */
	void createUser(NexusServerUser nexusUser);

	/**
	 * 用户更新
	 * @param nexusUser 更新信息
	 */
	void updateUser(NexusServerUser nexusUser);

	/**
	 * 用户更新角色密码
	 * @param userId 用户Id
	 * @param newPassword 新密码
	 */
	void changePassword(String userId, String newPassword);

	/**
	 * 校验改用户是否有仓库对应发布权限
	 * @param repositoryList 仓库名列表
	 * @param userName 用户名称（ID）
	 * @param ruleList 发布权限规则列表,一组规则有多个时用逗号隔开
	 *                  如：nx-repository-view-*-*-*    nx-repository-view-*-*-add,nx-repository-view-*-*-edit
	 * @return 返回repositoryList中该用户有发布权限的仓库
	 */
	List<String> validPush(List<String> repositoryList, String userName, List<String> ruleList);

	/**
	 * 校验用户与用户名是否正确
	 * @param userName 用户名
	 * @param password 密码
	 * @param currentNexusServer 当前服务配置
	 * @return true:正确  false:错误
	 */
	Boolean validUserNameAndPassword(String userName, String password, NexusServer currentNexusServer);
}
