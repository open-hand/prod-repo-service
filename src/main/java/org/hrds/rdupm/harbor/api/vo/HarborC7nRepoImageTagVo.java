package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 返回给猪齿鱼
 *
 * @author chenxiuhong 2020/04/24 11:37 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborC7nRepoImageTagVo {

    @ApiModelProperty("仓库类型")
    private String repoType;

    @ApiModelProperty("url")
    private String harborUrl;

    @ApiModelProperty("拉取账号")
    private String pullAccount;

    @ApiModelProperty("拉取密码")
    private String pullPassword;

    @ApiModelProperty("镜像版本列表")
    private List<HarborC7nImageTagVo> imageTagList;

    public HarborC7nRepoImageTagVo() {
    }

    public HarborC7nRepoImageTagVo(String repoType, String harborUrl, String pullAccount, String pullPassword, List<HarborC7nImageTagVo> imageTagList) {
        this.repoType = repoType;
        this.harborUrl = harborUrl;
        this.pullAccount = pullAccount;
        this.pullPassword = pullPassword;
        this.imageTagList = imageTagList;
    }
}
