package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库 negativeCache信息
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class RepositoryNegativeCache {
	private Boolean enabled;
	private Long timeToLive;

	public Boolean getEnabled() {
		return enabled;
	}

	public RepositoryNegativeCache setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Long getTimeToLive() {
		return timeToLive;
	}

	public RepositoryNegativeCache setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
		return this;
	}
}
