package org.hrds.rdupm.nexus.client.nexus.api.vo;

import java.util.List;

/**
 * Created by wangxiang on 2021/9/29
 */
public class ExtdirectRequestVO<T> {
    //  {"action":"coreui_Browse","method":"read","data":[{"repositoryName":"alpha-snapshot","node":"/"}],"type":"rpc","tid":120}
    private String action;
    private String method;
    /**
     * 请求数据类型
     */
    private List<T> data;
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
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
