package org.hrds.rdupm.harbor.infra.dto;

import java.util.List;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/18 15:15
 */
public class ProjectRepository {
    private List<ProjectDetail> project;

    private List<Repository> repository;

    public List<ProjectDetail> getProject() {
        return project;
    }

    public void setProject(List<ProjectDetail> project) {
        this.project = project;
    }

    public List<Repository> getRepository() {
        return repository;
    }

    public void setRepository(List<Repository> repository) {
        this.repository = repository;
    }
}
