package org.hrds.rdupm.nexus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 仓库校信息-CI流水线
 * @author weisen.yang@hand-china.com 2020/4/1
 */
@Getter
@Setter
public class NexusRepoDTO {

    @JsonIgnore
    private Long configId;
    @ApiModelProperty(value = "主键Id")
    @Encrypt(NexusRepository.ENCRYPT_KEY)
    private Long repositoryId;
    @ApiModelProperty(value = "仓库名称")
    private String name;
    @ApiModelProperty(value = "仓库类型")
    private String type;
    @ApiModelProperty(value = "仓库url")
    private String url;
    @ApiModelProperty(value = "仓库策略")
    private String versionPolicy;

    @ApiModelProperty(value = "仓库默认发布用户Id")
    private String neUserId;
    @ApiModelProperty(value = "仓库默认发布用户密码")
    private String neUserPassword;
    @ApiModelProperty(value = "仓库默认拉取用户Id")
    private String nePullUserId;
    @ApiModelProperty(value = "仓库默认拉取用户密码")
    private String nePullUserPassword;
}
