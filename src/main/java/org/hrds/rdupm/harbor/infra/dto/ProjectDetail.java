package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:21
 */
public class ProjectDetail {

    private String name;
    @SerializedName("project_id")
    private Integer projectId;
    @SerializedName("owner_id")
    private Integer ownerId;
    @SerializedName("owner_name")
    private String ownerName;
    @SerializedName("current_user_role_id")
    private Integer currentUserRoleId;
    @SerializedName("current_user_role_ids")
    private Integer [] currentUserRoleIds;
    @SerializedName("repo_count")
    private Integer repoCount;
    @SerializedName("chart_count")
    private Integer chartCount;
    private Metadata metadata;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getCurrentUserRoleId() {
        return currentUserRoleId;
    }

    public void setCurrentUserRoleId(Integer currentUserRoleId) {
        this.currentUserRoleId = currentUserRoleId;
    }

    public Integer[] getCurrentUserRoleIds() {
        return currentUserRoleIds;
    }

    public void setCurrentUserRoleIds(Integer[] currentUserRoleIds) {
        this.currentUserRoleIds = currentUserRoleIds;
    }

    public Integer getRepoCount() {
        return repoCount;
    }

    public void setRepoCount(Integer repoCount) {
        this.repoCount = repoCount;
    }

    public Integer getChartCount() {
        return chartCount;
    }

    public void setChartCount(Integer chartCount) {
        this.chartCount = chartCount;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
