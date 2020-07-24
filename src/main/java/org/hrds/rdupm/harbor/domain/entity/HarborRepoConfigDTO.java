package org.hrds.rdupm.harbor.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/11 10:20
 */
@Getter
@Setter
@ApiModel("Harbor仓库配置DTO")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborRepoConfigDTO {
    @ApiModelProperty(value = "仓库ID")
	@Encrypt
    private Long repoId;
    @ApiModelProperty(value = "仓库地址")
    private String repoUrl;
    @ApiModelProperty(value = "仓库名称")
    private String repoName;
    @ApiModelProperty(value = "是否私有")
    private String isPrivate;
    @ApiModelProperty(value = "pull机器人账户")
    private HarborRepoRobotDTO pullRobot;
    @ApiModelProperty(value = "push机器人账户")
    private HarborRepoRobotDTO pushRobot;

    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "项目共享")
    private String projectShare;

    public HarborRepoConfigDTO() {
    }

    public HarborRepoConfigDTO(Long repoId, String repoUrl, String repoName, String isPrivate) {
        this.repoId = repoId;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.isPrivate = isPrivate;
    }

    public HarborRepoConfigDTO(Long repoId, String repoUrl, String repoName, String isPrivate, String loginName, String password, String email, String projectShare) {
        this.repoId = repoId;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.isPrivate = isPrivate;
        this.loginName = loginName;
        this.password = password;
        this.email = email;
        this.projectShare = projectShare;
    }

    public HarborRepoConfigDTO(Long repoId, String repoUrl, String repoName, String isPrivate, List<HarborRobot> harborRobotList) {
        this.repoId = repoId;
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.isPrivate = isPrivate;
        harborRobotList.stream().forEach(harborRobot -> {
            if (harborRobot.getAction().equals(HarborConstants.HarborRobot.ACTION_PULL)) {
                this.pullRobot = new HarborRepoRobotDTO(harborRobot.getName(), harborRobot.getToken());
            } else {
                this.pushRobot = new HarborRepoRobotDTO(harborRobot.getName(), harborRobot.getToken());
            }
        });
    }
}
