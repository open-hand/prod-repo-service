package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 配置指引信息DTO
 * @author weisen.yang@hand-china.com 2020/4/1
 */
@ApiModel("nexus maven-jar依赖配置")
public class NexusComponentGuideDTO {

	@ApiModelProperty(value = "拉取配置：jar引入")
	private String pullPomDep;

	public String getPullPomDep() {
		return pullPomDep;
	}

	public NexusComponentGuideDTO setPullPomDep(String pullPomDep) {
		this.pullPomDep = pullPomDep;
		return this;
	}
}
