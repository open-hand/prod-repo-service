package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 仓库列表查询DTO
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("仓库列表查询")
@Getter
@Setter
public class NexusRepositoryQueryDTO {
	@ApiModelProperty(value = "仓库名称")
	private String repositoryName;
	@ApiModelProperty(value = "类型")
	private String type;
	@ApiModelProperty(value = "策略")
	private String versionPolicy;
	@ApiModelProperty(value = "是否查询已分配的仓库")
	private Integer distributedQueryFlag;
	@ApiModelProperty(value = "项目Id")
	private Long projectId;
	@ApiModelProperty(value = "组织Id", hidden = true)
	private Long organizationId;
	@ApiModelProperty(value = "制品库-类型", hidden = true)
	private String repoType;
}
