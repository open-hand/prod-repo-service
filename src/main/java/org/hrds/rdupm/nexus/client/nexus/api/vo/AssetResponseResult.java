package org.hrds.rdupm.nexus.client.nexus.api.vo;

import java.util.List;

/**
 * Created by wangxiang on 2021/10/7
 */
public class AssetResponseResult {
    /**
     * 请求是否成功
     */
    private Boolean success;
    /**
     * 请求返回仓库的数据
     */
    private AssetResponseData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public AssetResponseData getData() {
        return data;
    }

    public void setData(AssetResponseData data) {
        this.data = data;
    }
}
