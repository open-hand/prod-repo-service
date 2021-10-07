package org.hrds.rdupm.nexus.client.nexus.api.vo;

import java.util.List;

/**
 * Created by wangxiang on 2021/9/30
 */
public class ExtdirectResponseResult {

    /**
     * 请求是否成功
     */
    private Boolean success;
    /**
     * 请求返回仓库的数据
     */
    private List<ExtdirectResponseData> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<ExtdirectResponseData> getData() {
        return data;
    }

    public void setData(List<ExtdirectResponseData> data) {
        this.data = data;
    }
}
