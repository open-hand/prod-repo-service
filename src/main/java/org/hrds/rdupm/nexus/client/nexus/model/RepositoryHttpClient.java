package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库 HttpClient信息
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class RepositoryHttpClient {
	private Boolean blocked;
	private Boolean autoBlock;
	private RepositoryConnection connection;
	private RepositoryAuthentication authentication;

	public Boolean getBlocked() {
		return blocked;
	}

	public RepositoryHttpClient setBlocked(Boolean blocked) {
		this.blocked = blocked;
		return this;
	}

	public Boolean getAutoBlock() {
		return autoBlock;
	}

	public RepositoryHttpClient setAutoBlock(Boolean autoBlock) {
		this.autoBlock = autoBlock;
		return this;
	}

	public RepositoryConnection getConnection() {
		return connection;
	}

	public RepositoryHttpClient setConnection(RepositoryConnection connection) {
		this.connection = connection;
		return this;
	}

	public RepositoryAuthentication getAuthentication() {
		return authentication;
	}

	public RepositoryHttpClient setAuthentication(RepositoryAuthentication authentication) {
		this.authentication = authentication;
		return this;
	}
}
