package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hzero.mybatis.domian.SecurityToken;

import java.util.Date;
import java.util.List;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("仓库信息")
public class NexusRepositoryDTO implements SecurityToken {
	private String _token;

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
			this._token = nexusRepository.get_token();
			this.projectId = nexusRepository.getProjectId();
			this.organizationId = nexusRepository.getOrganizationId();
			this.projectName = nexusRepository.getProjectName();

			this.createdBy = nexusRepository.getCreatedBy();
			this.creationDate = nexusRepository.getCreationDate();
			this.creatorImageUrl = nexusRepository.getCreatorImageUrl();
			this.creatorLoginName = nexusRepository.getCreatorLoginName();
			this.creatorRealName = nexusRepository.getCreatorRealName();
		}
		if (nexusServerRepository != null) {
			this.name = nexusServerRepository.getName();
			this.type = nexusServerRepository.getType();
			this.versionPolicy = nexusServerRepository.getVersionPolicy();
			this.writePolicy = nexusServerRepository.getWritePolicy();
			this.online = nexusServerRepository.getOnline();
			this.url = nexusServerRepository.getUrl();

			this.blobStoreName = nexusServerRepository.getBlobStoreName();
			this.repoMemberList = nexusServerRepository.getRepoMemberList();
			this.remoteUrl = nexusServerRepository.getRemoteUrl();
			this.remoteUsername = nexusServerRepository.getRemoteUsername();
		}
	}


	private Long repositoryId;
	@ApiModelProperty(value = "仓库名称")
	private String name;
	@ApiModelProperty(value = "仓库类型")
	private String type;
	@ApiModelProperty(value = "仓库策略")
	private String versionPolicy;
	@ApiModelProperty(value = "版本策略")
	private String writePolicy;
	@ApiModelProperty(value = "在线状态")
	private Boolean online;
	@ApiModelProperty(value = "访问url")
	private String url;
	@ApiModelProperty(value = "是否允许匿名访问")
	private Integer allowAnonymous;

	@ApiModelProperty(value = "存储器")
	private String blobStoreName;

	@ApiModelProperty(value = "创建仓库组（group）时，仓库成员")
	private List<String> repoMemberList;

	@ApiModelProperty(value = "远程仓库地址")
	private String remoteUrl;
	@ApiModelProperty(value = "远程仓库账号")
	private String remoteUsername;
	@ApiModelProperty(value = "远程仓库密码")
	private String remotePassword;

	@ApiModelProperty(value = "项目Id")
	private Long projectId;
	@ApiModelProperty(value = "项目名称")
	private String projectName;
	@ApiModelProperty(value = "组织Id")
	private Long organizationId;

	private Long createdBy;
	private Date creationDate;
	@ApiModelProperty(value = "创建人图标")
	private String creatorImageUrl;
	@ApiModelProperty(value = "创建人登录名")
	private String creatorLoginName;
	@ApiModelProperty(value = "创建人名称")
	private String creatorRealName;
	@ApiModelProperty(value = "项目图标")
	private String projectImgUrl;

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

	public String getBlobStoreName() {
		return blobStoreName;
	}

	public NexusRepositoryDTO setBlobStoreName(String blobStoreName) {
		this.blobStoreName = blobStoreName;
		return this;
	}

	public List<String> getRepoMemberList() {
		return repoMemberList;
	}

	public NexusRepositoryDTO setRepoMemberList(List<String> repoMemberList) {
		this.repoMemberList = repoMemberList;
		return this;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public NexusRepositoryDTO setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		return this;
	}

	public String getRemoteUsername() {
		return remoteUsername;
	}

	public NexusRepositoryDTO setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
		return this;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public NexusRepositoryDTO setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
		return this;
	}

	public String getWritePolicy() {
		return writePolicy;
	}

	public NexusRepositoryDTO setWritePolicy(String writePolicy) {
		this.writePolicy = writePolicy;
		return this;
	}

	public String getProjectName() {
		return projectName;
	}

	public NexusRepositoryDTO setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public Long getProjectId() {
		return projectId;
	}

	public NexusRepositoryDTO setProjectId(Long projectId) {
		this.projectId = projectId;
		return this;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public NexusRepositoryDTO setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public NexusRepositoryDTO setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public NexusRepositoryDTO setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getCreatorImageUrl() {
		return creatorImageUrl;
	}

	public NexusRepositoryDTO setCreatorImageUrl(String creatorImageUrl) {
		this.creatorImageUrl = creatorImageUrl;
		return this;
	}

	public String getCreatorLoginName() {
		return creatorLoginName;
	}

	public NexusRepositoryDTO setCreatorLoginName(String creatorLoginName) {
		this.creatorLoginName = creatorLoginName;
		return this;
	}

	public String getCreatorRealName() {
		return creatorRealName;
	}

	public NexusRepositoryDTO setCreatorRealName(String creatorRealName) {
		this.creatorRealName = creatorRealName;
		return this;
	}

	public String getProjectImgUrl() {
		return projectImgUrl;
	}

	public NexusRepositoryDTO setProjectImgUrl(String projectImgUrl) {
		this.projectImgUrl = projectImgUrl;
		return this;
	}

	@Override
	public String get_token() {
		return _token;
	}

	@Override
	public void set_token(String _token) {
		this._token = _token;
	}

	@Override
	public Class<? extends SecurityToken> associateEntityClass() {
		return NexusRepositoryDTO.class;
	}
}
