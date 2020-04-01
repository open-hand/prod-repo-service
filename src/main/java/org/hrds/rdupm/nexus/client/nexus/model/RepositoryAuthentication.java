package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库 Authentication信息
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class RepositoryAuthentication {
	private String type;
	private String username;
	private String password;
	private String ntlmHost;
	private String ntlmDomain;

	public String getType() {
		return type;
	}

	public RepositoryAuthentication setType(String type) {
		this.type = type;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public RepositoryAuthentication setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getNtlmHost() {
		return ntlmHost;
	}

	public RepositoryAuthentication setNtlmHost(String ntlmHost) {
		this.ntlmHost = ntlmHost;
		return this;
	}

	public String getNtlmDomain() {
		return ntlmDomain;
	}

	public RepositoryAuthentication setNtlmDomain(String ntlmDomain) {
		this.ntlmDomain = ntlmDomain;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RepositoryAuthentication setPassword(String password) {
		this.password = password;
		return this;
	}
}
