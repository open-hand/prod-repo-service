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
	}
}
