package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 组件信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusServerComponent {
	private String id;
	private String repository;
	private String format;
	private String group;
	/**
	 * artifactId
	 */
	private String name;
	private String version;
	private List<NexusServerAsset> assets;

	/**
	 * 使用时，版本号
	 */
	private String useVersion;

	/**
	 * 下级Component的Id， 没有时就是id
	 */
	private List<String> componentIds;


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
}
