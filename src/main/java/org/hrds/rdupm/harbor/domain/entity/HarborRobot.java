package org.hrds.rdupm.harbor.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.util.Date;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 制品库-harbor机器人账户表
 *
 * @author mofei.li@hand-china.com 2020-05-28 15:29:06
 */
@Getter
@Setter
@ApiModel("制品库-harbor机器人账户表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_robot")
public class HarborRobot extends AuditDomain {

    public static final String FIELD_ROBOT_ID = "robotId";
    public static final String FIELD_HARBOR_ROBOT_ID = "harborRobotId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ACTION = "action";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_TOKEN = "token";
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
    private Long robotId;
    @ApiModelProperty(value = "harbor机器人账户ID",required = true)
    @NotNull
    private Long harborRobotId;
    @ApiModelProperty(value = "猪齿鱼项目id",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "账户名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "功能，pull/push",required = true)
    @NotBlank
    private String action;
   @ApiModelProperty(value = "机器人账户描述，拉取/推送")    
    private String description;
    @ApiModelProperty(value = "是否启用，Y启用/N禁用",required = true)
    @NotBlank
    private String enableFlag;
    @ApiModelProperty(value = "机器人账户token",required = true)
    @NotBlank
    private String token;
    @ApiModelProperty(value = "账户到期时间",required = true)
    @NotNull
    private Date endDate;
    @ApiModelProperty(value = "组织id",required = true)
    @NotNull
    private Long organizationId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
	@ApiModelProperty(value = "harbor项目id")
	private Long harborProjectId;

	//
    // getter/setter
    // ------------------------------------------------------------------------------

}
