package org.hrds.rdupm.nexus.client.nexus.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public interface NexusUrlConstants {

	// TODO 如何配置，暂时处理
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
		 * 创建maven仓库
		 */
		String CREATE_MAVEN_HOSTED_REPOSITORY = "/service/rest/beta/repositories/maven/hosted";
		/**
		 * 更新maven仓库
		 */
		String UPDATE_MAVEN_HOSTED_REPOSITORY = "/service/rest/beta/repositories/maven/hosted/";
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
}
