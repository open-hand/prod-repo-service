package org.hrds.rdupm.common.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 制品库-制品用户表
 *
 * @author xiuhong.chen@hand-china.com 2020-05-21 15:47:14
 */
@ApiModel("制品库-制品用户表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_prod_user")
@Getter
@Setter
public class ProdUser extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_PSW = "password";
    public static final String FIELD_PSW_UPDATE_FLAG = "pwdUpdateFlag";
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
    private Long id;

    @ApiModelProperty(value = "猪齿鱼用户ID",required = true)
    @NotNull
	@Encrypt
    private Long userId;

    @ApiModelProperty(value = "登录名",required = true)
    @NotBlank
    private String loginName;

	@ApiModelProperty(value = "密码",required = true)
    @NotBlank
    private String password;

    @ApiModelProperty(value = "密码是否被修改，0否表示明文默认密码 1是",required = true)
    @NotNull
    private Integer pwdUpdateFlag;

	@Transient
	@ApiModelProperty(value = "旧密码")
	private String oldPassword;

	@Transient
	@ApiModelProperty(value = "确认密码")
	private String rePassword;

    /**
     * 这里的构造方法不能省略,Repository里面会newInstance来创建实例 少了构造方法会报错
     */
    public ProdUser() {
    }

    public ProdUser(@NotNull Long userId, @NotBlank String loginName, @NotBlank String password, @NotNull Integer pwdUpdateFlag) {
		this.userId = userId;
		this.loginName = loginName;
		this.password = password;
		this.pwdUpdateFlag = pwdUpdateFlag;
		this.pwdUpdateFlag = 0;
	}

	public ProdUser(@NotNull Long userId, @NotBlank String loginName, @NotNull Integer pwdUpdateFlag) {
		this.userId = userId;
		this.loginName = loginName;
		this.pwdUpdateFlag = pwdUpdateFlag;
		this.pwdUpdateFlag = 0;
	}
}
