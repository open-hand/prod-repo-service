package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/30
 */
public class NexusRepositoryDTO {

	/**
	 * 类转换
	 * @param nexusRepository 数据库仓库数据
	 * @param nexusServerRepository nexus服务仓库数据
	 */
	public void convert(NexusRepository nexusRepository, NexusServerRepository nexusServerRepository) {
		if (nexusRepository != null) {
			this.repositoryId = nexusRepository.getRepositoryId();
			this.name = nexusRepository.getNeRepositoryName();
			this.allowAnonymous = nexusRepository.getAllowAnonymous();
		}
		if (nexusServerRepository != null) {
			this.name = nexusServerRepository.getName();
			this.type = nexusServerRepository.getType();
			this.versionPolicy = nexusServerRepository.getVersionPolicy();
			this.online = nexusServerRepository.getOnline();
			this.url = nexusServerRepository.getUrl();
		}


	}


	private Long repositoryId;
	@ApiModelProperty(value = "仓库名称")
	private String name;
	@ApiModelProperty(value = "仓库类型")
	private String type;
	@ApiModelProperty(value = "仓库策略")
	private String versionPolicy;
	@ApiModelProperty(value = "在线状态")
	private Boolean online;
	@ApiModelProperty(value = "访问url")
	private String url;
	@ApiModelProperty(value = "是否允许匿名访问")
	private Integer allowAnonymous;

	public Long getRepositoryId() {
		return repositoryId;
	}

	public NexusRepositoryDTO setRepositoryId(Long repositoryId) {
		this.repositoryId = repositoryId;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusRepositoryDTO setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusRepositoryDTO setType(String type) {
		this.type = type;
		return this;
	}

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public NexusRepositoryDTO setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}

	public Boolean getOnline() {
		return online;
	}

	public NexusRepositoryDTO setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public NexusRepositoryDTO setUrl(String url) {
		this.url = url;
		return this;
	}

	public Integer getAllowAnonymous() {
		return allowAnonymous;
	}

	public NexusRepositoryDTO setAllowAnonymous(Integer allowAnonymous) {
		this.allowAnonymous = allowAnonymous;
		return this;
	}
}
