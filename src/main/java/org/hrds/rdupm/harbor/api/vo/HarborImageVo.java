package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 3:31 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborImageVo {
	@ApiModelProperty("镜像ID")
	@SerializedName("id")
	private Long imageId;

	@ApiModelProperty("仓库名称/镜像名称")
	@SerializedName("name")
	private String repoName;

	@ApiModelProperty("镜像名称")
	private String imageName;

	@ApiModelProperty("镜像仓库ID")
	@SerializedName("project_id")
	private Long harborId;

	@ApiModelProperty("镜像描述")
	private String description;

	@ApiModelProperty("下载数量")
	@SerializedName("pull_count")
	private Integer pullCount;

	@ApiModelProperty("收藏数量")
	@SerializedName("star_count")
	private Integer starCount;

	@ApiModelProperty("镜像TAG数量")
	@SerializedName("tags_count")
	private Integer tagsCount;

	@ApiModelProperty("创建时间")
	@SerializedName("creation_time")
	private String creaionTime;

	@ApiModelProperty("更新时间")
	@SerializedName("update_time")
	private String updateTime;

}
