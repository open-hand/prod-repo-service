package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangxiang on 2021/4/29
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborC7nImageTagVo {
    @ApiModelProperty("TAG名称")
    @SerializedName("name")
    private String tagName;

    @ApiModelProperty("最新push时间")
    @SerializedName("push_time")
    private String pushTime;

    @ApiModelProperty("pull命令")
    private String pullCmd;

    public HarborC7nImageTagVo() {
    }

    public HarborC7nImageTagVo(String tagName, String pushTime, String pullCmd) {
        this.pullCmd = pullCmd;
        this.pushTime = pushTime;
        this.tagName = tagName;
    }
}
