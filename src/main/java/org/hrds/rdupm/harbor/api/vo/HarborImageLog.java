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
 * @author chenxiuhong 2020/04/29 7:13 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborImageLog {

	@SerializedName("username")
	private String loginName;

	@SerializedName("project_id")
	private Long harborId;

	@SerializedName("repo_name")
	private String repoName;

	@SerializedName("repo_tag")
	private String tagName;

	@SerializedName("operation")
	private String operateType;

	@SerializedName("op_time")
	private String operateTime;

	@ApiModelProperty("用户头像地址")
	private String userImageUrl;

	@ApiModelProperty("日志内容")
	private String content;


	public HarborImageLog(){

	}

}
