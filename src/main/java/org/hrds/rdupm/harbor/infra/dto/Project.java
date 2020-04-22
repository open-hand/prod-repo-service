package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:20
 */
public class Project {

    @SerializedName("project_name")
    private String name;

    @SerializedName("public")
    private Integer isPublic;

    private Metadata metadata;

    public Project(String name, Integer isPublic, Metadata metadata) {
        this.name = name;
        this.isPublic = isPublic;
        this.metadata = metadata;
    }

    public Project(String name, Integer isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}
