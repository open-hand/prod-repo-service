package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库 Connection信息
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class RepositoryConnection {
	private Long retries;
	private String userAgentSuffix;
	private Long timeout;
	private Boolean enableCircularRedirects;
	private Boolean enableCookies;

	public Long getRetries() {
		return retries;
	}

	public RepositoryConnection setRetries(Long retries) {
		this.retries = retries;
		return this;
	}

	public String getUserAgentSuffix() {
		return userAgentSuffix;
	}

	public RepositoryConnection setUserAgentSuffix(String userAgentSuffix) {
		this.userAgentSuffix = userAgentSuffix;
		return this;
	}

	public Long getTimeout() {
		return timeout;
	}

	public RepositoryConnection setTimeout(Long timeout) {
		this.timeout = timeout;
		return this;
	}

	public Boolean getEnableCircularRedirects() {
		return enableCircularRedirects;
	}

	public RepositoryConnection setEnableCircularRedirects(Boolean enableCircularRedirects) {
		this.enableCircularRedirects = enableCircularRedirects;
		return this;
	}

	public Boolean getEnableCookies() {
		return enableCookies;
	}

	public RepositoryConnection setEnableCookies(Boolean enableCookies) {
		this.enableCookies = enableCookies;
		return this;
	}
}
