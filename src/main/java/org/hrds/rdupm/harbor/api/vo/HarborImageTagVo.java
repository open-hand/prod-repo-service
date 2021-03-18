package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 11:37 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborImageTagVo {

    @ApiModelProperty("摘要")
    private String digest;

    @ApiModelProperty("TAG名称")
    @SerializedName("name")
    private String tagName;

    @ApiModelProperty("TAG大小：102400")
    private Integer size;

    @ApiModelProperty("架构")
    private String architecture;

    @ApiModelProperty("操作系统")
    private String os;

    @ApiModelProperty("操作系统版本")
    @SerializedName("os.version")
    private String osVersion;

    @ApiModelProperty("docker版本")
    @SerializedName("docker_version")
    private String dockerVersion;

    @ApiModelProperty("创建人")
    private String author;

    @ApiModelProperty("创建时间")
    @SerializedName("created")
    private String createTime;

    @ApiModelProperty("最新push时间")
    @SerializedName("push_time")
    private String pushTime;

    @ApiModelProperty("最近pull时间")
    @SerializedName("pull_time")
    private String pullTime;

    @ApiModelProperty("TAG大小显示：19MB")
    private String sizeDesc;

    @ApiModelProperty("登录名")
    private String loginName;

    @ApiModelProperty("用户姓名")
    private String realName;

    @ApiModelProperty("用户头像地址")
    private String userImageUrl;

    @ApiModelProperty("额外信息")
    @SerializedName("extra_attrs")
    private ExtraAttr extraAttrs;

    @ApiModelProperty("tag列表")
    private List<Tag> tags;

    @Getter
    @Setter
    public class ExtraAttr {
        @ApiModelProperty("架构")
        private String architecture;
        @ApiModelProperty("创建人")
        private String author;
        @ApiModelProperty("操作系统")
        private String os;
    }

    @Getter
    @Setter
    public class Tag {
        @ApiModelProperty("tag名称")
        private String name;
    }

}
