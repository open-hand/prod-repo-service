package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 配置指引信息DTO
 * @author weisen.yang@hand-china.com 2020/4/1
 */
public class NexusGuideDTO {
	public static final String PUSH_CMD = "mvn clean deploy -DskipTests";

	@ApiModelProperty(value = "拉取配置：server配置是否显示")
	private Boolean pullServerFlag;
	@ApiModelProperty(value = "拉取配置：server配置信息")
	private String pullServerInfo;
	@ApiModelProperty(value = "拉取配置：server配置信息(包含密码)")
	private String pullServerInfoPassword;
	@ApiModelProperty(value = "拉取配置：密码")
	private String pullPassword;

	@ApiModelProperty(value = "拉取配置：pom文件，仓库配置")
	private String pullPomRepoInfo;



	@ApiModelProperty(value = "拉取配置：拉取配置是否显示")
	private Boolean showPushFlag;
	@ApiModelProperty(value = "发布配置：server配置信息")
	private String pushServerInfo;
	@ApiModelProperty(value = "发布配置：server配置信息(包含密码)")
	private String pushServerInfoPassword;
	@ApiModelProperty(value = "发布配置：密码")
	private String pushPassword;
	@ApiModelProperty(value = "发布配置：pom文件，仓库配置")
	private String pushPomManageInfo;
	@ApiModelProperty(value = "发布配置：运行命令")
	private String pushCmd;

	public Boolean getPullServerFlag() {
		return pullServerFlag;
	}

	public NexusGuideDTO setPullServerFlag(Boolean pullServerFlag) {
		this.pullServerFlag = pullServerFlag;
		return this;
	}

	public String getPullServerInfo() {
		return pullServerInfo;
	}

	public NexusGuideDTO setPullServerInfo(String pullServerInfo) {
		this.pullServerInfo = pullServerInfo;
		return this;
	}

	public String getPullPassword() {
		return pullPassword;
	}

	public NexusGuideDTO setPullPassword(String pullPassword) {
		this.pullPassword = pullPassword;
		return this;
	}

	public String getPullPomRepoInfo() {
		return pullPomRepoInfo;
	}

	public NexusGuideDTO setPullPomRepoInfo(String pullPomRepoInfo) {
		this.pullPomRepoInfo = pullPomRepoInfo;
		return this;
	}

	public String getPushServerInfo() {
		return pushServerInfo;
	}

	public NexusGuideDTO setPushServerInfo(String pushServerInfo) {
		this.pushServerInfo = pushServerInfo;
		return this;
	}

	public String getPushPassword() {
		return pushPassword;
	}

	public NexusGuideDTO setPushPassword(String pushPassword) {
		this.pushPassword = pushPassword;
		return this;
	}

	public String getPushPomManageInfo() {
		return pushPomManageInfo;
	}

	public NexusGuideDTO setPushPomManageInfo(String pushPomManageInfo) {
		this.pushPomManageInfo = pushPomManageInfo;
		return this;
	}

	public String getPushCmd() {
		return pushCmd;
	}

	public NexusGuideDTO setPushCmd(String pushCmd) {
		this.pushCmd = pushCmd;
		return this;
	}

	public Boolean getShowPushFlag() {
		return showPushFlag;
	}

	public NexusGuideDTO setShowPushFlag(Boolean showPushFlag) {
		this.showPushFlag = showPushFlag;
		return this;
	}

	public String getPullServerInfoPassword() {
		return pullServerInfoPassword;
	}

	public NexusGuideDTO setPullServerInfoPassword(String pullServerInfoPassword) {
		this.pullServerInfoPassword = pullServerInfoPassword;
		return this;
	}

	public String getPushServerInfoPassword() {
		return pushServerInfoPassword;
	}

	public NexusGuideDTO setPushServerInfoPassword(String pushServerInfoPassword) {
		this.pushServerInfoPassword = pushServerInfoPassword;
		return this;
	}
}
