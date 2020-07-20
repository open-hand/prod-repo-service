package org.hrds.rdupm.nexus.api.dto;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Transient;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("仓库信息")
@Getter
@Setter
@ToString
public class NexusRepositoryOrgDTO implements SecurityToken, Comparator<NexusRepositoryOrgDTO> {
	private String _token;

	private Long configId;
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

	@Override
	public Class<? extends SecurityToken> associateEntityClass() {
		return NexusRepositoryOrgDTO.class;
	}

	@Override
	public int compare(NexusRepositoryOrgDTO o1, NexusRepositoryOrgDTO o2) {
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
