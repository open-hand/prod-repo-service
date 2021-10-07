package org.hrds.rdupm.nexus.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * Created by wangxiang on 2021/9/29
 */
@ApiModel("制品库_nexus包信息表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rdupm_nexus_assets")
@Getter
@Setter
@ExcelSheet(title = "包信息表")
public class NexusAssets extends AuditDomain {

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty("包的名字")
    private String name;

    @ApiModelProperty("包的版本")
    private String version;

    @ApiModelProperty("包的类型")
    private String type;

    @Encrypt
    @ApiModelProperty("仓库的Id")
    private Long repositoryId;

    @ApiModelProperty("项目Id")
    private Long projectId;

    @ApiModelProperty("项目Id")
    private Long size;



}
