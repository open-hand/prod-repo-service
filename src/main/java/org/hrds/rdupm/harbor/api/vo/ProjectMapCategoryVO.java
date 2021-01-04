package org.hrds.rdupm.harbor.api.vo;

import org.hrds.rdupm.harbor.domain.entity.ProjectCategoryDTO;

/**
 * Created by wangxiang on 2020/12/30
 */
public class ProjectMapCategoryVO {
    private Long id;

    private Long projectId;

    private Long categoryId;

    private ProjectCategoryDTO projectCategoryDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ProjectCategoryDTO getProjectCategoryDTO() {
        return projectCategoryDTO;
    }

    public void setProjectCategoryDTO(ProjectCategoryDTO projectCategoryDTO) {
        this.projectCategoryDTO = projectCategoryDTO;
    }
}
