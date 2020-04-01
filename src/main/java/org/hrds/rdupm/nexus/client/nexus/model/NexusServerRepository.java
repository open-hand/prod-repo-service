package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public class NexusServerRepository {

	private String name;
	private String format;
	private String url;
	private Boolean online;
	private String type;

	private String versionPolicy;
	private String blobStoreName;
	private String writePolicy;

	private List<String> repoMemberList;

	private String remoteUrl;
	private String remoteUsername;
	private String remotePassword;

	public String getName() {
		return name;
	}

	public NexusServerRepository setName(String name) {
		this.name = name;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerRepository setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public NexusServerRepository setUrl(String url) {
		this.url = url;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public NexusServerRepository setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusServerRepository setType(String type) {
		this.type = type;
		return this;
	}

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public NexusServerRepository setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusServerRepository setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public String getWritePolicy() {
		return writePolicy;
	}

	public NexusServerRepository setWritePolicy(String writePolicy) {
		this.writePolicy = writePolicy;
		return this;
	}

	public List<String> getRepoMemberList() {
		return repoMemberList;
	}

	public NexusServerRepository setRepoMemberList(List<String> repoMemberList) {
		this.repoMemberList = repoMemberList;
		return this;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public NexusServerRepository setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		return this;
	}

	public String getRemoteUsername() {
		return remoteUsername;
	}

	public NexusServerRepository setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
		return this;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public NexusServerRepository setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
		return this;
	}
}
