package org.hrds.rdupm.harbor.api.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/05/28 14:24
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborRobotVO {
    @ApiModelProperty("harbor机器人id")
    private Long id;

    @ApiModelProperty("harbor项目ID")
    @SerializedName("project_id")
    private Long projectId;

    @ApiModelProperty("机器人账户名称")
    private String name;

    @ApiModelProperty("机器人账户描述")
    private String description;

    @ApiModelProperty("机器人账户token")
    private String token;

    @ApiModelProperty("机器人账户到期时间，秒数")
    @SerializedName("expires_at")
    private Long expiresAt;

    @ApiModelProperty("是否禁用，false启用/true禁用")
    private Boolean disabled;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName("creation_time")
    private Date creationTime;

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName("update_time")
    private Date updateTime;

    @ApiModelProperty("机器人账户功能（创建时用到）")
    private List<HarborRobotAccessVO> access;

    public HarborRobotVO(String name, String description, List<HarborRobotAccessVO> access) {
        this.name = name;
        this.description = description;
        this.access = access;
    }

    public HarborRobotVO(String name, String token) {
        this.name = name;
        this.token = token;
    }
}
