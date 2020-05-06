package org.hrds.rdupm.harbor.infra.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author superlee
 * @since 2019-04-15
 */
@Setter
@Getter
public class RoleDTO {
	private Long id;

    @ApiModelProperty(value = "角色名/必填")
    private String name;

    @ApiModelProperty(value = "角色编码/必填")
    private String code;

    @ApiModelProperty(value = "角色描述/非必填")
    private String description;

    @ApiModelProperty(value = "角色层级/必填")
    @JsonProperty(value = "level")
    private String resourceLevel;

    @ApiModelProperty(value = "组织ID/非必填")
    private Long organizationId;

    @ApiModelProperty(value = "是否启用/非必填")
    private Boolean enabled;

    @ApiModelProperty(value = "是否允许修改/非必填")
    private Boolean modified;

    @ApiModelProperty(value = "是否允许禁用/非必填")
    private Boolean enableForbidden;

    @ApiModelProperty(value = "是否内置角色/非必填")
    private Boolean builtIn;

}
