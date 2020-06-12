package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 制品库_nexus服务信息配置表
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@ApiModel("制品库_nexus服务信息配置表")
@VersionAudit
@ModifyAudit
@Table(name = "rdupm_nexus_server_config")
@Getter
@Setter
public class NexusServerConfig extends AuditDomain {

    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_URL = "serverUrl";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_ANONYMOUS = "anonymous";
	public static final String FIELD_DEFAULT_FLAG = "defaultFlag";
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
    private Long configId;
    @ApiModelProperty(value = "服务名称",required = true)
    @NotBlank
    private String serverName;
    @ApiModelProperty(value = "访问地址",required = true)
    @NotBlank
    private String serverUrl;
    @ApiModelProperty(value = "管理用户",required = true)
    @NotBlank
    private String userName;
    @ApiModelProperty(value = "管理用户密码",required = true)
    @NotBlank
    private String password;
    @ApiModelProperty(value = "匿名访问，用户")
    private String anonymous;
	@ApiModelProperty(value = "匿名访问，用户对应角色")
	private String anonymousRole;
	@ApiModelProperty(value = "是否是Choerodon默认服务")
	private Integer defaultFlag;
    @ApiModelProperty(value = "租户Id")
    private Long tenantId;
	@ApiModelProperty(value = "是否启用匿名访问控制")
	@NotNull
	private Integer enableAnonymousFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

	@Override
	public AuditDomain set_innerMap(Map<String, Object> _innerMap) {
		return super.set_innerMap(_innerMap);
	}
}
