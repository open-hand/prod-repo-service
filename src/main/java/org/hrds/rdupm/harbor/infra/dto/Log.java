package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/24 20:20
 */
public class Log {
    @SerializedName("log_id")
    private Integer logId;

    private String username;

    @SerializedName("project_id")
    private Integer projectId;

    @SerializedName("repo_name")
    private String repoName;

    @SerializedName("repo_tag")
    private String repoTag;

    private String guid;

    private String operation;

    @SerializedName("op_time")
    private String opTime;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoTag() {
        return repoTag;
    }

    public void setRepoTag(String repoTag) {
        this.repoTag = repoTag;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }
}
