package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * group 仓库 group信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class RepositoryGroup {
	private List<String> memberNames;

	public List<String> getMemberNames() {
		return memberNames;
	}

	public RepositoryGroup setMemberNames(List<String> memberNames) {
		this.memberNames = memberNames;
		return this;
	}
}
