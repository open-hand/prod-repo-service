package org.hrds.rdupm.harbor.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.util.Date;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 制品库-harbor仓库服务关联表
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@Getter
@Setter
@ApiModel("制品库-harbor仓库服务关联表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_harbor_repo_service")
public class HarborRepoService extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CUSTOM_REPO_ID = "customRepoId";
    public static final String FIELD_APP_SERVICE_ID = "appServiceId";
    public static final String FIELD_PROJECT_ID = "projectId";
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


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "自定义镜像仓库ID",required = true)
    @NotNull
    private Long customRepoId;
   @ApiModelProperty(value = "应用服务ID")    
    private Long appServiceId;
   @ApiModelProperty(value = "猪齿鱼项目ID")    
    private Long projectId;
   @ApiModelProperty(value = "猪齿鱼组织ID")    
    private Long organizationId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------



    //
    // getter/setter
    // ------------------------------------------------------------------------------

	public HarborRepoService(){}
	public HarborRepoService(@NotNull Long customRepoId, Long appServiceId, Long projectId, Long organizationId) {
		this.customRepoId = customRepoId;
		this.appServiceId = appServiceId;
		this.projectId = projectId;
		this.organizationId = organizationId;
	}
}
