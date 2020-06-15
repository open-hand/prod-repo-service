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

/**
 * 制品库-项目与nexus服务关系表
 *
 * @author weisen.yang@hand-china.com 2020-06-10 20:33:59
 */
@ApiModel("制品库-项目与nexus服务关系表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_nexus_project_service")
@Setter
@Getter
public class NexusProjectService extends AuditDomain {

    public static final String FIELD_PROJECT_SERVICE_ID = "projectServiceId";
    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long projectServiceId;
    @ApiModelProperty(value = "rdupm_nexus_server_config 表主键",required = true)
    @NotNull
    private Long configId;
    @ApiModelProperty(value = "猪齿鱼项目ID",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "猪齿鱼组织ID",required = true)
    @NotNull
    private Long organizationId;
    @ApiModelProperty(value = "是否启用",required = true)
    @NotNull
    private Integer enableFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------
}
