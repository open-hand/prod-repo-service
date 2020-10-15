package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 描述
 *
 * @author weisen.yang@hand-china.com 2020/7/20
 */
@Setter
@Getter
@ToString
public class NexusRepositoryListDTO {
    @ApiModelProperty(value = "仓库名称")
    private String name;

}
