package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/25 18:39
 */
public class Scanner {

    private Boolean disabled;

    private String vendor;

    private String description;

    private String url;

    private String adapter;

    @SerializedName("access_credential")
    private String accessCredential;

    private String uuid;

    private String auth;

    @SerializedName("is_default")
    private Boolean isDefault;

    private String version;

    private String health;

    @SerializedName("use_internal_addr")
    private Boolean useInternalAddr;

    @SerializedName("skip_certVerify")
    private Boolean skipCertVerify;

    private String name;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    public String getAccessCredential() {
        return accessCredential;
    }

    public void setAccessCredential(String accessCredential) {
        this.accessCredential = accessCredential;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public Boolean getUseInternalAddr() {
        return useInternalAddr;
    }

    public void setUseInternalAddr(Boolean useInternalAddr) {
        this.useInternalAddr = useInternalAddr;
    }

    public Boolean getSkipCertVerify() {
        return skipCertVerify;
    }

    public void setSkipCertVerify(Boolean skipCertVerify) {
        this.skipCertVerify = skipCertVerify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
