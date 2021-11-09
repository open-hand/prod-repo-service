package org.hrds.rdupm.nexus.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2021/11/9
 */
public class ResourceVO {

    private Long projectId;

    @ApiModelProperty("当前所用的Nexus使用量")
    private String  currentNexusCapacity;
    @ApiModelProperty("Nexus容量")
    private Long totalNexusCapacity;

    @ApiModelProperty("Harbor当前使用量")
    private String currentHarborCapacity;

    @ApiModelProperty("Harbor容量")
    private Long totalHarborCapacity;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }



    public Long getTotalNexusCapacity() {
        return totalNexusCapacity;
    }

    public void setTotalNexusCapacity(Long totalNexusCapacity) {
        this.totalNexusCapacity = totalNexusCapacity;
    }



    public Long getTotalHarborCapacity() {
        return totalHarborCapacity;
    }

    public void setTotalHarborCapacity(Long totalHarborCapacity) {
        this.totalHarborCapacity = totalHarborCapacity;
    }

    public String getCurrentNexusCapacity() {
        return currentNexusCapacity;
    }

    public void setCurrentNexusCapacity(String currentNexusCapacity) {
        this.currentNexusCapacity = currentNexusCapacity;
    }

    public String getCurrentHarborCapacity() {
        return currentHarborCapacity;
    }

    public void setCurrentHarborCapacity(String currentHarborCapacity) {
        this.currentHarborCapacity = currentHarborCapacity;
    }
}
