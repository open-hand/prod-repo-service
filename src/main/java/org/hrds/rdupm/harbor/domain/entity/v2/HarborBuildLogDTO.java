package org.hrds.rdupm.harbor.domain.entity.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

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
public class HarborBuildLogDTO {
    private String created;
    @SerializedName("created_by")
    private String createdBy;
}
