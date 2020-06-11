package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/05/28 14:24
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborRobotAccessVO {
    @ApiModelProperty("机器人账户功能，push/pull")
    private String action;

    @ApiModelProperty("机器人账户操作的资源(项目)，/project/{projectId}/repository")
    private String resource;

    public HarborRobotAccessVO(String action, String resource) {
        this.action = action;
        this.resource = resource;
    }
}
