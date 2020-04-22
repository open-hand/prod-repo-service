package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * maven 仓库创建、更新、查询请求信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryMavenInfo {

	/**
	 * 将请求返回数据转换为NexusServerRepository
	 * @return NexusServerRepository
	 */
	public NexusServerRepository covertNexusServerRepository(){
		NexusServerRepository nexusServerRepository = new NexusServerRepository();
		nexusServerRepository.setName(this.getName());
		nexusServerRepository.setFormat(this.getFormat());
		nexusServerRepository.setUrl(this.getUrl());
		nexusServerRepository.setOnline(this.getOnline());
		nexusServerRepository.setType(this.getType());
		RepositoryMaven maven = this.getMaven();
		nexusServerRepository.setVersionPolicy(maven != null ? maven.getVersionPolicy() : null);
		RepositoryStorage storage = this.getStorage();
		nexusServerRepository.setBlobStoreName(storage != null ? storage.getBlobStoreName() : null);
		nexusServerRepository.setWritePolicy(storage != null ? storage.getWritePolicy() : null);
		RepositoryGroup group = this.getGroup();
		nexusServerRepository.setRepoMemberList(group != null ? group.getMemberNames() : null);
		RepositoryProxy proxy = this.getProxy();
		nexusServerRepository.setRemoteUrl(proxy != null ? proxy.getRemoteUrl() : null);
		RepositoryHttpClient httpClient = this.getHttpClient();
		RepositoryAuthentication authentication = httpClient != null ? httpClient.getAuthentication() : null;
		nexusServerRepository.setRemoteUsername(authentication != null ? authentication.getUsername() : null);
		nexusServerRepository.setRemotePassword(authentication != null ? authentication.getPassword() : null);
		return nexusServerRepository;
	}

	private String format;
	private String url;

	/**
	 * NexusConstants.RepositoryType 常量
	 */
	private String type;


	private String name;
	private Boolean online;
	private RepositoryStorage storage;
	private RepositoryCleanup cleanup;
	private RepositoryMaven maven;


	// maven proxy仓库信息
	private RepositoryProxy proxy;
	private RepositoryNegativeCache negativeCache;
	private RepositoryHttpClient httpClient;
	private String routingRule;

	// group 仓库信息
	private RepositoryGroup group;

	public String getType() {
		return type;
	}

	public RepositoryMavenInfo setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public RepositoryMavenInfo setName(String name) {
		this.name = name;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public RepositoryMavenInfo setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public RepositoryStorage getStorage() {
		return storage;
	}

	public RepositoryMavenInfo setStorage(RepositoryStorage storage) {
		this.storage = storage;
		return this;
	}

	public RepositoryCleanup getCleanup() {
		return cleanup;
	}

	public RepositoryMavenInfo setCleanup(RepositoryCleanup cleanup) {
		this.cleanup = cleanup;
		return this;
	}

	public RepositoryMaven getMaven() {
		return maven;
	}

	public RepositoryMavenInfo setMaven(RepositoryMaven maven) {
		this.maven = maven;
		return this;
	}

	public RepositoryProxy getProxy() {
		return proxy;
	}

	public RepositoryMavenInfo setProxy(RepositoryProxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public RepositoryNegativeCache getNegativeCache() {
		return negativeCache;
	}

	public RepositoryMavenInfo setNegativeCache(RepositoryNegativeCache negativeCache) {
		this.negativeCache = negativeCache;
		return this;
	}

	public RepositoryHttpClient getHttpClient() {
		return httpClient;
	}

	public RepositoryMavenInfo setHttpClient(RepositoryHttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	public String getRoutingRule() {
		return routingRule;
	}

	public RepositoryMavenInfo setRoutingRule(String routingRule) {
		this.routingRule = routingRule;
		return this;
	}

	public RepositoryGroup getGroup() {
		return group;
	}

	public RepositoryMavenInfo setGroup(RepositoryGroup group) {
		this.group = group;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public RepositoryMavenInfo setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public RepositoryMavenInfo setUrl(String url) {
		this.url = url;
		return this;
	}
}
