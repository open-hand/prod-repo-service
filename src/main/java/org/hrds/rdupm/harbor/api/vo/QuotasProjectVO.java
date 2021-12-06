package org.hrds.rdupm.harbor.api.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxiang on 2021/10/10
 */
public class QuotasProjectVO {
    /**
     * 仓库id
     */
    private Integer id;
    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库所有者
     */
    @SerializedName("owner_name")
    private String ownerName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
