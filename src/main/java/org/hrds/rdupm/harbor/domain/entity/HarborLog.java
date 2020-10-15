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
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 制品库-harbor日志表
 *
 * @author xiuhong.chen@hand-china.com 2020-04-29 14:54:57
 */
@ApiModel("制品库-harbor日志表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_log")
@Getter
@Setter
public class HarborLog extends AuditDomain {

    public static final String FIELD_LOG_ID = "logId";
    public static final String FIELD_OPERATOR_ID = "operatorId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_OPERATE_TYPE = "operateType";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_OPERATE_TIME = "operateTime";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_LAST_UPDATE_LOGIN = "lastUpdateLogin";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
	@Encrypt
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
    @ApiModelProperty(value = "操作类型",required = true)
    @NotBlank
    private String operateType;
    @ApiModelProperty(value = "日志内容",required = true)
    @NotBlank
    private String content;
    @ApiModelProperty(value = "操作时间",required = true)
    @NotNull
    private Date operateTime;


	@Transient
	@ApiModelProperty("项目编码")
	private String projectCode;

    @Transient
	@ApiModelProperty("项目名称")
	private String projectName;

    @Transient
	@ApiModelProperty("项目图标URL")
	private String projectImageUrl;

	public HarborLog(){}

	public HarborLog(@NotNull Long projectId, @NotNull Long organizationId, @NotBlank String operateType, String content, Date startDate, Date endDate) {
		this.projectId = projectId;
		this.organizationId = organizationId;
		this.operateType = operateType;
		this.content = content;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Transient
	private Date startDate;

    @Transient
	private Date endDate;


}
