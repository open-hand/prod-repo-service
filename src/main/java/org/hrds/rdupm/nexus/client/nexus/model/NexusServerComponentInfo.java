package org.hrds.rdupm.nexus.client.nexus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@ApiModel("包信息")
@Getter
@Setter
public class NexusServerComponentInfo {

	public static String FIELD_CREATE_BY = "createdBy";
	public static String FIELD_CREATION_DATE = "creationDate";
	public static String FIELD_LAST_DOWNLOAD_DATE = "lastDownloadDate";

	@ApiModelProperty(value = "id")
	private String id;
	@ApiModelProperty(value = "成员components列表，Id集合")
	private List<String> componentIds;
	private String path;
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
	@ApiModelProperty(value = "使用版本")
	private String useVersion;
	@ApiModelProperty(value = "是否允许删除")
	private Boolean deleteFlag;
	@ApiModelProperty(value = "extension: pom、jar、war等")
	private String extension;
	@ApiModelProperty(value = "更新时间")
	private String lastUpdateDate;
	private List<NexusServerComponent> components;

	@ApiModelProperty(value = "版本数（NPM使用）")
	private int versionCount;
	@ApiModelProperty(value = "最新版本（NPM使用）")
	private String newestVersion;

	@ApiModelProperty(value = "项目名称")
	private String projectName;
	@ApiModelProperty(value = "项目图标")
	private String projectImgUrl;


	// maven release 包时，显示components中的值。
	@ApiModelProperty(value = "创建人")
	private String createdBy;
	@ApiModelProperty(value = "创建时间")
	private String creationDate;
	@ApiModelProperty(value = "拉取时间")
	private String lastDownloadDate;
	@ApiModelProperty(value = "图标")
	private String creatorImageUrl;
	@ApiModelProperty(value = "登录名")
	private String creatorLoginName;
	@ApiModelProperty(value = "用户名")
	private String creatorRealName;


}
