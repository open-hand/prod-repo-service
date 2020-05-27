package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 制品库_nexus权限表
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@ApiModel("制品库_nexus权限表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_nexus_auth")
@Getter
@Setter
public class NexusAuth extends AuditDomain {

    public static final String FIELD_AUTH_ID = "authId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_REAL_NAME = "realName";
    public static final String FIELD_ROLE_CODE = "roleCode";
    public static final String FIELD_NE_ROLE_ID = "neRoleId";
    public static final String FIELD_END_DATE = "endDate";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------


    public void setNeRoleIdByRoleCode(NexusRole nexusRole){
        if (this.roleCode.equals(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode())
                || this.roleCode.equals(NexusConstants.NexusRoleEnum.DEVELOPER.getRoleCode())) {
            this.neRoleId = nexusRole.getNeRoleId();
        } else if (this.roleCode.equals(NexusConstants.NexusRoleEnum.GUEST.getRoleCode())
                || this.roleCode.equals(NexusConstants.NexusRoleEnum.LIMITED_GUEST.getRoleCode())) {
            this.neRoleId = nexusRole.getNePullRoleId();
        } else {
            throw new CommonException("权限角色有误");
        }
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long authId;
    @ApiModelProperty(value = "猪齿鱼项目ID",required = true)
    private Long projectId;
    @ApiModelProperty(value = "组织ID",required = true)
    private Long organizationId;
    @ApiModelProperty(value = "rdupm_nexus_repository 表主键",required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "猪齿鱼用户ID",required = true)
    @NotNull
    private Long userId;
    @ExcelColumn(title = "登录名", order = 3)
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ExcelColumn(title = "用户名", order = 4)
    @ApiModelProperty(value = "用户名")
    private String realName;
    @ExcelColumn(title = "权限角色", renderers = HarborAuth.AuthorityValueRenderer.class, order = 6) // TODO
    @ApiModelProperty(value = "角色编码", required = true)
    @NotBlank
    private String roleCode;
    @ApiModelProperty(value = "用户对应nexus角色Id", required = true)
    private String neRoleId;
    @ExcelColumn(title = "有效期", pattern = BaseConstants.Pattern.DATE, order = 7)
    @ApiModelProperty(value = "有效期，必输")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @ExcelColumn(title = "仓库名称", order = 1)
	@Transient
	private String neRepositoryName;

    @ExcelColumn(title = "成员角色", order = 5)
    @Transient
    private String memberRole;

    @Transient
    private String userImageUrl;
    @ExcelColumn(title = "项目名称", order = 2)
    @Transient
    private String projectName;


    //
    // getter/setter
    // ------------------------------------------------------------------------------



}
