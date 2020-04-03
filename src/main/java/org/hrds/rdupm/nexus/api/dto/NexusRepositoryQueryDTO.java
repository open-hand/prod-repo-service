package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 仓库列表查询DTO
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("仓库列表查询")
public class NexusRepositoryQueryDTO {
	@ApiModelProperty(value = "仓库名称")
	private String name;
	@ApiModelProperty(value = "项目Id", hidden = true)
	private Long projectId;

	public String getName() {
		return name;
	}

	public NexusRepositoryQueryDTO setName(String name) {
		this.name = name;
		return this;
	}

	public Long getProjectId() {
		return projectId;
	}

	public NexusRepositoryQueryDTO setProjectId(Long projectId) {
		this.projectId = projectId;
		return this;
	}
}
