package org.hrds.rdupm.harbor.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/11 10:20
 */
@Getter
@Setter
@ApiModel("Harbor仓库配置DTO")
public class HarborRepoConfigDTO {

    @ApiModelProperty(value = "仓库地址")
    private String repoUrl;
    @ApiModelProperty(value = "仓库名称")
    private String repoName;

    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "密码")
    private String password;

}
