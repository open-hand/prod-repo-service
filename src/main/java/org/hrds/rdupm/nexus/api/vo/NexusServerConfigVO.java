package org.hrds.rdupm.nexus.api.vo;

public class NexusServerConfigVO {

    private Long configId;
    /**
     * 服务名称
     */
    private String serverName;
    /**
     * 访问地址
     */
    private String serverUrl;

    private Integer defaultFlag;

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
