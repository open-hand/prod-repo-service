package org.hrds.rdupm.common.api.vo;

/**
 * Created by wangxiang on 2021/9/9
 */
public class UserNexusInfo {

    /**
     * nexus 仓库名
     */
    private String repositoryName;

    /**
     * 拉取jar包的用户名
     */
    private String userName;

    /**
     * 包的名字
     */
    private String packageName;

    /**
     * 仓库类型
     */
    private String repoType;

    /**
     * nexus配置的id
     */
    private Long configId;

    /**
     * 操作类型
     */
    private String operateType;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
