package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusComponentInfo {
	private String path;
	private String repository;
	private String format;
	private String group;
	private String name;
	private String useVersion;
	private List<NexusComponent> components;

	public String getRepository() {
		return repository;
	}

	public NexusComponentInfo setRepository(String repository) {
		this.repository = repository;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusComponentInfo setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public NexusComponentInfo setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusComponentInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getUseVersion() {
		return useVersion;
	}

	public NexusComponentInfo setUseVersion(String useVersion) {
		this.useVersion = useVersion;
		return this;
	}

	public List<NexusComponent> getComponents() {
		return components;
	}

	public NexusComponentInfo setComponents(List<NexusComponent> components) {
		this.components = components;
		return this;
	}

	public String getPath() {
		return path;
	}

	public NexusComponentInfo setPath(String path) {
		this.path = path;
		return this;
	}
}
