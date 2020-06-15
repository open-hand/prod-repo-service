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
    @ApiModelProperty(value = "仓库ID")
    private Long repoId;
    @ApiModelProperty(value = "仓库地址")
    private String repoUrl;
    @ApiModelProperty(value = "仓库名称")
    private String repoName;

    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "密码")
    private String password;

    public HarborRepoConfigDTO() {
    }

    public HarborRepoConfigDTO(Long repoId, String repoUrl, String repoName) {
        this.repoId = repoId;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
    }

    public HarborRepoConfigDTO(Long repoId, String repoUrl, String repoName, String loginName, String password) {
        this.repoId = repoId;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.loginName = loginName;
        this.password = password;
    }
}
