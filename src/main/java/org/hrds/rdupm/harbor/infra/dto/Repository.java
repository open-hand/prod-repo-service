package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/18 15:05
 */
public class Repository {
    @SerializedName("project_id")
    private Integer projectId;

    @SerializedName("project_name")
    private String projectName;

    @SerializedName("project_public")
    private Boolean projectPublic;

    @SerializedName("pull_count")
    private Integer pullCount;

    @SerializedName("repository_name")
    private String repositoryName;

    @SerializedName("tags_count")
    private Integer tagsCount;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getProjectPublic() {
        return projectPublic;
    }

    public void setProjectPublic(Boolean projectPublic) {
        this.projectPublic = projectPublic;
    }

    public Integer getPullCount() {
        return pullCount;
    }

    public void setPullCount(Integer pullCount) {
        this.pullCount = pullCount;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Integer getTagsCount() {
        return tagsCount;
    }

    public void setTagsCount(Integer tagsCount) {
        this.tagsCount = tagsCount;
    }
}
