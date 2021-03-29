package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/3/29
 * @Modified By:
 */
public class HarborImageScanResultVO {
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("连接")
    private List<String> links;

    @ApiModelProperty("修复版本")
    @SerializedName("fix_version")
    private String fixVersion;

    @ApiModelProperty("组件")
    @SerializedName("package")
    private String packageStr;

    private String severity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public String getPackageStr() {
        return packageStr;
    }

    public void setPackageStr(String packageStr) {
        this.packageStr = packageStr;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
