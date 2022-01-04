package org.hrds.rdupm.nexus.client.nexus.api.vo;

/**
 * Created by wangxiang on 2021/9/30
 */
public class ExtdirectResponseData {
//      "type": "hosted",
//              "format": "maven2",
//              "versionPolicy": "MIXED",
//              "url": "http://nexus.c7n.devops.hand-china.com/repository/choerodon-market-repo/",
//              "status": {
//        "repositoryName": "choerodon-market-repo",
//                "online": true,
//                "description": null,
//                "reason": null
//    },
//            "sortOrder": 0,
//            "id": "choerodon-market-repo",
//            "name": "choerodon-market-repo"

    /**
     * 仓库的类型 hosted, 文件夹类型folder
     */
    private String type;
    /**
     * 仓库的版本策略 MIXED
     */
    private String versionPolicy;
    /**
     * 仓库名
     */
    private String name;

    /**
     * 文件夹的名称
     */
    private String id;

    /**
     * 是否已经是叶子节点
     */
    private Boolean leaf;

    /**
     * type是asset 时存放assetId
     */
    private String assetId;



    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersionPolicy() {
        return versionPolicy;
    }

    public void setVersionPolicy(String versionPolicy) {
        this.versionPolicy = versionPolicy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
