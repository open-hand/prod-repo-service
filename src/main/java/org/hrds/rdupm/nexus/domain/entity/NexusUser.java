package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hzero.core.util.AssertUtils;

import java.util.List;

/**
 * 制品库_nexus仓库默认用户信息表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@ApiModel("制品库_nexus仓库默认用户信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_user")
public class NexusUser extends AuditDomain {

	/**
	 * 密码更改校验
	 */
	public void validChangePassword(){
		AssertUtils.notNull(this.userId, "userId is not null");
		if (StringUtils.isEmpty(this.neUserPassword)) {
			throw new CommonException(NexusMessageConstants.NEXUS_NEW_PASSWORD_NOT_NULL);
		}
		if (StringUtils.isEmpty(this.oldNeUserPassword)) {
			throw new CommonException(NexusMessageConstants.NEXUS_OLD_PASSWORD_NOT_NULL);
		}
	}

    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_NE_USER_ID = "neUserId";
    public static final String FIELD_NE_USER_PASSWORD = "neUserPassword";
    public static final String FIELD_IS_DEFAULT = "isDefault";
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
    private Long userId;
    @ApiModelProperty(value = "rdupm_nexus_repository表主键",required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "nexus 发布用户Id",required = true)
    @NotBlank
    private String neUserId;
    @ApiModelProperty(value = "nexus 发布用户密码")
    private String neUserPassword;
	@ApiModelProperty(value = "nexus 拉取用户Id")
	private String nePullUserId;
	@ApiModelProperty(value = "nexus 拉取用户密码")
	private String nePullUserPassword;
    @ApiModelProperty(value = "是否是该仓库默认管理用户",required = true)
    @NotNull
    private Integer isDefault;
    @ApiModelProperty(value = "租户Id")
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@ApiModelProperty(value = "默认仓库名称")
	@Transient
	private String neRepositoryName;

	@ApiModelProperty(value = "默认管理用户角色")
	@Transient
	private String neRoleId;

	@ApiModelProperty(value = "其它仓库名称")
	@Transient
	private List<String> otherRepositoryName;

	@ApiModelProperty(value = "组织Id")
	@Transient
	private Long organizationId;
	@ApiModelProperty(value = "项目id")
	@Transient
	private Long projectId;



	@ApiModelProperty(value = "旧密码")
	@Transient
	private String oldNeUserPassword;

	@ApiModelProperty(value = "是否可编辑-项目层")
	@Transient
	private Boolean editFlag;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    /**
     * @return rdupm_nexus_repository表主键
     */
	public Long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Long repositoryId) {
		this.repositoryId = repositoryId;
	}
    /**
     * @return nexus用户Id
     */
	public String getNeUserId() {
		return neUserId;
	}

	public void setNeUserId(String neUserId) {
		this.neUserId = neUserId;
	}
    /**
     * @return nexus用户密码
     */
	public String getNeUserPassword() {
		return neUserPassword;
	}

	public void setNeUserPassword(String neUserPassword) {
		this.neUserPassword = neUserPassword;
	}
    /**
     * @return 是否是该仓库默认管理用户
     */
	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
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

	public String getNePullUserId() {
		return nePullUserId;
	}

	public NexusUser setNePullUserId(String nePullUserId) {
		this.nePullUserId = nePullUserId;
		return this;
	}

	public String getNePullUserPassword() {
		return nePullUserPassword;
	}

	public NexusUser setNePullUserPassword(String nePullUserPassword) {
		this.nePullUserPassword = nePullUserPassword;
		return this;
	}

	public String getNeRepositoryName() {
		return neRepositoryName;
	}

	public NexusUser setNeRepositoryName(String neRepositoryName) {
		this.neRepositoryName = neRepositoryName;
		return this;
	}

	public List<String> getOtherRepositoryName() {
		return otherRepositoryName;
	}

	public NexusUser setOtherRepositoryName(List<String> otherRepositoryName) {
		this.otherRepositoryName = otherRepositoryName;
		return this;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public NexusUser setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public Long getProjectId() {
		return projectId;
	}

	public NexusUser setProjectId(Long projectId) {
		this.projectId = projectId;
		return this;
	}

	public String getOldNeUserPassword() {
		return oldNeUserPassword;
	}

	public NexusUser setOldNeUserPassword(String oldNeUserPassword) {
		this.oldNeUserPassword = oldNeUserPassword;
		return this;
	}

	public String getNeRoleId() {
		return neRoleId;
	}

	public NexusUser setNeRoleId(String neRoleId) {
		this.neRoleId = neRoleId;
		return this;
	}

	public Boolean getEditFlag() {
		return editFlag;
	}

	public NexusUser setEditFlag(Boolean editFlag) {
		this.editFlag = editFlag;
		return this;
	}
}
