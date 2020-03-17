package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库创建、更新请求信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryRequest {
	/**
	 * NexusConstants.RepositoryType 常量
	 */
	private String type;

	private String name;
	private Boolean online;
	private RepositoryStorage storage;
	private RepositoryCleanup cleanup;
	private RepositoryMaven maven;

	public String getType() {
		return type;
	}

	public RepositoryRequest setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public RepositoryRequest setName(String name) {
		this.name = name;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public RepositoryRequest setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public RepositoryStorage getStorage() {
		return storage;
	}

	public RepositoryRequest setStorage(RepositoryStorage storage) {
		this.storage = storage;
		return this;
	}

	public RepositoryCleanup getCleanup() {
		return cleanup;
	}

	public RepositoryRequest setCleanup(RepositoryCleanup cleanup) {
		this.cleanup = cleanup;
		return this;
	}

	public RepositoryMaven getMaven() {
		return maven;
	}

	public RepositoryRequest setMaven(RepositoryMaven maven) {
		this.maven = maven;
		return this;
	}
}
