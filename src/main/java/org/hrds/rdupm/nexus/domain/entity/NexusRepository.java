package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 制品库_nexus仓库信息表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@ApiModel("制品库_nexus仓库信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_repository")
public class NexusRepository extends AuditDomain {

    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_NE_REPOSITORY_NAME = "neRepositoryName";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ALLOW_ANONYMOUS = "allowAnonymous";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long repositoryId;
    @ApiModelProperty(value = "nexus服务配置ID: rdupm_nexus_server_config主键",required = true)
    private Long configId;
    @ApiModelProperty(value = "nexus仓库名称",required = true)
    @NotBlank
    private String neRepositoryName;
    @ApiModelProperty(value = "组织Id",required = true)
    @NotNull
    private Long organizationId;
    @ApiModelProperty(value = "项目id",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "是否允许匿名。1 允许；0 不允许",required = true)
    @NotNull
    private Integer allowAnonymous;
	@ApiModelProperty(value = "是否是关联仓库引入的。1 是；0 不是",required = true)
	@NotNull
	private Integer isRelated;
    @ApiModelProperty(value = "租户Id")
    private Long tenantId;

	@Transient
	private String creatorImageUrl;
	@Transient
	private String creatorLoginName;
	@Transient
	private String creatorRealName;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------


	@ApiModelProperty(value = "项目名称")
	@Transient
	private String projectName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Long repositoryId) {
		this.repositoryId = repositoryId;
	}
    /**
     * @return nexus服务配置ID: rdupm_nexus_server_config主键
     */
	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}
    /**
     * @return nexus仓库名称
     */
	public String getNeRepositoryName() {
		return neRepositoryName;
	}

	public void setNeRepositoryName(String neRepositoryName) {
		this.neRepositoryName = neRepositoryName;
	}
    /**
     * @return 组织Id
     */
	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
    /**
     * @return 项目id
     */
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
    /**
     * @return 是否允许匿名。1 允许；0 不允许
     */
	public Integer getAllowAnonymous() {
		return allowAnonymous;
	}

	public void setAllowAnonymous(Integer allowAnonymous) {
		this.allowAnonymous = allowAnonymous;
	}
    /**
     * @return 租户Id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getIsRelated() {
		return isRelated;
	}

	public NexusRepository setIsRelated(Integer isRelated) {
		this.isRelated = isRelated;
		return this;
	}

	public String getProjectName() {
		return projectName;
	}

	public NexusRepository setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public String getCreatorImageUrl() {
		return creatorImageUrl;
	}

	public NexusRepository setCreatorImageUrl(String creatorImageUrl) {
		this.creatorImageUrl = creatorImageUrl;
		return this;
	}

	public String getCreatorLoginName() {
		return creatorLoginName;
	}

	public NexusRepository setCreatorLoginName(String creatorLoginName) {
		this.creatorLoginName = creatorLoginName;
		return this;
	}

	public String getCreatorRealName() {
		return creatorRealName;
	}

	public NexusRepository setCreatorRealName(String creatorRealName) {
		this.creatorRealName = creatorRealName;
		return this;
	}
}
