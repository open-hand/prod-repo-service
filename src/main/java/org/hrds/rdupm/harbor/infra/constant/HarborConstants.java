package org.hrds.rdupm.harbor.infra.constant;

import org.springframework.http.HttpMethod;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 9:58 上午
 */
public interface HarborConstants {

	String TRUE = "true";

	String FALSE = "false";

	String KB = "KB";

	String MB = "MB";

	String GB = "GB";

	String TB = "TB";

	String DEFAULT_PASSWORD = "Abcd1234";

	/**
	* 危害等级
	* */
	interface SeverityLevel{

		String LOW = "low";

		String MEDIUM = "medium";

		String HIGH = "high";

		String CRITICAL = "critical";
	}

	interface HarborSagaCode{
		/**
		* 创建Docker仓库
		* 创建用户、创建镜像仓库、保存存储容量配置、保存CVE白名单、保存项目到数据库
		* */
		String CREATE_PROJECT = "rdupm-docker-create";

		String CREATE_PROJECT_USER = "rdupm-docker-create.user";

		String CREATE_PROJECT_REPO = "rdupm-docker-create.repo";

		String CREATE_PROJECT_QUOTA = "rdupm-docker-create.quota";

		String CREATE_PROJECT_CVE = "rdupm-docker-create.cve";

		String CREATE_PROJECT_DB = "rdupm-docker-create.db";

		/**
		* 更新Docke仓库
		* */
		String UPDATE_PROJECT = "rdupm-docker-update";

		String UPDATE_PROJECT_REPO = "rdupm-docker-update.repo";

		String UPDATE_PROJECT_QUOTA = "rdupm-docker-update.quota";

		String UPDATE_PROJECT_CVE = "rdupm-docker-update.cve";

		String UPDATE_PROJECT_DB = "rdupm-docker-update.db";
	}

	enum HarborApiEnum{

		COUNT("/api/statistics", HttpMethod.GET,"获取所有项目数量、镜像数量"),

		SEARCH("/api/search", HttpMethod.GET,"获取所有项目、所有镜像"),

		/**
		* 用户API
		* */
		CREATE_USER("/api/users", HttpMethod.POST,"创建用户"),

		SELECT_USER_BY_USERNAME("/api/users/search", HttpMethod.GET,"根据用户名查询用户信息"),

		/**
		* 项目API
		* */
		CREATE_PROJECT("/api/projects", HttpMethod.POST,"创建项目"),

		DETAIL_PROJECT("/api/projects/%s", HttpMethod.GET,"查询项目详情-项目ID"),

		DELETE_PROJECT("/api/projects/%s", HttpMethod.DELETE,"删除项目-项目ID"),

		UPDATE_PROJECT("/api/projects/%s", HttpMethod.PUT,"修改项目-项目ID"),

		CHECK_PROJECT_NAME("/api/projects", HttpMethod.HEAD,"检查项目名称是否存在"),

		LIST_PROJECT("/api/projects", HttpMethod.GET,"查询项目列表"),

		/**
		 * 元数据API
		 *
		 * */
		GET_PROJECT_METADATA("/api/projects/%s/metadatas/%s", HttpMethod.GET,"根据项目ID和元数据名称 获取元数据值"),

		UPDATE_PROJECT_METADATA("/api/projects/%s/metadatas/%s", HttpMethod.PUT,"根据项目ID和元数据名称 更新元数据值"),    //有问题

		DELETE_PROJECT_METADATA("/api/projects/%s/metadatas/%s", HttpMethod.DELETE,"根据项目ID和元数据名称 更新元数据值"), //有问题

		/**
		* 项目概览
		* */
		GET_PROJECT_SUMMARY("/api/projects/%s/summary", HttpMethod.GET,"获取存储容量使用情况--项目ID"),

		/**
		* 项目资源API
		* */
		GET_PROJECT_QUOTA("/api/quotas/%s", HttpMethod.GET,"获取项目资源使用情况--项目ID"),

		UPDATE_PROJECT_QUOTA("/api/quotas/%s", HttpMethod.PUT,"更新项目资源使用情况--项目ID"),

		/**
		* 镜像API
		* */
		LIST_IMAGE("/api/repositories", HttpMethod.GET,"查询镜像列表"),

		UPDATE_IMAGE_DESC("/api/repositories/%s", HttpMethod.PUT,"更新镜像描述--  仓库名/镜像名称"),

		DELETE_IMAGE("/api/repositories/%s", HttpMethod.DELETE,"删除镜像-- 仓库名/镜像名称"),

		/**
		* 镜像TAG API
		* */
		GET_IMAGE_BUILD_LOG("/api/repositories/%s/tags/%s/manifest", HttpMethod.GET,"获取构建日志-- 仓库名/镜像名称、版本号"),

		DETAIL_IMAGE_TAG("/api/repositories/%s/tags/%s", HttpMethod.GET,"获取镜像TAG明细-- 仓库名/镜像名称、版本号"),

		DELETE_IMAGE_TAG("/api/repositories/%s/tags/%s", HttpMethod.DELETE,"删除镜像TAG-- 仓库名/镜像名称、版本号"),

		LIST_IMAGE_TAG("/api/repositories/%s/tags", HttpMethod.GET,"获取镜像TAG列表-- 仓库名/镜像名称"),

		COPY_IMAGE_TAG("/api/repositories/%s/tags", HttpMethod.POST,"复制镜像TAG-- 仓库名/镜像名称"),

		/**
		* 获取项目用户
		* */
		LIST_AUTH("/api/projects/%s/members", HttpMethod.GET,"获取项目中权限列表--项目ID"),

		GET_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.GET,"获取项目中某个用户的权限情况--项目ID、harbor中权限ID"),

		DELETE_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.DELETE,"删除项目中某个用户的权限情况--项目ID、harbor中权限ID"),

		UPDATE_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.PUT,"修改项目中某个用户的权限情况--项目ID、harbor中权限ID"),

		CREATE_ONE_AUTH("/api/projects/%s/members", HttpMethod.POST,"分配项目中某个用户的权限情况--项目ID"),

		/**
		* 日志API
		* */
		LOGS_PROJECT("/api/projects/%s/logs", HttpMethod.GET,"查询项目日志-项目ID");

		String apiUrl;

		HttpMethod httpMethod;

		String apiDesc;

		public String getApiUrl() {
			return apiUrl;
		}

		public void setApiUrl(String apiUrl) {
			this.apiUrl = apiUrl;
		}

		public HttpMethod getHttpMethod() {
			return httpMethod;
		}

		public void setHttpMethod(HttpMethod httpMethod) {
			this.httpMethod = httpMethod;
		}

		public String getApiDesc() {
			return apiDesc;
		}

		public void setApiDesc(String apiDesc) {
			this.apiDesc = apiDesc;
		}

		HarborApiEnum(String apiUrl, HttpMethod method, String apiDesc) {
			this.apiUrl = apiUrl;
			this.httpMethod = method;
			this.apiDesc = apiDesc;
		}

	}

	interface ErrorMessage{
		String HARBOR_SERVER_ERROR = "error.harbor.service";
	}

}
