package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 组件信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@ApiModel("包信息")
@Getter
@Setter
public class NexusServerComponent {
	@ApiModelProperty(value = "id")
	private String id;
	@ApiModelProperty(value = "仓库名称")
	private String repository;
	@ApiModelProperty(value = "format")
	private String format;
	@ApiModelProperty(value = "groupId")
	private String group;
	@ApiModelProperty(value = "artifactId")
	private String name;
	@ApiModelProperty(value = "版本")
	private String version;
	private List<NexusServerAsset> assets;

	@ApiModelProperty(value = "使用版本")
	private String useVersion;

	@ApiModelProperty(value = "下级Component的Id， 没有时就是id")
	private List<String> componentIds;
	@ApiModelProperty(value = "是否允许删除")
	private Boolean deleteFlag;
	@ApiModelProperty(value = "extension: pom、jar、war等")
	private String extension;

	private String downloadUrl;
	private String sha1;
	private String repositoryUrl;
}
