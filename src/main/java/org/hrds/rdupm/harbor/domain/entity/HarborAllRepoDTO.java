package org.hrds.rdupm.harbor.domain.entity;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/17 17:08
 */
@Getter
@Setter
@ApiModel("Harbor所有仓库DTO")
public class HarborAllRepoDTO {

    @ApiModelProperty(value = "猪齿鱼项目ID")
    private Long projectId;

    @ApiModelProperty(value = "默认仓库配置信息")
    private HarborRepoConfigDTO harborDefaultRepoConfig;

    @ApiModelProperty(value = "自定义仓库配置信息")
    private List<HarborRepoConfigDTO> harborCustomRepoConfigList;

    public HarborAllRepoDTO() {
    }

    public HarborAllRepoDTO(Long projectId, HarborRepoConfigDTO harborDefaultRepoConfig, List<HarborRepoConfigDTO> harborCustomRepoConfigList) {
        this.projectId = projectId;
        this.harborDefaultRepoConfig = harborDefaultRepoConfig;
        this.harborCustomRepoConfigList = harborCustomRepoConfigList;
    }
}
