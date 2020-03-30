package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 创建代理仓库请求参数
 * @author weisen.yang@hand-china.com 2020/3/27
 */
public class NexusServerMavenProxy {

	private String name;
	private String remoteUrl;
	private String blobStoreName;
	private Boolean strictContentValidation;
	private String versionPolicy;
	private String layoutPolicy;
	private String remoteUsername;
	private String remotePassword;

	public String getName() {
		return name;
	}

	public NexusServerMavenProxy setName(String name) {
		this.name = name;
		return this;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public NexusServerMavenProxy setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		return this;
	}

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusServerMavenProxy setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public Boolean getStrictContentValidation() {
		return strictContentValidation;
	}

	public NexusServerMavenProxy setStrictContentValidation(Boolean strictContentValidation) {
		this.strictContentValidation = strictContentValidation;
		return this;
	}

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public NexusServerMavenProxy setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}

	public String getLayoutPolicy() {
		return layoutPolicy;
	}

	public NexusServerMavenProxy setLayoutPolicy(String layoutPolicy) {
		this.layoutPolicy = layoutPolicy;
		return this;
	}

	public String getRemoteUsername() {
		return remoteUsername;
	}

	public NexusServerMavenProxy setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
		return this;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public NexusServerMavenProxy setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
		return this;
	}
}
