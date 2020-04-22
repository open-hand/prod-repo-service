package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库创建 maven信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryMaven {
	/**
	 * MIXED, SNAPSHOT, RELEASE
	 */
	private String versionPolicy;
	/**
	 * STRICT, PERMISSIVE
	 */
	private String layoutPolicy;

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public RepositoryMaven setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}

	public String getLayoutPolicy() {
		return layoutPolicy;
	}

	public RepositoryMaven setLayoutPolicy(String layoutPolicy) {
		this.layoutPolicy = layoutPolicy;
		return this;
	}
}
