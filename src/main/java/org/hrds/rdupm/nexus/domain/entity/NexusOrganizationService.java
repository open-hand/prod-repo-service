package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 制品库-租户与nexus服务关系表(NexusOrganizationService)实体类
 *
 * @author hao.wang08@hand-china.com
 * @since 2022-03-01 10:17:12
 */

@ApiModel("制品库-租户与nexus服务关系表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_nexus_organization_service")
public class NexusOrganizationService extends AuditDomain {
    private static final long serialVersionUID = -90279578906104605L;

    public static final String FIELD_ID = "id";
    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";

    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "rdupm_nexus_server_config 表主键", required = true)
    @NotNull
    private Long configId;

    @ApiModelProperty(value = "猪齿鱼组织ID", required = true)
    @NotNull
    private Long organizationId;

    @ApiModelProperty(value = "是否启用", required = true)
    @NotNull
    private Integer enableFlag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

}

