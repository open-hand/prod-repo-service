package org.hrds.rdupm.harbor.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 制品库-harbor仓库（默认/自定义）
 *
 * @author mofei.li@hand-china.com 2020/06/08 17:54
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("制品库-仓库DTO")
public class HarborRepoDTO {
    @ApiModelProperty(value = "默认仓库")
    private HarborRepository defaultRepository;

    @ApiModelProperty(value = "自定义仓库")
    private HarborCustomRepo customRepository;
}
