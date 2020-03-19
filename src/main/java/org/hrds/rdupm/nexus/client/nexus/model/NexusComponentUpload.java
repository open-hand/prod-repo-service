package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 组件提交jar请求
 * @author weisen.yang@hand-china.com 2020/3/19
 */
public class NexusComponentUpload {

	public static final String REPOSITORY_NAME = "repository";
	public static final String GROUP_ID = "maven2.groupId";
	public static final String ARTIFACT_ID = "maven2.artifactId";
	public static final String VERSION = "maven2.version";
	public static final String ASSET_FILE = "maven2.asset{num}";
	public static final String ASSET_EXTENSION = "maven2.asset{num}.extension";
	/**
	 * 是否自动创建pom文件
	 */
	public static final String GENERATE_POM = "maven2.generate-pom";


	private String repositoryName;
	private String groupId;
	private String artifactId;
	private String version;
	private List<NexusAssetUpload> assetUploads;

	public String getRepositoryName() {
		return repositoryName;
	}

	public NexusComponentUpload setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public NexusComponentUpload setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public NexusComponentUpload setArtifactId(String artifactId) {
		this.artifactId = artifactId;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public NexusComponentUpload setVersion(String version) {
		this.version = version;
		return this;
	}

	public List<NexusAssetUpload> getAssetUploads() {
		return assetUploads;
	}

	public NexusComponentUpload setAssetUploads(List<NexusAssetUpload> assetUploads) {
		this.assetUploads = assetUploads;
		return this;
	}
}
