package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 用户查询返回
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class NexusUser {
	private String userId;
	private String password;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String source;
	private String status;
	private String readOnly;
	private List<String> roles;
	private List<String> externalRoles;

	public String getUserId() {
		return userId;
	}

	public NexusUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public NexusUser setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public NexusUser setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public NexusUser setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public NexusUser setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
	}

	public String getSource() {
		return source;
	}

	public NexusUser setSource(String source) {
		this.source = source;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public NexusUser setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getReadOnly() {
		return readOnly;
	}

	public NexusUser setReadOnly(String readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public List<String> getRoles() {
		return roles;
	}

	public NexusUser setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}

	public List<String> getExternalRoles() {
		return externalRoles;
	}

	public NexusUser setExternalRoles(List<String> externalRoles) {
		this.externalRoles = externalRoles;
		return this;
	}
}