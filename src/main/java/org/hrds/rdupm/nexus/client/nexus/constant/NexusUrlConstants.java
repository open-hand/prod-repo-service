package org.hrds.rdupm.nexus.client.nexus.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public interface NexusUrlConstants {


	interface  Nexus {
		String NEXUS_VERSION = "/service/rest/swagger.json";

		String NEXUS_VERSION_BASIC = "3.21.0";
		String NEXUS_VERSION_INFO = "info";
		String NEXUS_VERSION_INFO_VERSION = "version";

	}

	/**
	 * 仓库相关API
	 */
	interface Repository {
		/**
		 * 仓库信息查询
		 */
		String GET_REPOSITORY_LIST = "/service/rest/v1/repositories";

		/**
		 * 仓库信息查询
		 */
		String GET_REPOSITORY_MANAGE_LIST = "/service/rest/beta/repositories";
		/**
		 * 删除仓库
		 */
		String DELETE_REPOSITORY = "/service/rest/beta/repositories/";
		/**
		 * 创建maven hosted仓库
		 */
		String CREATE_MAVEN_HOSTED_REPOSITORY = "/service/rest/beta/repositories/maven/hosted";
		/**
		 * 更新maven hosted仓库
		 */
		String UPDATE_MAVEN_HOSTED_REPOSITORY = "/service/rest/beta/repositories/maven/hosted/";

		/**
		 * 创建maven proxy仓库
		 */
		String CREATE_MAVEN_PROXY_REPOSITORY = "/service/rest/beta/repositories/maven/proxy";
		/**
		 * 更新maven proxy仓库
		 */
		String UPDATE_MAVEN_PROXY_REPOSITORY = "/service/rest/beta/repositories/maven/proxy/";
	}

	/**
	 * 组件相关API
	 */
	interface Components {
		/**
		 * 组件信息查询
		 */
		String GET_COMPONENTS_LIST = "/service/rest/v1/components";

		/**
		 * 删除组件
		 */
		String DELETE_COMPONENTS = "/service/rest/v1/components/";

		/**
		 * 上传组件
		 */
		String UPLOAD_COMPONENTS = "/service/rest/v1/components";
	}

	/**
	 * 查询相关API
	 */
	interface Search {
		/**
		 * 组件（包）信息查询
		 */
		String SEARCH_COMPONENT = "/service/rest/v1/search";
	}

	/**
	 * 权限相关API
	 */
	interface Privileges {
		/**
		 * 权限信息查询
		 */
		String GET_PRIVILEGES_LIST = "/service/rest/beta/security/privileges";
	}

	/**
	 * 存储相关API
	 */
	interface BlobStore {
		/**
		 * 列表查询
		 */
		String GET_BLOB_STORE_LIST = "/service/rest/beta/blobstores";
	}

	/**
	 * 角色相关API
	 */
	interface Role {
		/**
		 * 列表查询
		 */
		String GET_ROLE_LIST = "/service/rest/beta/security/roles";

		/**
		 * 信息查询
		 */
		String GET_ROLE_BY_ID = "/service/rest/beta/security/roles/";
		/**
		 * 删除角色
		 */
		String DELETE_ROLE = "/service/rest/beta/security/roles/";
		/**
		 * 创建角色
		 */
		String CREATE_ROLE = "/service/rest/beta/security/roles";
		/**
		 * 更新角色
		 */
		String UPDATE_ROLE = "/service/rest/beta/security/roles/";
	}

	/**
	 * 用户相关API
	 */
	interface User {
		/**
		 * 列表查询
		 */
		String GET_USER_LIST = "/service/rest/beta/security/users";
		/**
		 * 删除用户
		 */
		String DELETE_USER = "/service/rest/beta/security/users/";
		/**
		 * 创建用户
		 */
		String CREATE_USER = "/service/rest/beta/security/users";
		/**
		 * 更新用户
		 */
		String UPDATE_USER = "/service/rest/beta/security/users/";
		/**
		 * 密码更改
		 */
		String CHANGE_PSW = "/service/rest/beta/security/users/{userId}/change-password";

	}

	/**
	 * 脚本相关API
	 */
	interface Script {

		String GET_SCRIPT_BY_NAME = "/service/rest/v1/script/";
		/**
		 * 脚本上传
		 */
		String UPLOAD_SCRIPT = "/service/rest/v1/script";
		/**
		 * 脚本更新
		 */
		String UPDATE_SCRIPT = "/service/rest/v1/script/{scriptName}";
		/**
		 * 脚本执行
		 */
		String RUN_SCRIPT = "/service/rest/v1/script/{scriptName}/run";
		/**
		 * 脚本删除
		 */
		String DELETE_SCRIPT = "/service/rest/v1/script/{scriptName}";
	}
}
