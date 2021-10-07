package org.hrds.rdupm.nexus.client.nexus.api.vo;


/**
 * Created by wangxiang on 2021/9/30
 */
public class ExtdirectRequestData {


    /**
     *  请求所有仓库的时候的请求参数
     */
    private Integer page;
    /**
     * 请求所有仓库的时候的请求参数
     */
    private Integer start;
    //{"action":"coreui_Browse","method":"read","data":[{"repositoryName":"a-source","node":"/"}],"type":"rpc","tid":45}

    /**
     * 请求仓库下所有文件时所传参数 仓库名称
     */
    private String repositoryName;

    /**
     * 请求仓库下所有文件时所传参数 仓库名称 文件夹的路径  /
     */
    private String node;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }




    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
