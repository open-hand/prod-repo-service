package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * nexus服务信息
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public class NexusServer {
	private String password;
	private String username;
	private String baseUrl;
	private String serverIp;

	public NexusServer(String baseUrl, String username, String password) {
		this.password = password;
		this.username = username;
		this.baseUrl = baseUrl;

	}

	public String getPassword() {
		return password;
	}

	public NexusServer setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public NexusServer setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public NexusServer setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public String getServerIp() {
		return serverIp;
	}

	public NexusServer setServerIp(String serverIp) {
		this.serverIp = serverIp;
		return this;
	}
}
