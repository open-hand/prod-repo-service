package org.hrds.rdupm.nexus.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2021/6/23
 */
public class MavenComponentVO {

    @Encrypt
    private Long repositoryId;

    private String componentId;

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
}
