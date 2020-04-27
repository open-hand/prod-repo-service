package org.hrds.rdupm.api.vo;

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



	public ProductLibraryDTO() {
	}
	/**
	 * @param nexusRepositoryDTO  maven仓库数据
	 */
	public ProductLibraryDTO(NexusRepositoryDTO nexusRepositoryDTO) {
		this.uniqueId = UUIDUtils.generateUUID();
		this.productType = TYPE_MAVEN;
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
	}

}
