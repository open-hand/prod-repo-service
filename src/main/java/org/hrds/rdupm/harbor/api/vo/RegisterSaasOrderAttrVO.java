package org.hrds.rdupm.harbor.api.vo;

public class RegisterSaasOrderAttrVO {

    private Long id;
    private Long saasOrderId;
    private String version;
    private Long userCount;

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaasOrderId() {
        return saasOrderId;
    }

    public void setSaasOrderId(Long saasOrderId) {
        this.saasOrderId = saasOrderId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }
}
