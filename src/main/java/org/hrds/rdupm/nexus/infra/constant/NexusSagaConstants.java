package org.hrds.rdupm.nexus.infra.constant;

/**
 * saga 常量配置
 * @author weisen.yang@hand-china.com 2020/4/8
 */
public interface NexusSagaConstants {

	interface NexusMavenRepoCreate {
		/**
		 * 创建maven仓库   Saga code
		 */
		String MAVEN_REPO_CREATE = "hrds-prod-repo-maven-create";
		String MAVEN_REPO_CREATE_DEC = "创建maven仓库，及其相关信息";
		/**
		 * 创建maven仓库: 创建nexus server仓库   SagaTask code
		 */
		String MAVEN_REPO_CREATE_REPO = "hrds-prod-repo-maven-create.repo";
		String MAVEN_REPO_CREATE_DEC_REPO  = "创建nexus server仓库";
		int REPO_SEQ = 1;
		/**
		 * 创建maven仓库：创建角色   SagaTask code
		 */
		String MAVEN_REPO_CREATE_ROLE = "hrds-prod-repo-maven-create.role";
		String MAVEN_REPO_CREATE_DEC_ROLE = "创建nexus server 角色信息";
		int ROLE_SEQ = 2;
		/**
		 * 创建maven仓库：创建用户   SagaTask code
		 */
		String MAVEN_REPO_CREATE_USER = "hrds-prod-repo-maven-create.user";
		String MAVEN_REPO_CREATE_DEC_USER  = "创建nexus server 用户信息";
		int USER_SEQ = 3;
	}
}
