package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 制品库DTO
 * @author weisen.yang@hand-china.com 2020/3/27
 */
public class ProdRepositoryDTO {
	@ApiModelProperty(value = "制品仓库类型",required = true)
	@NotBlank
	private String type;
	@ApiModelProperty(value = "制品仓库类型")
	private String name;

	@ApiModelProperty(value = "maven 制品仓库信息")
	private ProdMavenDTO prodMavenDTO;
	@ApiModelProperty(value = "创建maven制品时，是否创建nexus仓库")
	private Boolean nexusRepoFlag;

	public String getType() {
		return type;
	}

	public ProdRepositoryDTO setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public ProdRepositoryDTO setName(String name) {
		this.name = name;
		return this;
	}

	public ProdMavenDTO getProdMavenDTO() {
		return prodMavenDTO;
	}

	public ProdRepositoryDTO setProdMavenDTO(ProdMavenDTO prodMavenDTO) {
		this.prodMavenDTO = prodMavenDTO;
		return this;
	}

	public Boolean getNexusRepoFlag() {
		return nexusRepoFlag;
	}

	public ProdRepositoryDTO setNexusRepoFlag(Boolean nexusRepoFlag) {
		this.nexusRepoFlag = nexusRepoFlag;
		return this;
	}
}
