package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 角色查询返回
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class NexusRole {
	private String id;
	private String source;
	private String name;
	private String description;
	private List<String> privileges;
	private List<String> roles;

	public String getId() {
		return id;
	}

	public NexusRole setId(String id) {
		this.id = id;
		return this;
	}

	public String getSource() {
		return source;
	}

	public NexusRole setSource(String source) {
		this.source = source;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusRole setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public NexusRole setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<String> getPrivileges() {
		return privileges;
	}

	public NexusRole setPrivileges(List<String> privileges) {
		this.privileges = privileges;
		return this;
	}

	public List<String> getRoles() {
		return roles;
	}

	public NexusRole setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}
}
