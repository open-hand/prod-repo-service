package org.hrds.rdupm.harbor.infra.feign.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/02 17:11
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AppServiceDTO {
    @ApiModelProperty("应用服务id")
    private Long id;

    @ApiModelProperty("应用服务名称")
    private String name;

    @ApiModelProperty("应用服务code")
    private String code;

    @ApiModelProperty("应用服务所属项目id")
    private Long projectId;

//    @ApiModelProperty("应用服务对应gitlab项目的id")
//    private Long gitlabProjectId;
//
//    @ApiModelProperty("应用服务对应的gitlab仓库地址")
//    private String repoUrl;

//    @ApiModelProperty("应用服务对应的gitlab的仓库的ssh协议克隆地址")
//    private String sshRepositoryUrl;
//
//    @ApiModelProperty("应用服务是否同步完成，false表示正在处理中")
//    private Boolean synchro;

    @ApiModelProperty("应用服务是否启用")
    private Boolean isActive;

//    private String publishLevel;
//    private String contributor;

    @ApiModelProperty("应用服务描述")
    private String description;

//    @ApiModelProperty("sonarqube地址")
//    private String sonarUrl;
//
//    @ApiModelProperty("应用服务是否失败，如果已同步且这个值为true说明应用服务创建失败")
//    private Boolean fail;

    @ApiModelProperty("应用服务的类型")
    private String type;

    @ApiModelProperty("应用服务数据库纪录的版本号")
    private Long objectVersionNumber;

    @ApiModelProperty("应用服务图标url")
    private String imgUrl;

    @ApiModelProperty("应用创建时间")
    private Date creationDate;

    @ApiModelProperty("应用服务最近更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty("创建者用户名")
    private String createUserName;

    @ApiModelProperty("创建者登录名")
    private String createLoginName;

    @ApiModelProperty("最近更新者用户名")
    private String updateUserName;

    @ApiModelProperty("最近更新者登录名")
    private String updateLoginName;

//    @ApiModelProperty("此应用服务是够跳过权限检查，true表示允许项目下所有的项目成员及项目所有者访问")
//    private Boolean skipCheckPermission;
//
//    @ApiModelProperty("是否是空仓库(是否没有分支)")
//    private Boolean emptyRepository;
//
//    @ApiModelProperty("应用服务类型")
//    private String serviceType;


}
