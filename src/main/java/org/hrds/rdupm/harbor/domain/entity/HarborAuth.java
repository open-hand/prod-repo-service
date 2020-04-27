package org.hrds.rdupm.harbor.domain.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 制品库-harbor权限表
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
@Getter
@Setter
@ApiModel("制品库-harbor权限表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_auth")
public class HarborAuth extends AuditDomain {

    public static final String FIELD_AUTH_ID = "authId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_REAL_NAME = "realName";
    public static final String FIELD_HARBOR_ROLE_ID = "harborRoleId";
    public static final String FIELD_HARBOR_AUTH_ID = "harborAuthId";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_LAST_UPDATE_LOGIN = "lastUpdateLogin";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long authId;

    @ApiModelProperty(value = "猪齿鱼项目ID")
    private Long projectId;

    @ApiModelProperty(value = "猪齿鱼用户ID,必输")
    @NotNull
    private Long userId;

   @ApiModelProperty(value = "登录名，必输")
   @NotBlank
    private String loginName;

   @ApiModelProperty(value = "用户姓名，必输")
    private String realName;

    @ApiModelProperty(value = "harbor角色ID，必输")
    @NotNull
    private Long harborRoleId;

    @ApiModelProperty(value = "有效期，必输")
    @NotNull
    private Date endDate;

	@ApiModelProperty(value = "harbor权限ID")
	private Long harborAuthId;

	@ApiModelProperty(value = "组织ID")
    private Long organizationId;

    @Transient
	private Long harborId;

    @Transient
	private String userImageUrl;

    @Transient
	private String memberRole;

    @Transient
	private String code;

    @Transient
	private String name;

}
