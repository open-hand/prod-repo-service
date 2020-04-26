package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 配置指引
 *
 * @author chenxiuhong 2020/04/23 2:39 下午
 */
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborGuideVo {

	@ApiModelProperty("登录命令")
	private String loginCmd;

	@ApiModelProperty("dockerfile内容")
	private String dockerFile;

	@ApiModelProperty("构建命令")
	private String buildCmd;

	@ApiModelProperty("push命令")
	private String pushCmd;

	@ApiModelProperty("pull命令")
	private String pullCmd;

	public HarborGuideVo(){

	}
	public HarborGuideVo(String loginCmd, String dockerFile, String buildCmd, String pushCmd, String pullCmd) {
		this.loginCmd = loginCmd;
		this.dockerFile = dockerFile;
		this.buildCmd = buildCmd;
		this.pushCmd = pushCmd;
		this.pullCmd = pullCmd;
	}
}
