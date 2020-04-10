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
	private String repositoryName;
	@ApiModelProperty(value = "类型")
	private String type;
	@ApiModelProperty(value = "策略")
	private String versionPolicy;
	@ApiModelProperty(value = "项目Id", hidden = true)
	private Long projectId;
	@ApiModelProperty(value = "组织Id", hidden = true)
	private Long organizationId;

	public String getRepositoryName() {
		return repositoryName;
	}

	public NexusRepositoryQueryDTO setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
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

	public String getType() {
		return type;
	}

	public NexusRepositoryQueryDTO setType(String type) {
		this.type = type;
		return this;
	}

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public NexusRepositoryQueryDTO setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}
}
