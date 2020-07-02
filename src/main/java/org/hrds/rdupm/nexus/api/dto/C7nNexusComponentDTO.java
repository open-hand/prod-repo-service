package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * choerodon 版本返回DTO
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@ApiModel("版本返回DTO")
@Getter
@Setter
@ToString
public class C7nNexusComponentDTO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "仓库名称")
    private String repository;
    @ApiModelProperty(value = "format")
    private String format;
    @ApiModelProperty(value = "groupId")
    private String group;
    @ApiModelProperty(value = "artifactId")
    private String name;
    @ApiModelProperty(value = "版本")
    private String version;
}
