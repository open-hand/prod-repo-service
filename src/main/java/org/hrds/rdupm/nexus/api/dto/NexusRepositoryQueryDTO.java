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
	@ApiModelProperty(value = "组织Id", hidden = true)
	private Long organizationId;

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

	public Long getOrganizationId() {
		return organizationId;
	}

	public NexusRepositoryQueryDTO setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
		return this;
	}
}
