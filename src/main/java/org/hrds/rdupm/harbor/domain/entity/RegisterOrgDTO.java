package org.hrds.rdupm.harbor.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;

/**
 * description
 *
 * @author chenxiuhong 2020/11/30 5:02 下午
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterOrgDTO {

    private ProjectDTO organization;

    private ProjectDTO project;

    private UserDTO user;

    public ProjectDTO getOrganization() {
        return organization;
    }

    public void setOrganization(ProjectDTO organization) {
        this.organization = organization;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }


}
