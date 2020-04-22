package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@ApiModel("包信息")
public class NexusServerComponentInfo {
	@ApiModelProperty(value = "id")
	private String id;
	@ApiModelProperty(value = "成员components列表，Id集合")
	private List<String> componentIds;
	private String path;
	@ApiModelProperty(value = "仓库名称")
	private String repository;
	@ApiModelProperty(value = "format")
	private String format;
	@ApiModelProperty(value = "groupId")
	private String group;
	@ApiModelProperty(value = "artifactId")
	private String name;
	@ApiModelProperty(value = "版本")
	private String version;
	@ApiModelProperty(value = "使用版本")
	private String useVersion;
	@ApiModelProperty(value = "是否允许删除")
	private Boolean deleteFlag;
	private List<NexusServerComponent> components;

	public String getRepository() {
		return repository;
	}

	public NexusServerComponentInfo setRepository(String repository) {
		this.repository = repository;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerComponentInfo setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public NexusServerComponentInfo setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusServerComponentInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getUseVersion() {
		return useVersion;
	}

	public NexusServerComponentInfo setUseVersion(String useVersion) {
		this.useVersion = useVersion;
		return this;
	}

	public List<NexusServerComponent> getComponents() {
		return components;
	}

	public NexusServerComponentInfo setComponents(List<NexusServerComponent> components) {
		this.components = components;
		return this;
	}

	public String getPath() {
		return path;
	}

	public NexusServerComponentInfo setPath(String path) {
		this.path = path;
		return this;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public NexusServerComponentInfo setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
		return this;
	}

	public String getId() {
		return id;
	}

	public NexusServerComponentInfo setId(String id) {
		this.id = id;
		return this;
	}

	public List<String> getComponentIds() {
		return componentIds;
	}

	public NexusServerComponentInfo setComponentIds(List<String> componentIds) {
		this.componentIds = componentIds;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public NexusServerComponentInfo setVersion(String version) {
		this.version = version;
		return this;
	}
}
