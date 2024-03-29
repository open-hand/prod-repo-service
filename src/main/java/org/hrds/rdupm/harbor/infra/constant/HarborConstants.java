package org.hrds.rdupm.harbor.infra.constant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 9:58 上午
 */
public interface HarborConstants {

    String ADMIN = "admin";

    String ANONYMOUS = "ANONYMOUS";

    String Y = "Y";

    String N = "N";

    String TRUE = "true";

    String FALSE = "false";

    String B = "B";

    String KB = "KB";

    String MB = "MB";

    String GB = "GB";

    String TB = "TB";

    String DEFAULT_PSW = "Abcd1234";

    String ASSIGN_AUTH = "assign";

    String UPDATE_AUTH = "update";

    String REVOKE_AUTH = "revoke";

    String LOWER_CREATE = "create";

    String HARBOR_UI = "harbor-ui";

    String DEFAULT_DATE = "0001-01-01T00:00:00Z";
    String DEFAULT_DATE_V2 = "0001-01-01T00:00:00.000Z";
    String API_VERSION_1 = "v1";// harbor1.x版本
    String API_VERSION_2 = "v2";// harbor2.x版本

    /**
     * 危害等级
     */
    interface SeverityLevel {

        String CRITICAL = "critical";
        String HIGH = "high";
        String MEDIUM = "medium";
        String LOW = "low";
        String NEGLIGIBLE = "negligible";
        String UNKNOWN = "unknown";
    }

    interface HarborSagaCode {
        /**
         * 创建Docker仓库
         * 创建用户、创建镜像仓库、保存存储容量配置、保存CVE白名单、保存项目到数据库
         */
        String CREATE_PROJECT = "rdupm-docker-repo-create";

        String CREATE_PROJECT_USER = "rdupm-docker-repo-create.user";

        String CREATE_PROJECT_REPO = "rdupm-docker-repo-create.repo";

        String CREATE_PROJECT_QUOTA = "rdupm-docker-repo-create.quota";

        String CREATE_PROJECT_CVE = "rdupm-docker-repo-create.cve";

        String CREATE_PROJECT_DB = "rdupm-docker-repo-create.db";

        String CREATE_PROJECT_AUTH = "rdupm-docker-repo-create.auth";

        String ROBOT_SAGA_TASK_CODE = "rdupm-docker-robot-create";

        /**
         * 更新Docke仓库
         */
        String UPDATE_PROJECT = "rdupm-docker-repo-update";

        String UPDATE_PROJECT_REPO = "rdupm-docker-repo-update.repo";

        String UPDATE_PROJECT_QUOTA = "rdupm-docker-repo-update.quota";

        String UPDATE_PROJECT_CVE = "rdupm-docker-repo-update.cve";

        String UPDATE_PROJECT_DB = "rdupm-docker-repo-update.db";

        /**
         * 分配权限
         */
        String CREATE_AUTH = "rdupm-docker-auth-create";

        String CREATE_AUTH_USER = "rdupm-docker-auth-create.user";

        String CREATE_AUTH_AUTH = "rdupm-docker-auth-create.auth";

        String CREATE_AUTH_DB = "rdupm-docker-auth-create.db";

        String UPDATE_PSW = "rdupm-docker-user-update";

        String UPDATE_PSW_HARBOR = "rdupm-docker-user-update.harbor";

        String UPDATE_PSW_NEXUS = "rdupm-docker-user-update.nexus";

        /**
         * 创建自定义镜像仓库
         */
        String CREATE_CUSTOMIZE_REPOSITORY = "rdupm-docker-customize-repository-create";

        String CREATE_HARBOR_REGISTRY = "rdupm-docker-harbor-registry-create";
    }

    enum HarborApiEnum {

        COUNT("/api/statistics", HttpMethod.GET, "获取所有项目数量、镜像数量", "/api/v2.0/statistics"),

        SEARCH("/api/search", HttpMethod.GET, "获取所有项目、所有镜像", "/api/v2.0/search"),

        /**
         * 用户API
         */
        CREATE_USER("/api/users", HttpMethod.POST, "创建用户", "/api/v2.0/users"),

        SELECT_USER_BY_USERNAME("/api/users/search", HttpMethod.GET, "根据用户名查询用户信息", "/api/v2.0/users/search"),
        SELECT_USER_BY_EMAIL("/api/users", HttpMethod.GET, "根据邮箱名查询用户信息", "/api/v2.0/users"),

        /**
         * 项目API
         */
        CREATE_PROJECT("/api/projects", HttpMethod.POST, "创建项目", "/api/v2.0/projects"),

        DETAIL_PROJECT("/api/projects/%s", HttpMethod.GET, "查询项目详情-项目ID", "/api/v2.0/projects/%s"),

        DELETE_PROJECT("/api/projects/%s", HttpMethod.DELETE, "删除项目-项目ID", "/api/v2.0/projects/%s"),

        UPDATE_PROJECT("/api/projects/%s", HttpMethod.PUT, "修改项目-项目ID", "/api/v2.0/projects/%s"),

        CHECK_PROJECT_NAME("/api/projects", HttpMethod.HEAD, "检查项目名称是否存在", "/api/v2.0/projects"),

        LIST_PROJECT("/api/projects", HttpMethod.GET, "查询项目列表", "/api/v2.0/projects"),

        /**
         * 项目概览
         */
        GET_PROJECT_SUMMARY("/api/projects/%s/summary", HttpMethod.GET, "获取存储容量使用情况--项目ID", "/api/v2.0/projects/%s/summary"),

        /**
         * 项目资源API
         */
        GET_PROJECT_QUOTA("/api/quotas/%s", HttpMethod.GET, "获取项目资源使用情况--项目ID", "/api/v2.0/quotas/%s"),

        UPDATE_PROJECT_QUOTA("/api/quotas/%s", HttpMethod.PUT, "更新项目资源配额--项目ID", "/api/v2.0/quotas/%s"),

        UPDATE_GLOBAL_QUOTA("/api/configurations", HttpMethod.PUT, "全局更新项目资源配额", "/api/v2.0/configurations"),

        GET_GLOBAL_QUOTA("/api/configurations", HttpMethod.GET, "获得全局资源配额", "/api/v2.0/configurations"),

        /**
         * 镜像API
         */
        LIST_IMAGE("/api/repositories", HttpMethod.GET, "查询镜像列表", "/api/v2.0/projects/%s/repositories", "项目名"),

        UPDATE_IMAGE_DESC("/api/repositories/%s", HttpMethod.PUT, "更新镜像描述--  仓库名/镜像名称", "/api/v2.0/projects/%s/repositories/%s", "项目名,repository_name"),

        DELETE_IMAGE("/api/repositories/%s", HttpMethod.DELETE, "删除镜像-- 仓库名/镜像名称", "/api/v2.0/projects/%s/repositories/%s", "项目名,repository_name"),

        /**
         * 镜像TAG API
         */
        GET_IMAGE_BUILD_LOG("/api/repositories/%s/tags/%s/manifest", HttpMethod.GET, "获取构建日志-- 仓库名/镜像名称、版本号", "/api/v2.0/projects/%s/repositories/%s/artifacts/%s/additions/build_history", "项目名/repository_name/reference（摘要）"),

        DELETE_IMAGE_TAG("/api/repositories/%s/tags/%s", HttpMethod.DELETE, "删除镜像TAG-- 仓库名/镜像名称、版本号", "/api/v2.0/projects/%s/repositories/%s/artifacts/%s", "项目名/repository_name/reference（摘要）"),

        LIST_IMAGE_TAG("/api/repositories/%s/tags", HttpMethod.GET, "获取镜像TAG列表-- 仓库名/镜像名称", "/api/v2.0/projects/%s/repositories/%s/artifacts", "项目名,repository_name"),

        COPY_IMAGE_TAG("/api/repositories/%s/tags", HttpMethod.POST, "复制镜像TAG-- 仓库名/镜像名称", "/api/v2.0/projects/%s/repositories/%s/artifacts", "需要复制到的项目名，目标repository_name"),

        /**
         * 获取项目用户
         */
        LIST_AUTH("/api/projects/%s/members", HttpMethod.GET, "获取项目中权限列表--项目ID", "/api/v2.0/projects/%s/members"),

        GET_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.GET, "获取项目中某个用户的权限情况--项目ID、harbor中权限ID", "/api/v2.0/projects/%s/members/%s"),

        DELETE_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.DELETE, "删除项目中某个用户的权限情况--项目ID、harbor中权限ID", "/api/v2.0/projects/%s/members/%s"),

        UPDATE_ONE_AUTH("/api/projects/%s/members/%s", HttpMethod.PUT, "修改项目中某个用户的权限情况--项目ID、harbor中权限ID", "/api/v2.0/projects/%s/members/%s"),

        CREATE_ONE_AUTH("/api/projects/%s/members", HttpMethod.POST, "分配项目中某个用户的权限情况--项目ID", "/api/v2.0/projects/%s/members"),

        /**
         * 日志API
         */
        LIST_LOGS_PROJECT("/api/projects/%s/logs", HttpMethod.GET, "查询项目日志-项目ID", "/api/v2.0/projects/%s/logs"),

        LIST_LOGS("/api/logs", HttpMethod.GET, "查询全局日志", "/api/v2.0/logs"),

        /**
         * 修改密码
         */
        CHANGE_PASSWORD("/api/users/%s/password", HttpMethod.PUT, "修改用户密码-Harbor用户ID", "/api/v2.0/users/%s/password"),

        /**
         * 机器人账户API
         */
        CREATE_ROBOT("/api/projects/%s/robots", HttpMethod.POST, "创建机器人账户-项目ID", "/api/v2.0/projects/%s/robots"),

        GET_PROJECT_ALL_ROBOTS("/api/projects/%s/robots", HttpMethod.GET, "查询项目的所有机器人账户-项目ID", "/api/v2.0/projects/%s/robots"),

        GET_ONE_ROBOT("/api/projects/%s/robots/%s", HttpMethod.GET, "查询指定ID的机器人账户-项目ID、机器人账户ID", "/api/v2.0/projects/%s/robots/%s"),

        DELETE_ROBOT("/api/projects/%s/robots/%s", HttpMethod.DELETE, "删除指定ID的机器人账户-项目ID、机器人账户ID", "/api/v2.0/projects/%s/robots/%s"),

        /**
         * 自定义仓库API
         */
        CURRENT_USER("/api/users/current", HttpMethod.GET, "查询当前用户信息", "/api/v2.0/users/current"),

        GET_SYSTEM_INFO("/api/systeminfo", HttpMethod.GET, "查询当前系统信息", "/api/v2.0/systeminfo"),

        /**
         * 镜像扫描
         */
        IMAGE_SCAN("/api/repositories/%s/tags/%s/scan", HttpMethod.POST, "镜像安全扫描，repo_name镜像仓库名/tag", "/api/v2.0/projects/%s/repositories/%s/artifacts/%s/scan", "项目名/repository_name仓库名/reference摘要"),

        /**
         * 实时查询单个镜像扫描结果
         * 镜像扫描结果查询
         */
        IMAGE_SCAN_RESULT("/api/repositories/%s/tags/%s", HttpMethod.GET, "镜像安全扫描，repo_name镜像仓库名/tag", "/api/v2.0/projects/%s/repositories/%s/artifacts/%s", "项目名/repository_name仓库名/reference摘要"),

        /**
         * 镜像扫描详情
         */
        IMAGE_SCAN_DETAIL("/api/repositories/%s/tags/%s/vulnerability/details", HttpMethod.GET, "镜像安全扫描，repo_name镜像仓库名/tag", "/api/v2.0/projects/%s/repositories/%s/artifacts/%s/additions/vulnerabilities", "项目名/repository_name仓库名/reference摘要"),

        /**
         * 获取项目下可用扫描器状态
         */
        IMAGE_QUERY_SCANNER_STATUS("", HttpMethod.GET, "", "/api/v2.0/projects/%s/scanner", "harbor项目Id"),

        //修改资源定额 /api/v2.0/quotas/908
        UPDATE_QUOTAS("/api/quotas/%s", HttpMethod.PUT, "修改仓库资源定额", "/api/v2.0/quotas/%s", "修改仓库资源定额"),
        LIST_QUOTAS("/api/quotas", HttpMethod.GET, "查询项目定额列表", "/api/v2.0/quotas", "查询项目定额列表");


        /**
         * v2.0接口
         */

        String apiUrl;

        HttpMethod httpMethod;

        String apiDesc;

        String apiUrlV2;

        String apiDescV2;


        public String getApiUrl() {
            return apiUrl;
        }


        public HttpMethod getHttpMethod() {
            return httpMethod;
        }


        public String getApiDesc() {
            return apiDesc;
        }


        public String getApiUrlV2() {
            return apiUrlV2;
        }


        HarborApiEnum(String apiUrl, HttpMethod method, String apiDesc) {
            this.apiUrl = apiUrl;
            this.httpMethod = method;
            this.apiDesc = apiDesc;
        }

        HarborApiEnum(String apiUrl, HttpMethod method, String apiDesc, String apiUrlV2) {
            this.apiUrl = apiUrl;
            this.httpMethod = method;
            this.apiDesc = apiDesc;
            this.apiUrlV2 = apiUrlV2;
        }

        HarborApiEnum(String apiUrl, HttpMethod method, String apiDesc, String apiUrlV2, String apiDescV2) {
            this.apiUrl = apiUrl;
            this.httpMethod = method;
            this.apiDesc = apiDesc;
            this.apiUrlV2 = apiUrlV2;
            this.apiDescV2 = apiDescV2;
        }
    }

    enum HarborRoleEnum {
        PROJECT_ADMIN(1L, "projectAdmin", "仓库管理员"),
        DEVELOPER(2L, "developer", "开发人员"),
        GUEST(3L, "guest", "访客"),
        MASTER(4L, "master", "维护人员"),
        LIMITED_GUEST(5L, "limitedGuest", "受限访客");

        Long roleId;

        String roleValue;

        String roleName;

        public static Long getIdByName(String harborRoleName) {
            if (StringUtils.isEmpty(harborRoleName)) {
                return null;
            }
            for (HarborRoleEnum authorityEnum : HarborRoleEnum.values()) {
                if (harborRoleName.equals(authorityEnum.getRoleName())) {
                    return authorityEnum.getRoleId();
                }
            }
            return null;
        }

        public Long getRoleId() {
            return roleId;
        }


        public String getRoleValue() {
            return roleValue;
        }


        public String getRoleName() {
            return roleName;
        }


        HarborRoleEnum(Long roleId, String roleValue, String roleName) {
            this.roleId = roleId;
            this.roleValue = roleValue;
            this.roleName = roleName;
        }

        public static String getValueById(Long roleId) {
            if (roleId == null) {
                return null;
            }
            for (HarborRoleEnum authorityEnum : HarborRoleEnum.values()) {
                if (roleId.equals(authorityEnum.getRoleId())) {
                    return authorityEnum.getRoleValue();
                }
            }
            return null;
        }

        public static String getNameById(Long roleId) {
            if (roleId == null) {
                return null;
            }
            for (HarborRoleEnum authorityEnum : HarborRoleEnum.values()) {
                if (roleId.equals(authorityEnum.getRoleId())) {
                    return authorityEnum.getRoleName();
                }
            }
            return null;
        }

        public static Long getIdByValue(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            for (HarborRoleEnum authorityEnum : HarborRoleEnum.values()) {
                if (value.equals(authorityEnum.getRoleValue())) {
                    return authorityEnum.getRoleId();
                }
            }
            return null;
        }
    }

    enum HarborImageOperateEnum {
        DELETE("delete", "删除"),
        PULL("pull", "拉取"),
        PUSH("push", "推送"),
        CREATE("create", "推送");

        String operateType;

        String operateName;

        public String getOperateType() {
            return operateType;
        }


        public String getOperateName() {
            return operateName;
        }

        HarborImageOperateEnum(String operateType, String operateName) {
            this.operateType = operateType;
            this.operateName = operateName;
        }


        public static String getNameByValue(String value) {
            for (HarborImageOperateEnum operateEnum : HarborImageOperateEnum.values()) {
                if (value.equals(operateEnum.getOperateType())) {
                    return operateEnum.getOperateName();
                }
            }
            return null;
        }
    }

    interface HarborRobot {

        String ENABLE_FLAG_Y = "Y";

        String ENABLE_FLAG_N = "N";

        String ROBOT = "robot";

        String ACTION_PULL = "pull";

        String ACTION_PUSH = "push";

        String ROBOT_RESOURCE = "/project/%s/repository";

        String ROBOT_NAME_PREFIX = "robot$";
    }

    interface HarborRepoType {
        String DEFAULT_REPO = "DEFAULT_REPO";

        String CUSTOM_REPO = "CUSTOM_REPO";
    }
}
