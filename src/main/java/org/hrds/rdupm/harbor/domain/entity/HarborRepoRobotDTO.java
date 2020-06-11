package org.hrds.rdupm.harbor.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/11 11:04
 */
@Getter
@Setter
@ApiModel("Harbor仓库机器人DTO")
public class HarborRepoRobotDTO {
    @ApiModelProperty(value = "账户名")
    private String name;
    @ApiModelProperty(value = "账户token")
    private String token;
}
