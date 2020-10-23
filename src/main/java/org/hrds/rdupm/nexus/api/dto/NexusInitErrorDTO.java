package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 描述
 *
 * @author weisen.yang@hand-china.com 2020/10/23
 */
@ApiModel("nexus maven-仓库配置")
@Getter
@Setter
public class NexusInitErrorDTO {
    private Long configId;
    private String serverName;
    private String serverUrl;
}
