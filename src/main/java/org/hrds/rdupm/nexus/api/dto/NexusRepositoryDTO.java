package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.Transient;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("仓库信息")
@Getter
@Setter
@ToString
public class NexusRepositoryDTO implements SecurityToken, Comparator<NexusRepositoryDTO> {
	private String _token;

	/**
	 * 类转换
	 * @param nexusRepository 数据库仓库数据
	 * @param nexusServerRepository nexus服务仓库数据
	 */
	public void convert(NexusRepository nexusRepository, NexusServerRepository nexusServerRepository) {
		if (nexusRepository != null) {
			this.configId = nexusRepository.getConfigId();
			this.repositoryId = nexusRepository.getRepositoryId();
			this.neRepositoryName = nexusRepository.getNeRepositoryName();
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
			this.enableFlag = nexusRepository.getEnableFlag();
			this.enableAnonymousFlag = nexusRepository.getEnableAnonymousFlag();
			this.downloadTimes = nexusRepository.getDownloadTimes();
			this.personTimes = nexusRepository.getPersonTimes();
		}
		if (nexusServerRepository != null) {
			this.name = nexusServerRepository.getName();
			this.type = nexusServerRepository.getType();
			this.versionPolicy = nexusServerRepository.getVersionPolicy();
			this.writePolicy = nexusServerRepository.getWritePolicy();
			this.online = nexusServerRepository.getOnline();
			this.url = nexusServerRepository.getUrl() + "/";

			this.blobStoreName = nexusServerRepository.getBlobStoreName();
			this.repoMemberList = nexusServerRepository.getRepoMemberList();
			this.remoteUrl = nexusServerRepository.getRemoteUrl();
			this.remoteUsername = nexusServerRepository.getRemoteUsername();
		}
	}
	@Encrypt
	private Long configId;
	@Encrypt
	private Long repositoryId;
	@ApiModelProperty(value = "仓库名称")
	private String neRepositoryName;
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
	@ApiModelProperty(value = "仓库是否启用")
	private String enableFlag;
	@Transient
	private Integer enableAnonymousFlag;

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

	@ApiModelProperty(value = "仓库包的拉取总次数")
	private Long downloadTimes;

	@ApiModelProperty(value = "仓库拉取包的人数")
	private Long personTimes;
	@ApiModelProperty(value = "内部url")
	private String internalUrl;

	@Override
	public Class<? extends SecurityToken> associateEntityClass() {
		return NexusRepositoryDTO.class;
	}

	@Override
	public int compare(NexusRepositoryDTO o1, NexusRepositoryDTO o2) {
		if (Objects.nonNull(o1.getProjectId()) && Objects.nonNull(o2.getProjectId())) {
			return o2.getRepositoryId().compareTo(o1.getRepositoryId());
		} else if (Objects.nonNull(o1.getProjectId())) {
			return 1;
		} else if (Objects.nonNull(o2.getProjectId())) {
			return -1;
		} else {
			return 0;
		}
	}
}
