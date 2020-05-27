package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

import java.util.Date;

/**
 * 制品库_nexus日志表
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@ApiModel("制品库_nexus日志表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_nexus_log")
@Getter
@Setter
public class NexusLog extends AuditDomain {

    public static final String FIELD_LOG_ID = "logId";
    public static final String FIELD_OPERATOR_ID = "operatorId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_OPERATE_TYPE = "operateType";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_OPERATE_TIME = "operateTime";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long logId;
    @ApiModelProperty(value = "操作者ID",required = true)
    @NotNull
    private Long operatorId;
    @ApiModelProperty(value = "猪齿鱼项目ID",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "组织ID",required = true)
    @NotNull
    private Long organizationId;
    @ApiModelProperty(value = "rdupm_nexus_repository 表主键",required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "操作类型",required = true)
    @NotBlank
    private String operateType;
    @ApiModelProperty(value = "日志内容",required = true)
    @NotBlank
    private String content;
    @ApiModelProperty(value = "操作时间",required = true)
    @NotNull
    private Date operateTime;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


}
