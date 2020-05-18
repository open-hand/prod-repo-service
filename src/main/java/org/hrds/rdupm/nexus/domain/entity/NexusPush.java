package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 制品库_nexus发布权限校验信息表
 *
 * @author weisen.yang@hand-china.com 2020-05-18 16:26:47
 */
@ApiModel("制品库_nexus发布权限校验信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_push")
public class NexusPush extends AuditDomain {

    public static final String FIELD_NEXUS_PUSH_ID = "nexusPushId";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_RULE = "rule";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long nexusPushId;
    @ApiModelProperty(value = "类型：MAVEN",required = true)
    @NotBlank
    private String type;
    @ApiModelProperty(value = "规则，多个规则用逗号隔开",required = true)
    @NotBlank
    private String rule;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getNexusPushId() {
		return nexusPushId;
	}

	public void setNexusPushId(Long nexusPushId) {
		this.nexusPushId = nexusPushId;
	}
    /**
     * @return 类型：MAVEN
     */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    /**
     * @return 规则，多个规则用逗号隔开
     */
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

}
