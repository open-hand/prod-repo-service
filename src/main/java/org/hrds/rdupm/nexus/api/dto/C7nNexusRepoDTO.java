package org.hrds.rdupm.nexus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * choerodon nexus仓库DTO
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@ApiModel("nexus仓库DTO")
@Setter
@Getter
@ToString
public class C7nNexusRepoDTO {
    @ApiModelProperty(value = "服务配置Id")
    private Long configId;
    @ApiModelProperty(value = "主键Id")
    private Long repositoryId;
    @ApiModelProperty(value = "仓库名称")
    private String neRepositoryName;
}
