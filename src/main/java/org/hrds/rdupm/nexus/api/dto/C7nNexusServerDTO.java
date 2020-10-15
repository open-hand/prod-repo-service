package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * choerodon nexus服务DTO
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@ApiModel("nexus服务DTO")
@Setter
@Getter
@ToString
public class C7nNexusServerDTO {

    @Encrypt
    @ApiModelProperty(value = "主键")
    private Long configId;
    @ApiModelProperty(value = "服务名称")
    private String serverName;
    @ApiModelProperty(value = "访问地址")
    private String serverUrl;
    @ApiModelProperty(value = "项目Id")
    private Long projectId;
}
