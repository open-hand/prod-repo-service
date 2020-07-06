package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 返回给猪齿鱼
 *
 * @author chenxiuhong 2020/04/24 11:37 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborC7nImageTagVo {

	@ApiModelProperty("TAG名称")
	@SerializedName("name")
	private String tagName;

}
