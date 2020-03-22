package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/19 14:40
 */
public class ProjectUpdateDTO {

    @SerializedName("project_name")
    private String name;

    @SerializedName("count_limit")
    private Integer countLimit;

    @SerializedName("storage_limit")
    private Integer storageLimit;

    private Metadata metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCountLimit() {
        return countLimit;
    }

    public void setCountLimit(Integer countLimit) {
        this.countLimit = countLimit;
    }

    public Integer getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(Integer storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
