package org.hrds.rdupm.harbor.api.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置指引
 *
 * @author chenxiuhong 2020/04/23 2:39 下午
 */
@Getter
@Setter
public class HarborGuide {

	private String loginCmd;

	private String dockerFile;

	private String buildCmd;

	private String pushCmd;

	private String pullCmd;

	public HarborGuide(){

	}
	public HarborGuide(String loginCmd, String dockerFile, String buildCmd, String pushCmd, String pullCmd) {
		this.loginCmd = loginCmd;
		this.dockerFile = dockerFile;
		this.buildCmd = buildCmd;
		this.pushCmd = pushCmd;
		this.pullCmd = pullCmd;
	}
}
