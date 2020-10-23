package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;


/**
 * 描述
 *
 * @author weisen.yang@hand-china.com 2020/10/23
 */
@ApiModel("错误返回")
@Getter
@Setter
public class NexusInitErrorDTO {
    private Long configId;
    private String serverName;
    private String serverUrl;
}
