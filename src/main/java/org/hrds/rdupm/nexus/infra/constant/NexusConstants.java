package org.hrds.rdupm.nexus.infra.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/27
 */
public interface NexusConstants {


    interface Flag {
        String Y = "Y";
        String N = "N";
    }


    /**
     * 日志操作类型: RDUPM.AUTH_OPERATE_TYPE
     */
    interface LogOperateType {
        String AUTH_CREATE = "assign";
        String AUTH_UPDATE = "update";
        String AUTH_DELETE = "revoke";
        String AUTH_PULL = "pull";
    }

    enum NexusRoleEnum {
        PROJECT_ADMIN("projectAdmin", "仓库管理员"),
        DEVELOPER("developer", "开发人员"),
        GUEST("guest", "访客");

        String roleCode;

        String roleName;

        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        NexusRoleEnum(String roleCode, String roleName) {
            this.roleCode = roleCode;
            this.roleName = roleName;
        }

        public static String getNameByCode(String roleCode) {
            if (roleCode == null) {
                return null;
            }
            for (NexusRoleEnum nexusRoleEnum : NexusRoleEnum.values()) {
                if (roleCode.equals(nexusRoleEnum.getRoleCode())) {
                    return nexusRoleEnum.getRoleName();
                }
            }
            return null;
        }

        public static String getCodeByName(String roleName) {
            if (roleName == null) {
                return null;
            }
            for (NexusRoleEnum nexusRoleEnum : NexusRoleEnum.values()) {
                if (roleName.equals(nexusRoleEnum.getRoleName())) {
                    return nexusRoleEnum.getRoleCode();
                }
            }
            return null;
        }
    }

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
        String JAR = "jar";
    }


}
