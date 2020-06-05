package org.hrds.rdupm.common.api.vo;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hzero.core.util.UUIDUtils;
import org.springframework.beans.BeanUtils;

/**
 * 制品库DTO
 * @author weisen.yang@hand-china.com 2020/4/27
 */
@Getter
@Setter
@ApiModel("制品库DTO")
public class ProductLibraryDTO extends AuditDomain {

	public static final String TYPE_MAVEN = "MAVEN";
	public static final String TYPE_DOCKER = "DOCKER";
	public static final String TYPE_NPM = "NPM";

	@ApiModelProperty(value = "行记录唯一Id, UUID")
	private String uniqueId;
	@ApiModelProperty(value = "制品类型")
	private String productType;
	@ApiModelProperty(value = "组织ID")
	private Long organizationId;
	@ApiModelProperty(value = "项目ID")
	private Long projectId;
	@ApiModelProperty(value = "项目编码")
	private String projectCode;
	@ApiModelProperty(value = "创建人图标")
	private String creatorImageUrl;
	@ApiModelProperty(value = "创建人登录名")
	private String creatorLoginName;
	@ApiModelProperty(value = "创建人名称")
	private String creatorRealName;

	/**
	 * harbor
	 */
	@ApiModelProperty("harbor, 主键")
	private Long id;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "是否公开访问，默认false")
	private String publicFlag;
	@ApiModelProperty(value = "harbor项目ID")
	private Long harborId;
	@ApiModelProperty(value = "镜像数")
	private Integer repoCount;


	/**
	 * maven
	 */
	@ApiModelProperty("maven, 主键")
	private Long repositoryId;
	@ApiModelProperty(value = "仓库名称")
	private String repositoryName;
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


	public ProductLibraryDTO() {
	}
	/**
	 * @param nexusRepositoryDTO  maven npm 仓库数据
	 */
	public ProductLibraryDTO(NexusRepositoryDTO nexusRepositoryDTO, String repoType) {
		this.uniqueId = UUIDUtils.generateUUID();
		this.productType = repoType;
		this.repositoryName = nexusRepositoryDTO.getName();
		BeanUtils.copyProperties(nexusRepositoryDTO, this);
	}

	/**
	 * @param harborRepository docker仓库数据
	 */
	public ProductLibraryDTO(HarborRepository harborRepository) {
		this.uniqueId = UUIDUtils.generateUUID();
		this.productType = TYPE_DOCKER;
		BeanUtils.copyProperties(harborRepository, this);
		this.name = harborRepository.getCode();
		this.projectCode = harborRepository.getCode();
	}

}
