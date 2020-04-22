package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库 proxy信息
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class RepositoryProxy {
	private String remoteUrl;
	private Long contentMaxAge;
	private Long metadataMaxAge;

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public RepositoryProxy setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		return this;
	}

	public Long getContentMaxAge() {
		return contentMaxAge;
	}

	public RepositoryProxy setContentMaxAge(Long contentMaxAge) {
		this.contentMaxAge = contentMaxAge;
		return this;
	}

	public Long getMetadataMaxAge() {
		return metadataMaxAge;
	}

	public RepositoryProxy setMetadataMaxAge(Long metadataMaxAge) {
		this.metadataMaxAge = metadataMaxAge;
		return this;
	}
}
