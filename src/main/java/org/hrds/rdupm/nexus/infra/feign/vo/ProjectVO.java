package org.hrds.rdupm.nexus.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

/**
 * 项目VO
 * @author weisen.yang@hand-china.com 2020/4/3
 */
public class ProjectVO {

	@ApiModelProperty(value = "项目ID")
	private Long id;

	@ApiModelProperty(value = "项目名")
	private String name;

	@ApiModelProperty(value = "项目编码")
	private String code;

	@ApiParam(value = "组织id")
	private Long organizationId;

	@ApiModelProperty(value = "项目图标url")
	private String imageUrl;

	@ApiModelProperty(value = "是否启用")
	private Boolean enabled;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ProjectDTO{" +
				"id=" + id +
				", name='" + name + '\'' +
				", code='" + code + '\'' +
				", organizationId=" + organizationId +
				", imageUrl='" + imageUrl + '\'' +
				", enabled=" + enabled +
				'}';
	}
}
