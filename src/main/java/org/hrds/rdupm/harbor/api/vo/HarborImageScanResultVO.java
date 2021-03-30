package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/3/29
 * @Modified By:
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
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

    @ApiModelProperty("当前版本")
    @SerializedName("version")
    private String version;

    @ApiModelProperty("组件")
    @SerializedName("package")
    private String packageStr;

    @ApiModelProperty("安全程度")
    private transient String severity;

    @JsonIgnore
    @SerializedName("severity")
    private Object severityObject;

    @JsonIgnore
    @ApiModelProperty("连接")
    private String link;

    @JsonIgnore
    @ApiModelProperty("修复版本")
    private String fixedVersion;

}
