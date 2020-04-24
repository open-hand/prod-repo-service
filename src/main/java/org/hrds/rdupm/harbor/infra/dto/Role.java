package org.hrds.rdupm.harbor.infra.dto;

import java.util.List;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:22
 */
public class Role {

    private List<Integer> roles;
    private String username;

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
