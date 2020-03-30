package org.hrds.rdupm.nexus.api.dto;

import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * maven 仓库创建dto
 * @author weisen.yang@hand-china.com 2020/3/27
 */
public class NexusRepositoryCreateDTO {

	/**
	 * 参数信息校验 TODO message定义
	 */
	public void validParam(){
		if (this.allowAnonymous == null) {
			throw new CommonException("是否允许匿名访问不能为空");
		}
		switch (this.getType()) {
			case NexusApiConstants.RepositoryType.HOSTED:
				// 创建本地仓库
				if (StringUtils.isBlank(this.versionPolicy)) {
					throw new CommonException("仓库策略不能为空");
				}
				if (StringUtils.isBlank(this.writePolicy)) {
					throw new CommonException("版本策略不能为空");
				}
				break;
			case NexusApiConstants.RepositoryType.PROXY:
				// 创建代理仓库
				if (StringUtils.isBlank(this.versionPolicy)) {
					throw new CommonException("仓库策略不能为空");
				}
				if (StringUtils.isBlank(this.remoteUrl)) {
					throw new CommonException("远程仓库地址不能为空");
				}
				if (StringUtils.isNotBlank(this.remoteUsername) && StringUtils.isBlank(this.remotePassword)) {
					throw new CommonException("填写了远程仓库账号，必须填写账号密码");
				}
				break;
			case NexusApiConstants.RepositoryType.GROUP:
				// 创建仓库组
				if (CollectionUtils.isEmpty(repoMemberList)) {
					throw new CommonException("仓库组成员不能为空");
				}
				break;
			default:
				throw new CommonException("仓库类型错误");
		}
	}

	/**
	 * 创建hosted仓库，参数组织
	 * @return RepositoryMavenRequest
	 */
	public RepositoryMavenRequest convertMavenHostedRequest(){
		RepositoryMavenRequest request = new RepositoryMavenRequest();
		request.setName(this.name);
		request.setOnline(Boolean.TRUE);

		RepositoryStorage storage = new RepositoryStorage();
		storage.setBlobStoreName(this.blobStoreName);
		storage.setStrictContentTypeValidation(Boolean.TRUE);
		storage.setWritePolicy(this.writePolicy);
		request.setStorage(storage);

		RepositoryMaven maven = new RepositoryMaven();
		maven.setLayoutPolicy("STRICT");
		maven.setVersionPolicy(this.versionPolicy);
		request.setMaven(maven);

		return request;
	}

	/**
	 * 创建proxy仓库，参数组织
	 * @return RepositoryMavenRequest
	 */
	public NexusServerMavenProxy convertMavenProxyRequest(){
		NexusServerMavenProxy nexusMavenProxy = new NexusServerMavenProxy();
		nexusMavenProxy.setName(this.name);
		nexusMavenProxy.setRemoteUrl(this.remoteUrl);
		nexusMavenProxy.setBlobStoreName(this.blobStoreName);
		nexusMavenProxy.setStrictContentValidation(Boolean.TRUE);
		nexusMavenProxy.setVersionPolicy(this.versionPolicy);
		nexusMavenProxy.setLayoutPolicy("STRICT");
		nexusMavenProxy.setRemoteUsername(this.remoteUsername);
		nexusMavenProxy.setRemotePassword(this.remotePassword);
		return nexusMavenProxy;
	}

	/**
	 * 创建maven仓库组，参数组织
	 * @return NexusMavenGroup
	 */
	public NexusServerMavenGroup convertMavenGroupRequest(){
		NexusServerMavenGroup nexusMavenGroup = new NexusServerMavenGroup();
		nexusMavenGroup.setName(this.name);
		nexusMavenGroup.setBlobStoreName(this.blobStoreName);
		nexusMavenGroup.setMembers(this.getRepoMemberList());
		return nexusMavenGroup;
	}


	/**
	 * hosted，proxy，group
	 */
	@ApiModelProperty(value = "仓库类型",required = true)
	@NotBlank
	private String type;
	@ApiModelProperty(value = "仓库名称",required = true)
	@NotBlank
	private String name;
	@ApiModelProperty(value = "存储器",required = true)
	@NotBlank
	private String blobStoreName;

	/**
	 * MIXED, SNAPSHOT, RELEASE
	 */
	@ApiModelProperty(value = "仓库策略")
	private String versionPolicy;
	/**
	 * DENY: 只读   ALLOW_ONCE：禁止覆盖  ALLOW：允许覆盖
	 */
	@ApiModelProperty(value = "版本策略")
	private String writePolicy;
	@ApiModelProperty(value = "是否允许匿名访问")
	private Integer allowAnonymous;

	@ApiModelProperty(value = "创建仓库组（group）时，仓库成员")
	private List<String> repoMemberList;

	@ApiModelProperty(value = "远程仓库地址")
	private String remoteUrl;
	@ApiModelProperty(value = "远程仓库账号")
	private String remoteUsername;
	@ApiModelProperty(value = "远程仓库密码")
	private String remotePassword;

	public String getType() {
		return type;
	}

	public NexusRepositoryCreateDTO setType(String type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusRepositoryCreateDTO setName(String name) {
		this.name = name;
		return this;
	}

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusRepositoryCreateDTO setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public String getVersionPolicy() {
		return versionPolicy;
	}

	public NexusRepositoryCreateDTO setVersionPolicy(String versionPolicy) {
		this.versionPolicy = versionPolicy;
		return this;
	}

	public String getWritePolicy() {
		return writePolicy;
	}

	public NexusRepositoryCreateDTO setWritePolicy(String writePolicy) {
		this.writePolicy = writePolicy;
		return this;
	}

	public Integer getAllowAnonymous() {
		return allowAnonymous;
	}

	public NexusRepositoryCreateDTO setAllowAnonymous(Integer allowAnonymous) {
		this.allowAnonymous = allowAnonymous;
		return this;
	}

	public List<String> getRepoMemberList() {
		return repoMemberList;
	}

	public NexusRepositoryCreateDTO setRepoMemberList(List<String> repoMemberList) {
		this.repoMemberList = repoMemberList;
		return this;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public NexusRepositoryCreateDTO setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		return this;
	}

	public String getRemoteUsername() {
		return remoteUsername;
	}

	public NexusRepositoryCreateDTO setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
		return this;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public NexusRepositoryCreateDTO setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
		return this;
	}
}
