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
import lombok.Getter;
import lombok.Setter;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * 制品库_nexus仓库信息表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@ApiModel("制品库_nexus仓库信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_repository")
@Getter
@Setter
public class NexusRepository extends AuditDomain {
    public static final String ENCRYPT_KEY = "rdupm_nexus_repository";

    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_NE_REPOSITORY_NAME = "neRepositoryName";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ALLOW_ANONYMOUS = "allowAnonymous";
    public static final String FIELD_TENANT_ID = "tenantId";
	public static final String FIELD_REPO_TYPE = "repoType";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long repositoryId;
    @ApiModelProperty(value = "nexus服务配置ID: rdupm_nexus_server_config主键",required = true)
    private Long configId;
    @ApiModelProperty(value = "nexus仓库名称",required = true)
    @NotBlank
    private String neRepositoryName;
    @Transient
    private String name;
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
	@ApiModelProperty(value = "制品库类型")
	@NotNull
	private String repoType;
    @ApiModelProperty(value = "仓库是否启用")
    private String enableFlag;

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

	@ApiModelProperty(value = "用户权限信息", hidden = true)
    @Transient
	private List<NexusAuth> nexusAuthList;

    @ApiModelProperty(value = "删除团队成员， nexus（maven/npm）仓库权限处理: 删除用户Id", hidden = true)
	@Transient
    private Long deleteUserId;
    @Transient
    private Integer enableAnonymousFlag;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

}
