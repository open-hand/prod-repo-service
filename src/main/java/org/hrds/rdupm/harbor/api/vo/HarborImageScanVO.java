package org.hrds.rdupm.harbor.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/3/29
 * @Modified By:
 */
public class HarborImageScanVO {
    @ApiModelProperty("摘要")
    private String digest;
    @ApiModelProperty("TAG名称")
    private String tagName;
    @ApiModelProperty("仓库名")
    private String repoName;

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
}
