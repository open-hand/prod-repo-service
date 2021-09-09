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

    private String packageName;

    private String repoType;

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
