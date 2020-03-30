package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 创建仓库组请求参数
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public class NexusServerMavenGroup {

	private String name;
	private String blobStoreName;
	private List<String> members;

	public String getName() {
		return name;
	}

	public NexusServerMavenGroup setName(String name) {
		this.name = name;
		return this;
	}

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusServerMavenGroup setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public List<String> getMembers() {
		return members;
	}

	public NexusServerMavenGroup setMembers(List<String> members) {
		this.members = members;
		return this;
	}
}
