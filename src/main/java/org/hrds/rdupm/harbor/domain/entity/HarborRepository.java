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
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 制品库-harbor镜像仓库表
 *
 * @author xiuhong.chen@hand-china.com 2020-04-22 09:53:19
 */
@Getter
@Setter
@ApiModel("制品库-harbor镜像仓库表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_repository")
public class HarborRepository extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PUBLIC_FLAG = "publicFlag";
    public static final String FIELD_HARBOR_ID = "harborId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
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


    @ApiModelProperty("主键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "猪齿鱼项目ID", required = true)
    private Long projectId;
    @ApiModelProperty(value = "项目编码", required = true)
    @NotBlank
    private String code;
    @ApiModelProperty(value = "项目名称", required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "是否公开访问，默认false")
    private String publicFlag;

    @ApiModelProperty(value = "harbor项目ID", required = true)
    @Encrypt
    private Long harborId;
    @ApiModelProperty(value = "组织ID", required = true)
    private Long organizationId;
    @ApiModelProperty(value = "", required = true)
    private Date creationDate;
    @ApiModelProperty(value = "", required = true)
    private Long createdBy;
    @ApiModelProperty(value = "", required = true)
    private Long lastUpdatedBy;
    @ApiModelProperty(value = "", required = true)
    private Date lastUpdateDate;
    @ApiModelProperty(value = "")
    private Long lastUpdateLogin;

    @Transient
    private Integer repoCount;

    @Transient
    private String creatorImageUrl;

    @Transient
    private String creatorLoginName;

    @Transient
    private String creatorRealName;

    @Transient
    @ApiModelProperty(value = "仓库包的拉取总次数")
    private Long downloadTimes;

    @Transient
    @ApiModelProperty(value = "仓库拉取包的人数")
    private Long personTimes;

    public HarborRepository() {

    }

    public HarborRepository(@NotNull Long projectId, @NotBlank String code, @NotBlank String name, String publicFlag, @NotNull Long harborId, @NotNull Long organizationId) {
        this.projectId = projectId;
        this.code = code;
        this.name = name;
        this.publicFlag = publicFlag;
        this.harborId = harborId;
        this.organizationId = organizationId;
    }

    public HarborRepository(@NotBlank String code, @NotBlank String name, String publicFlag, @NotNull Long organizationId) {
        this.code = code;
        this.name = name;
        this.publicFlag = publicFlag;
        this.organizationId = organizationId;
    }
}
