package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
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
	@ApiModelProperty(value = "创建人")
	private String createdBy;
	@ApiModelProperty(value = "创建时间")
	private String creationDate;
	@ApiModelProperty(value = "更新时间")
	private String lastUpdateDate;
	@ApiModelProperty(value = "拉取时间")
	private String lastDownloadDate;
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
	private String repositoryUrl;

	@ApiModelProperty(value = "图标")
	private String creatorImageUrl;
	@ApiModelProperty(value = "登录名")
	private String creatorLoginName;
	@ApiModelProperty(value = "用户名")
	private String creatorRealName;

	@ApiModelProperty(value = "asset中，最新的jar/war版本path地址")
	private String path;
}
