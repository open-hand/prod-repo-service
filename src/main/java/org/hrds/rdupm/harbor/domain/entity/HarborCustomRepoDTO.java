package org.hrds.rdupm.harbor.domain.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/08 9:51
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("制品库-自定义仓库DTO")
public class HarborCustomRepoDTO {
    @ApiModelProperty("customRepo, 主键")
    private Long repoId;
    @ApiModelProperty(value = "名称")
    private String repoName;
    @ApiModelProperty(value = "是否公开访问，默认false")
    private String repoPublicFlag;
    @ApiModelProperty(value = "地址")
    private String repoUrl;
    @ApiModelProperty(value = "仓库登录用户名")
    private String repoLoginName;
    @ApiModelProperty(value = "仓库登录密码")
    private String repoPassword;
    @ApiModelProperty(value = "仓库用户邮箱")
    private String repoEmail;
    @ApiModelProperty(value = "描述")
    private String repoDescription;


    @ApiModelProperty(value = "组织ID")
    private Long organizationId;
    @ApiModelProperty(value = "项目ID")
    private Long projectId;
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    @ApiModelProperty(value = "创建人图标")
    private String creatorImageUrl;
    @ApiModelProperty(value = "创建人登录名")
    private String creatorLoginName;
    @ApiModelProperty(value = "创建人名称")
    private String creatorRealName;

    private Long createdBy;
    private Date creationDate;
    private String _token;

    public HarborCustomRepoDTO() {
    }

    public HarborCustomRepoDTO(HarborCustomRepo harborCustomRepo) {
        this.repoId = harborCustomRepo.getId();
        this.repoName = harborCustomRepo.getRepoName();
        this.repoPublicFlag = harborCustomRepo.getPublicFlag();
        this.repoUrl = harborCustomRepo.getRepoUrl();
        this.repoLoginName = harborCustomRepo.getLoginName();
        this.repoPassword = harborCustomRepo.getPassword();
        this.repoEmail = harborCustomRepo.getEmail();
        this.repoDescription = harborCustomRepo.getDescription();

        this.projectId = harborCustomRepo.getProjectId();
        this.organizationId = harborCustomRepo.getOrganizationId();
        this.projectCode = harborCustomRepo.getProjectCode();
        this.creatorImageUrl = harborCustomRepo.getCreatorImageUrl();
        this.creatorLoginName = harborCustomRepo.getCreatorLoginName();
        this.creatorRealName = harborCustomRepo.getCreatorRealName();

        this.createdBy = harborCustomRepo.getCreatedBy();
        this.creationDate = harborCustomRepo.getCreationDate();
        this._token = harborCustomRepo.get_token();
    }
}
