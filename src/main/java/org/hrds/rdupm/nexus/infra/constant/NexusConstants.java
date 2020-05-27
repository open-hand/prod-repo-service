package org.hrds.rdupm.nexus.infra.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/27
 */
public interface NexusConstants {

	interface Lookup {
		/**
		 * 仓库名称后缀
		 */
		String REPO_NAME_SUFFIX = "RDUPM.REPO_NAME_SUFFIX";
	}

	/**
	 * 仓库列表查询，数据分类
	 */
	interface RepoQueryData {
		/**
		 * 查询所有仓库信息
		 */
		String REPO_ALL = "all";

		/**
		 * 排除当前项目创建或关联的仓库后，仓库信息
		 */
		String REPO_EXCLUDE_PROJECT = "exclude_project";
		/**
		 * 查询当前项目下创建或关联的仓库信息
		 */
		String REPO_PROJECT = "project";

		/**
		 * 查询当前组织下的仓库信息
		 */
		String REPO_ORG = "org";
	}

	/**
	 * 制品库-仓库类型
	 */
	interface RepoType {
		String MAVEN = "MAVEN";
		String NPM = "NPM";
	}


}
