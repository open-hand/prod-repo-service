package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 4:21 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborImageReTag {
	private String srcRepoName;

	@ApiModelProperty("摘要")
	private String digest;

	@ApiModelProperty("目标镜像仓库名称")
	private String destProjectCode;

	@ApiModelProperty("目标镜像名称")
	private String destImageName;

	@ApiModelProperty("目标镜像版本号")
	private String destImageTagName;

}
