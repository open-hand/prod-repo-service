package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 组件提交jar请求
 * @author weisen.yang@hand-china.com 2020/3/19
 */
@ApiModel("包上传")
public class NexusServerComponentUpload {

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

	/**
	 * npm tgz上传
	 */
	public static final String NPM_TGX = "npm.asset";

	@ApiModelProperty(value = "仓库名称",required = true)
	@NotBlank
	private String repositoryName;
	@ApiModelProperty(value = "groupId",required = true)
	@NotBlank
	private String groupId;
	@ApiModelProperty(value = "artifactId",required = true)
	@NotBlank
	private String artifactId;
	@ApiModelProperty(value = "版本",required = true)
	@NotBlank
	private String version;
	private List<NexusServerAssetUpload> assetUploads;

	public String getRepositoryName() {
		return repositoryName;
	}

	public NexusServerComponentUpload setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public NexusServerComponentUpload setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public NexusServerComponentUpload setArtifactId(String artifactId) {
		this.artifactId = artifactId;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public NexusServerComponentUpload setVersion(String version) {
		this.version = version;
		return this;
	}

	public List<NexusServerAssetUpload> getAssetUploads() {
		return assetUploads;
	}

	public NexusServerComponentUpload setAssetUploads(List<NexusServerAssetUpload> assetUploads) {
		this.assetUploads = assetUploads;
		return this;
	}
}
