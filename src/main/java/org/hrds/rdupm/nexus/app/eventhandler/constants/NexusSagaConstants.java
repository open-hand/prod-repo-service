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

	interface NexusMavenRepoUpdate {
		/**
		 * 更新maven仓库   Saga code
		 */
		String MAVEN_REPO_UPDATE = "rdupm-maven-update";
		/**
		 * 更新maven仓库: 更新nexus server仓库   SagaTask code
		 */
		String MAVEN_REPO_UPDATE_REPO = "rdupm-maven-update.repo";
		int REPO_SEQ = 1;
	}
}
