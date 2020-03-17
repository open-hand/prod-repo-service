package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 组件信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusComponent {
	private String id;
	private String repository;
	private String format;
	private String group;
	private String name;
	private String version;
	private List<NexusAsset> assets;

	/**
	 * 使用时，版本号
	 */
	private String useVersion;

	public String getId() {
		return id;
	}

	public NexusComponent setId(String id) {
		this.id = id;
		return this;
	}

	public String getRepository() {
		return repository;
	}

	public NexusComponent setRepository(String repository) {
		this.repository = repository;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusComponent setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public NexusComponent setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusComponent setName(String name) {
		this.name = name;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public NexusComponent setVersion(String version) {
		this.version = version;
		return this;
	}

	public List<NexusAsset> getAssets() {
		return assets;
	}

	public NexusComponent setAssets(List<NexusAsset> assets) {
		this.assets = assets;
		return this;
	}

	public String getUseVersion() {
		return useVersion;
	}

	public NexusComponent setUseVersion(String useVersion) {
		this.useVersion = useVersion;
		return this;
	}
}
