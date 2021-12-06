package org.hrds.rdupm.nexus.client.nexus.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusApiConstants {

	/**
	 * 默认角色，无权限
	 */
	interface defaultRole {
		String DEFAULT_ROLE = "Choerodon-hrds-default";
		String DEFAULT_ROLE_NAME = "Choerodon初始默认角色";
	}

	/**
	 * 发布类型
	 */
	interface packageType {
		String JAR = "jar";
		String WAR = "war";
		String POM = "pom";
	}

	/**
	 * format类型
	 */
	interface NexusRepoFormat {
		String MAVEN_FORMAT = "maven2";
		String NPM_FORMAT = "npm";
	}

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
	/**
	 * 仓库策略
	 */
	interface VersionPolicy {
		String MIXED = "MIXED";
		String SNAPSHOT = "SNAPSHOT";
		String RELEASE = "RELEASE";
	}

	interface WritePolicy {
		String ALLOW = "ALLOW";
		String ALLOW_ONCE = "ALLOW_ONCE";
		String DENY = "DENY";
	}

	/**
	 * script脚本列表
	 */
	interface ScriptName {

		/**
		 * TYPE
		 */
		String TYPE = "groovy";
		/**
		 * 脚本前缀
		 */
		String SCRIPT_PREFIX = "hrds.";

		/**
		 * 脚本ID list: 初始化脚本时使用
		 */
		List<String> SCRIPT_LIST = Arrays.asList(
				ScriptName.CREATE_MAVEN_PROXY,
				ScriptName.CREATE_MAVEN_GROUP,
				ScriptName.CREATE_NPM_GROUP,
				ScriptName.CREATE_NPM_HOSTED,
				ScriptName.CREATE_NPM_PROXY,
				ScriptName.COMPONENT_LIST_QUERY,
				ScriptName.COMPONENT_DELETE,
				ScriptName.COMPONENT_COUNT_QUERY_DELETE,
				ScriptName.FIND_ASSET);
		/**
		 * 创建maven仓库组   脚本：groovy包下create_maven_group.groovy
		 */
		String CREATE_MAVEN_GROUP = ScriptName.SCRIPT_PREFIX + "create_maven_group";
		/**
		 * 创建maven代理仓库  脚本：groovy包下create_maven_proxy.groovy
		 */
		String CREATE_MAVEN_PROXY = ScriptName.SCRIPT_PREFIX + "create_maven_proxy";


		/**
		 * 创建npm本地仓库   脚本：groovy包下create_npm_hosted.groovy
		 */
		String CREATE_NPM_HOSTED = ScriptName.SCRIPT_PREFIX + "create_npm_hosted";
		/**
		 * 创建npm仓库组   脚本：groovy包下create_npm_group.groovy
		 */
		String CREATE_NPM_GROUP = ScriptName.SCRIPT_PREFIX + "create_npm_group";
		/**
		 * 创建npm代理仓库  脚本：groovy包下create_npm_proxy.groovy
		 */
		String CREATE_NPM_PROXY = ScriptName.SCRIPT_PREFIX + "create_npm_proxy";

		/**
		 * component查询  脚本：groovy包下 component_list_query.groovy
		 */
		String COMPONENT_LIST_QUERY = ScriptName.SCRIPT_PREFIX + "component_list_query";

		/**
		 * component删除  脚本：groovy包下 delete_component.groovy
		 */
		String COMPONENT_DELETE = ScriptName.SCRIPT_PREFIX + "delete_component";

		/**
		 * 删除仓库，查询仓库下是否有组件  脚本：groovy包下 component_count_query_delete.groovy
		 */
		String COMPONENT_COUNT_QUERY_DELETE = ScriptName.SCRIPT_PREFIX + "component_count_query_delete";

		String FIND_ASSET = ScriptName.SCRIPT_PREFIX + "find_asset";
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
		 * 用户权限不足，没有该操作的权限
		 */
		String NEXUS_ROLE_PRI_NOT_ASSIGNED = "error.nexus.client.nexus.role.pri.not.assigned";
		/**
		 * 访问nexus服务失败，请检查服务是否正常
		 */
		String NEXUS_SERVER_ERROR = "error.nexus.client.nexus.server.failed";

		/**
		 * 对应资源已不存在
		 */
		String RESOURCE_NOT_EXIST = "error.nexus.client.resource.not.exist";

		/**
		 * 包上传错误： 该仓库，不允许去更新已有包
		 */
		String REPO_NOT_UPDATE_ASSET = "error.nexus.client.not.update.asset";

		/**
		 * 调用nexus服务接口失败，放回状态码：{0}
		 */
		String NEXUS_API_IS_ERROR = "error.nexus.api.is.error";
	}
}
