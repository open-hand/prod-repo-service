package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库创建 Cleanup信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryCleanup {
	private String policyNames;

	public String getPolicyNames() {
		return policyNames;
	}

	public RepositoryCleanup setPolicyNames(String policyNames) {
		this.policyNames = policyNames;
		return this;
	}
}
