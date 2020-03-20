package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public class NexusMavenGroup {
	public static final String SCRIPT_CREATE_NAME = "hrds.create_maven_group";


	private String groupName;
	private String blobStoreName;
	private List<String> members;

	public String getGroupName() {
		return groupName;
	}

	public NexusMavenGroup setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusMavenGroup setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public List<String> getMembers() {
		return members;
	}

	public NexusMavenGroup setMembers(List<String> members) {
		this.members = members;
		return this;
	}
}
