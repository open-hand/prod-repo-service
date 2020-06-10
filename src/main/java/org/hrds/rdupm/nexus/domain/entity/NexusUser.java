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
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class NexusUser extends AuditDomain {

    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
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
	@ApiModelProperty(value = "仓库默认发布用户Id")
	private String neUserId;
	@ApiModelProperty(value = "仓库默认发布用户密码")
	private String neUserPassword;
	@ApiModelProperty(value = "仓库默认拉取用户Id")
	private String nePullUserId;
	@ApiModelProperty(value = "仓库默认拉取用户密码")
	private String nePullUserPassword;
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

	@ApiModelProperty(value = "默认管理仓库名称")
	@Transient
	private List<String> defaultRepositoryNames;

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
}
