package org.hrds.rdupm.nexus.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
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

    public static final String ADMIN_USER = "admin";

    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_URL = "serverUrl";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_ANONYMOUS = "anonymous";
    public static final String FIELD_ANONYMOUS_ROLE = "anonymousRole";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLE_ANONYMOUS_FLAG = "enableAnonymousFlag";



    public void validParam (NexusClient nexusClient) {
        if (!ADMIN_USER.equals(this.userName)) {
            throw new CommonException(NexusMessageConstants.NEXUS_INPUT_ADMIN_USER);
        }
        this.validaUserPassword(nexusClient);
        if (this.enableAnonymousFlag.equals(BaseConstants.Flag.YES)) {
            // 启用匿名访问控制
            if (StringUtils.isBlank(this.anonymous)) {
                throw new CommonException("anonymous not null");
            }
            if (StringUtils.isBlank(this.anonymousRole)) {
                throw new CommonException("anonymousRole not null");
            }
            NexusServerUser anonymousUser = nexusClient.getNexusUserApi().getUsers(this.anonymous);
            if (anonymousUser == null) {
                throw new CommonException(NexusMessageConstants.NEXUS_ANONYMOUS_USER_NOT_EXIST);
            }
            NexusServerRole anonymousRoleExist = nexusClient.getNexusRoleApi().getRoleById(this.anonymousRole);
            if (anonymousRoleExist == null) {
                throw new CommonException(NexusMessageConstants.NEXUS_ANONYMOUS_ROLE_USER_NOT_EXIST);
            }
        } else if (this.enableAnonymousFlag.equals(BaseConstants.Flag.NO)) {
            this.anonymous = null;
            this.anonymousRole = null;
        } else {
            throw new CommonException("enableAnonymousFlag param error");
        }
    }

    public void  validaUserPassword(NexusClient nexusClient) {
        this.serverUrl = this.serverUrl.replaceAll("/*$", "");
        NexusServer nexusServer = new NexusServer(this.serverUrl, this.userName, this.password);
        nexusClient.setNexusServerInfo(nexusServer);
        NexusServerUser nexusExistUser = null;
        try {
            nexusExistUser = nexusClient.getNexusUserApi().getUsers(this.userName);
        } catch (NexusResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CommonException(NexusMessageConstants.NEXUS_USER_AND_PASSWORD_ERROR);
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_PERMISSIONS);
            }
            throw e;
        }
    }
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

    @Transient
    private Long projectServiceId;
    @Transient
    private Long projectId;
    @Transient
    private Long organizationId;
    @ApiModelProperty(value = "是否启用,当前项目下")
    @Transient
    private Integer enableFlag;

    @Transient
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Override
    public AuditDomain set_innerMap(Map<String, Object> _innerMap) {
		return super.set_innerMap(_innerMap);
	}
}
