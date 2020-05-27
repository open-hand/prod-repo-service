package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 制品库_nexus仓库角色信息表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@ApiModel("制品库_nexus仓库角色信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_role")
public class NexusRole extends AuditDomain {

    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_NE_PULL_ROLE_ID = "nePullRoleId";
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
    private Long roleId;
    @ApiModelProperty(value = "rdupm_nexus_repository表主键",required = true)
    @NotNull
    private Long repositoryId;
	@ApiModelProperty(value = "仓库默认拉取角色Id",required = true)
	private String nePullRoleId;
    @ApiModelProperty(value = "租户Id")
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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
     * @return 租户Id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getNePullRoleId() {
		return nePullRoleId;
	}

	public NexusRole setNePullRoleId(String nePullRoleId) {
		this.nePullRoleId = nePullRoleId;
		return this;
	}
}
