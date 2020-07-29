package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

/**
 * 包查询dto
 * @author weisen.yang@hand-china.com 2020/4/2
 */
@ApiModel("包查询")
@Getter
@Setter
public class NexusComponentQuery {

	/**
	 * 转换查询参数 maven
	 * @return map
	 */
	public Map<String, Object> convertMavenParam(){
		Map<String, Object> paramMap = new HashMap<>(16);
		if (this.repositoryName != null) {
			paramMap.put("repository", this.repositoryName);
		}
		if (this.group != null) {
			paramMap.put("maven.groupId", this.group);
		}
		if (this.name != null) {
			paramMap.put("maven.artifactId", this.name);
		}
		if (this.version != null) {
			paramMap.put("maven.baseVersion", this.version);
		}
		return paramMap;
	}

	/**
	 * 转换查询参数 npm
	 * @return map
	 */
	public Map<String, Object> convertNpmParam(){
		Map<String, Object> paramMap = new HashMap<>(16);
		if (this.repositoryName != null) {
			paramMap.put("repository", this.repositoryName);
		}
		if (this.name != null) {
			paramMap.put("name", this.name);
		}
		if (this.version != null) {
			paramMap.put("version", this.version);
		}
		return paramMap;
	}

	@ApiModelProperty(value = "仓库名称", required = true)
	private String repositoryName;
	@ApiModelProperty(value = "仓库Id", required = true)
	private Long repositoryId;
	@ApiModelProperty(value = "groupId")
	private String group;
	@ApiModelProperty(value = "artifactId")
	private String name;
	@ApiModelProperty(value = "版本")
	private String version;
	@ApiModelProperty(value = "制品类型")
	private String repoType;
}
