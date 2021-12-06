package org.hrds.rdupm.nexus.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2021/11/7
 */
public class AssetQuery {
    private String path;

    @ApiModelProperty(value = "仓库名称", required = true)
    private String repositoryName;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
