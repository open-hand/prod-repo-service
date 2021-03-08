package org.hrds.rdupm.harbor.domain.entity.v2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoImageTagVo;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/3/8
 * @Modified By:
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborArtifactDTO {
    private String digest;
    @SerializedName("repository_id")
    private Long repositoryId;
    private List<HarborC7nRepoImageTagVo.HarborC7nImageTagVo> tags;

}
