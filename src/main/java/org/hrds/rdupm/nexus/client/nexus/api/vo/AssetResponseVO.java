package org.hrds.rdupm.nexus.client.nexus.api.vo;

/**
 * Created by wangxiang on 2021/10/7
 */
public class AssetResponseVO {

    private String action;
    private String method;
    /**
     * 请求数据类型
     */
    private AssetResponseResult result;
    private String type;
    private Integer tid;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public AssetResponseResult getResult() {
        return result;
    }

    public void setResult(AssetResponseResult result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }
}
