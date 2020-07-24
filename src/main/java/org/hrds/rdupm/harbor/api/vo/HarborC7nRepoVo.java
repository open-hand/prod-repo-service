package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 返回给猪齿鱼
 *
 * @author chenxiuhong 2020/04/24 11:37 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborC7nRepoVo {

	@ApiModelProperty("仓库ID")
	@Encrypt
	private Long repoId;

	@ApiModelProperty("仓库名称")
	private String repoName;

	@ApiModelProperty("仓库类型")
	private String repoType;

	public HarborC7nRepoVo(){}
	public HarborC7nRepoVo(Long repoId, String repoName, String repoType) {
		this.repoId = repoId;
		this.repoName = repoName;
		this.repoType = repoType;
	}
}
