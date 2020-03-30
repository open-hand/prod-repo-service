package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusServerComponentInfo {
	private String path;
	private String repository;
	private String format;
	private String group;
	private String name;
	private String useVersion;
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
}
