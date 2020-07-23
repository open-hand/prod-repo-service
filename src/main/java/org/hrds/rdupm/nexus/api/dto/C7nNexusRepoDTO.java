package org.hrds.rdupm.nexus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * choerodon nexus仓库DTO
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@ApiModel("nexus仓库DTO")
@Setter
@Getter
@ToString
public class C7nNexusRepoDTO {
    @ApiModelProperty(value = "服务配置Id")
    @Encrypt(NexusServerConfig.ENCRYPT_KEY)
    private Long configId;
    @ApiModelProperty(value = "主键Id")
    @Encrypt(NexusRepository.ENCRYPT_KEY)
    private Long repositoryId;
    @ApiModelProperty(value = "仓库名称")
    private String neRepositoryName;
}
