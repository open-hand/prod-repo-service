package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
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
public class HarborImageVo {

	@SerializedName("id")
	private Long imageId;

	@SerializedName("name")
	private String imageName;

	private Long projectId;

	private String description;

	@SerializedName("pull_count")
	private Integer pullCount;

	@SerializedName("star_count")
	private Integer starCount;

	@SerializedName("tags_count")
	private Integer tagsCount;

	@SerializedName("creation_time")
	private String creaionTime;

	@SerializedName("update_time")
	private String updateTime;

}
