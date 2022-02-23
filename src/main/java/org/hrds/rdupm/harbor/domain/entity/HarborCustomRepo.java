package org.hrds.rdupm.harbor.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 制品库-harbor自定义镜像仓库表
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@Getter
@Setter
@ApiModel("制品库-harbor自定义镜像仓库表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_custom_repo")
public class HarborCustomRepo extends AuditDomain {

    public static final String FIELD_ID = "id";

    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";

    public static final String FIELD_REPO_NAME = "repoName";
    public static final String FIELD_REPO_URL = "repoUrl";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_PSW = "password";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PUBLIC_FLAG = "publicFlag";
    public static final String FIELD_PROJECT_SHARE = "projectShare";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_LAST_UPDATE_LOGIN = "lastUpdateLogin";
    public static final String FIELD_API_VERSION = "apiVersion";

    public static final String ENCRYPT_KEY = "rdupm_harbor_custom_repo";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------


    public HarborCustomRepo() {
    }

    public HarborCustomRepo(HarborCustomRepoDTO harborCustomRepoDTO) {
        this.id = harborCustomRepoDTO.getRepoId();
        this.projectId = harborCustomRepoDTO.getProjectId();
        this.organizationId = harborCustomRepoDTO.getOrganizationId();
        this.repoName = harborCustomRepoDTO.getRepoName();
        this.repoUrl = harborCustomRepoDTO.getRepoUrl();
        this.loginName = harborCustomRepoDTO.getRepoLoginName();
        this.password = harborCustomRepoDTO.getRepoPassword();
        this.email = harborCustomRepoDTO.getRepoEmail();
        this.description = harborCustomRepoDTO.getRepoDescription();
        this.publicFlag = harborCustomRepoDTO.getRepoPublicFlag();
        this.projectShare = harborCustomRepoDTO.getProjectShare();
        this.set_token(harborCustomRepoDTO.get_token());
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "猪齿鱼项目ID")
    private Long projectId;

    @ApiModelProperty(value = "猪齿鱼组织ID")
    private Long organizationId;

    @ApiModelProperty(value = "自定义镜像仓库名称（harbor项目名）",required = true)
    @NotBlank
    private String repoName;
    @ApiModelProperty(value = "自定义镜像仓库地址",required = true)
    @NotBlank
    private String repoUrl;
    @ApiModelProperty(value = "登录名",required = true)
    @NotBlank
    private String loginName;
    @ApiModelProperty(value = "密码",required = true)
    @NotBlank
    private String password;
    @ApiModelProperty(value = "邮箱")
    private String email;
   @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "是否公开访问，默认false")
    private String publicFlag;
    @ApiModelProperty(value = "是否项目下共享，默认false")
    private String projectShare;
    @ApiModelProperty(value = "是否启用，默认Y")
    private String enabledFlag;
    @ApiModelProperty(value = "harbor api版本")
    private String apiVersion;
	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
	@ApiModelProperty(value = "关联的应用服务ID")
	private Set<Long> appServiceIds;


	@Transient
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    @Transient
    @ApiModelProperty(value = "创建人图标")
    private String creatorImageUrl;
    @Transient
    @ApiModelProperty(value = "创建人登录名")
    private String creatorLoginName;
    @Transient
    @ApiModelProperty(value = "创建人名称")
    private String creatorRealName;
    @Transient
    @ApiModelProperty(value = "Harbor项目ID")
    private Integer harborProjectId;
    @Transient
    @ApiModelProperty(value = "仓库包的拉取总次数")
    private Long downloadTimes;

    @Transient
    @ApiModelProperty(value = "仓库拉取包的人数")
    private Long personTimes;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

}
