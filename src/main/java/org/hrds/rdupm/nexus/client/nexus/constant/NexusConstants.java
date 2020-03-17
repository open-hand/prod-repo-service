package org.hrds.rdupm.nexus.client.nexus.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusConstants {

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
}
