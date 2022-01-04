package org.hrds.rdupm.nexus.client.nexus.api.vo;

/**
 * Created by wangxiang on 2021/10/7
 */
public class AssetResponseData {
    private String id;
    private String name;
    private Integer size;
    private String repositoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
