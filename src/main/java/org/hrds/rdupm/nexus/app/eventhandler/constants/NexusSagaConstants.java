package org.hrds.rdupm.nexus.app.eventhandler.constants;

/**
 * saga 常量配置
 * @author weisen.yang@hand-china.com 2020/4/8
 */
public interface NexusSagaConstants {

	interface NexusMavenRepoCreate {
		/**
		 * 创建maven仓库   Saga code
		 */
		String MAVEN_REPO_CREATE = "rdupm-maven-create";
		/**
		 * 创建maven仓库: 创建nexus server仓库   SagaTask code
		 */
		String MAVEN_REPO_CREATE_REPO = "rdupm-maven-create.repo";
		int REPO_SEQ = 1;
		/**
		 * 创建maven仓库：创建角色   SagaTask code
		 */
		String MAVEN_REPO_CREATE_ROLE = "rdupm-maven-create.role";
		int ROLE_SEQ = 2;
		/**
		 * 创建maven仓库：创建用户   SagaTask code
		 */
		String MAVEN_REPO_CREATE_USER = "rdupm-maven-create.user";
		int USER_SEQ = 3;
	}

	interface NexusRepoDistribute {
		/**
		 * 平台层-仓库分配
		 */
		String SITE_NEXUS_REPO_DISTRIBUTE = "rdupm-nexus-repo-distribute";
		/**
		 * 平台层-仓库分配: 创建角色
		 */
		String SITE_NEXUS_REPO_DISTRIBUTE_ROLE = "rdupm-nexus-repo-distribute.role";

		/**
		 * 平台层-仓库分配: 创建用户
		 */
		String SITE_NEXUS_REPO_DISTRIBUTE_USER = "rdupm-nexus-repo-distribute.user";
	}

	interface NexusMavenRepoUpdate {
		/**
		 * 更新maven仓库   Saga code
		 */
		String MAVEN_REPO_UPDATE = "rdupm-maven-update";
		/**
		 * 更新maven仓库: 更新nexus server仓库   SagaTask code
		 */
		String MAVEN_REPO_UPDATE_REPO = "rdupm-maven-update.repo";
	}

	interface NexusMavenRepoDelete {
		/**
		 * 删除maven仓库   Saga code
		 */
		String MAVEN_REPO_DELETE = "rdupm-maven-delete";
		/**
		 * 删除maven仓库   SagaTask code
		 */
		String MAVEN_REPO_DELETE_REPO = "rdupm-maven-delete.repo";
	}

	interface NexusMavenRepoRelated {
		/**
		 * 关联maven仓库   Saga code
		 */
		String MAVEN_REPO_RELATED = "rdupm-maven-related";
		/**
		 * 关联maven仓库   SagaTask code
		 */
		String MAVEN_REPO_RELATED_REPO = "rdupm-maven-related.repo";
	}

	interface NexusAuthCreate {
		/**
		 * 分配权限  Saga code
		 */
		String NEXUS_AUTH_CREATE = "rdupm-maven-auth-create";
		/**
		 * 分配权限  SagaTask code
		 */
		String NEXUS_AUTH_CREATE_USER = "rdupm-maven-auth-create.user";
	}
	interface NexusAuthDelete {
		/**
		 * 删除权限  Saga code
		 */
		String NEXUS_AUTH_DELETE = "rdupm-maven-auth-delete";
		/**
		 * 删除权限  SagaTask code
		 */
		String NEXUS_AUTH_DELETE_USER = "rdupm-maven-auth-delete.user";
	}
}
