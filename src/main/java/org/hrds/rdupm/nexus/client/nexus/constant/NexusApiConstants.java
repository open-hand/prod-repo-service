package org.hrds.rdupm.nexus.client.nexus.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusApiConstants {

	/**
	 * 仓库类型
	 */
	interface RepositoryType {
		/**
		 * 本地仓库
		 */
		String HOSTED = "hosted";
		/**
		 * 代理仓库
		 */
		String PROXY = "proxy";
		/**
		 * 仓库组
		 */
		String GROUP = "group";
	}


	interface ErrorMessage {
		/**
		 * 组件Id错误
		 */
		String COMPONENT_ID_ERROR = "error.nexus.client.component";
		/**
		 * 仓库类型错误
		 */
		String REPO_TYPE_ERROR = "error.nexus.client.repo.type";
		/**
		 * 仓库名，对应仓库已存在
		 */
		String REPO_NAME_EXIST = "error.nexus.client.repo.name.exist";

		/**
		 * 角色ID对应角色已存在
		 */
		String ROLE_EXIST = "error.nexus.client.role.exist";
		/**
		 * 用户ID对应用户已存在
		 */
		String USER_EXIST = "error.nexus.client.user.exist";
		/**
		 * nexus服务信息未配置
		 */
		String NEXUS_INFO_NOT_CONF = "error.nexus.client.nexus.not.conf";

		/**
		 * nexus用户名或密码错误
		 */
		String NEXUS_USER_PASS_ERROR = "error.nexus.client.nexus.user.pass.failed";
		/**
		 * nexus角色对应操作权限未分配
		 */
		String NEXUS_ROLE_PRI_NOT_ASSIGNED = "error.nexus.client.nexus.role.pri.not.assigned";
		/**
		 * nexus服务不可用，请检查服务
		 */
		String NEXUS_SERVER_ERROR = "error.nexus.client.nexus.server.failed";

		/**
		 * 对应资源已不存在
		 */
		String RESOURCE_NOT_EXIST = "error.nexus.client.resource.not.exist";
	}
}
