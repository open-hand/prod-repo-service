package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 组件信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@ApiModel("包信息")
public class NexusServerComponent {
	@ApiModelProperty(value = "id")
	private String id;
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
	private List<NexusServerAsset> assets;

	@ApiModelProperty(value = "使用版本")
	private String useVersion;

	@ApiModelProperty(value = "下级Component的Id， 没有时就是id")
	private List<String> componentIds;
	@ApiModelProperty(value = "是否允许删除")
	private Boolean deleteFlag;


	public String getId() {
		return id;
	}

	public NexusServerComponent setId(String id) {
		this.id = id;
		return this;
	}

	public String getRepository() {
		return repository;
	}

	public NexusServerComponent setRepository(String repository) {
		this.repository = repository;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerComponent setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public NexusServerComponent setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusServerComponent setName(String name) {
		this.name = name;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public NexusServerComponent setVersion(String version) {
		this.version = version;
		return this;
	}

	public List<NexusServerAsset> getAssets() {
		return assets;
	}

	public NexusServerComponent setAssets(List<NexusServerAsset> assets) {
		this.assets = assets;
		return this;
	}

	public String getUseVersion() {
		return useVersion;
	}

	public NexusServerComponent setUseVersion(String useVersion) {
		this.useVersion = useVersion;
		return this;
	}

	public List<String> getComponentIds() {
		return componentIds;
	}

	public NexusServerComponent setComponentIds(List<String> componentIds) {
		this.componentIds = componentIds;
		return this;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public NexusServerComponent setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
		return this;
	}
}
