package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

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
    @JsonIgnore
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

    @ApiModelProperty("额外信息")
    @SerializedName("extra_attrs")
    @JsonIgnore
    private ExtraAttr extraAttrs;

    @ApiModelProperty("tag列表")
    private List<Tag> tags;

    @SerializedName("scan_overview")
    @JsonIgnore
    private JSONObject scanOverviewJson;

    @ApiModelProperty("扫描结果")
    private ScanOverview scanOverview;

    @ApiModelProperty
    private Integer totalCount;

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
        @ApiModelProperty("最新push时间")
        @SerializedName("push_time")
        private String pushTime;
        @ApiModelProperty("最近pull时间")
        @SerializedName("pull_time")
        private String pullTime;
        @ApiModelProperty("登录名")
        private String loginName;
        @ApiModelProperty("用户姓名")
        private String realName;
        @ApiModelProperty("用户头像地址")
        private String userImageUrl;
        @ApiModelProperty("创建人")
        private String author;
    }

    @Getter
    @Setter
    public  class ScanOverview {
        @ApiModelProperty("扫描状态")
        @SerializedName("scan_status")
        private String scanStatus;
        @ApiModelProperty("严重程度")
        private String severity;
        @ApiModelProperty("可修复的")
        private Long fixable;
        @ApiModelProperty("总共的")
        private Long total;
        @ApiModelProperty("总结")
        private Summary summary;
        @ApiModelProperty("地址连接")
        private String logUrl;
    }

    @Getter
    @Setter
    public class Summary{
        @ApiModelProperty("危急")
        @SerializedName("Critical")
        private Long critical;
        @ApiModelProperty("严重")
        @SerializedName("High")
        private Long high;
        @ApiModelProperty("中等")
        @SerializedName("Medium")
        private Long medium;
        @ApiModelProperty("较低")
        @SerializedName("Low")
        private Long low;
        @ApiModelProperty("可忽略")
        @SerializedName("Negligible")
        private Long negligible;
        @ApiModelProperty("未知")
        @SerializedName("Unknown")
        private Long unknown;
    }

}
