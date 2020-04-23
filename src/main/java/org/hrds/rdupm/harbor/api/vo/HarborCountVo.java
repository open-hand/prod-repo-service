package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * harbor项目、镜像数量
 *
 * @author chenxiuhong 2020/04/23 3:31 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HarborCountVo {
	@SerializedName("private_project_count")
	private Integer privateProjectCount;

	@SerializedName("private_repo_count")
	private Integer privateRepoCount;

	@SerializedName("public_project_count")
	private Integer publicProjectCount;

	@SerializedName("public_repo_count")
	private Integer publicRepoCount;

	@SerializedName("total_project_count")
	private Integer totalProjectCount;

	@SerializedName("total_repo_count")
	private Integer totalRepoCount;

}
