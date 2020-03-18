package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * maven hosted仓库创建、更新请求信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryMavenRequest {
	/**
	 * NexusConstants.RepositoryType 常量
	 */
	private String type;

	private String name;
	private Boolean online;
	private RepositoryStorage storage;
	private RepositoryCleanup cleanup;
	private RepositoryMaven maven;


	// 创建maven proxy仓库需要信息
	private RepositoryProxy proxy;
	private RepositoryNegativeCache negativeCache;
	private RepositoryHttpClient httpClient;
	private String routingRule;

	public String getType() {
		return type;
	}

	public RepositoryMavenRequest setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public RepositoryMavenRequest setName(String name) {
		this.name = name;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public RepositoryMavenRequest setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public RepositoryStorage getStorage() {
		return storage;
	}

	public RepositoryMavenRequest setStorage(RepositoryStorage storage) {
		this.storage = storage;
		return this;
	}

	public RepositoryCleanup getCleanup() {
		return cleanup;
	}

	public RepositoryMavenRequest setCleanup(RepositoryCleanup cleanup) {
		this.cleanup = cleanup;
		return this;
	}

	public RepositoryMaven getMaven() {
		return maven;
	}

	public RepositoryMavenRequest setMaven(RepositoryMaven maven) {
		this.maven = maven;
		return this;
	}

	public RepositoryProxy getProxy() {
		return proxy;
	}

	public RepositoryMavenRequest setProxy(RepositoryProxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public RepositoryNegativeCache getNegativeCache() {
		return negativeCache;
	}

	public RepositoryMavenRequest setNegativeCache(RepositoryNegativeCache negativeCache) {
		this.negativeCache = negativeCache;
		return this;
	}

	public RepositoryHttpClient getHttpClient() {
		return httpClient;
	}

	public RepositoryMavenRequest setHttpClient(RepositoryHttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	public String getRoutingRule() {
		return routingRule;
	}

	public RepositoryMavenRequest setRoutingRule(String routingRule) {
		this.routingRule = routingRule;
		return this;
	}
}
