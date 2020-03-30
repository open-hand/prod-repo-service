package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusServerPrivilege {
	private String type;
	private String name;
	private String description;
	private Boolean readOnly;
	private String domain;
	private List<String> actions;
	private String format;
	private String repository;

	public String getType() {
		return type;
	}

	public NexusServerPrivilege setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusServerPrivilege setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public NexusServerPrivilege setDescription(String description) {
		this.description = description;
		return this;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public NexusServerPrivilege setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public NexusServerPrivilege setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public List<String> getActions() {
		return actions;
	}

	public NexusServerPrivilege setActions(List<String> actions) {
		this.actions = actions;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerPrivilege setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getRepository() {
		return repository;
	}

	public NexusServerPrivilege setRepository(String repository) {
		this.repository = repository;
		return this;
	}
}
