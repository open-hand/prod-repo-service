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
 * 制品库_制品信息表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@ApiModel("制品库_制品信息表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_prod_repository")
public class ProdRepository extends AuditDomain {

    public static final String FIELD_PROD_REPOSITORY_ID = "prodRepositoryId";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
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
    private Long prodRepositoryId;
    @ApiModelProperty(value = "类型： MAVEN、DOCKER、NPM ",required = true)
    @NotBlank
    private String type;
    @ApiModelProperty(value = "名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "租户Id")
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getProdRepositoryId() {
		return prodRepositoryId;
	}

	public void setProdRepositoryId(Long prodRepositoryId) {
		this.prodRepositoryId = prodRepositoryId;
	}
    /**
     * @return 类型： MAVEN、DOCKER、NPM
     */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    /**
     * @return 名称
     */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    /**
     * @return 租户Id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
