package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/22 20:37
 */
public class HasAdminRoleDTO {
    @SerializedName("has_admin_role")
    private Boolean hasAdminRole;

    public Boolean getHasAdminRole() {
        return hasAdminRole;
    }

    public void setHasAdminRole(Boolean hasAdminRole) {
        this.hasAdminRole = hasAdminRole;
    }
}
