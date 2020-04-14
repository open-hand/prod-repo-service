package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/25 15:11
 */
public class ConfigurationsUpdateDTO {
    @SerializedName("oidc_verify_cert")
    private Boolean oidcVerifyCert;

    @SerializedName("email_identity")
    private String emailIdentity;

    @SerializedName("ldap_group_search_filter")
    private String ldapGroupSearchFilter;

    @SerializedName("auth_mode")
    private String authMode;

    @SerializedName("self_registration")
    private Boolean selfRegistration;

    @SerializedName("oidc_scope")
    private String oidcScope;

    @SerializedName("ldap_search_dn")
    private String ldapSearchDn;

    @SerializedName("storage_per_project")
    private String storagePerProject;

    @SerializedName("scan_all_policy")
    private Object scanAllPolicy;

    @SerializedName("verify_remote_cert")
    private Boolean verifyRemoteCert;

    @SerializedName("ldap_timeout")
    private Integer ldapTimeout;

    @SerializedName("ldap_base_dn")
    private String ldapBaseDn;

    @SerializedName("ldap_filter")
    private String ldapFilter;

    @SerializedName("read_only")
    private Boolean readOnly;

    @SerializedName("quota_per_project_enable")
    private Boolean quotaPerProjectEnable;

    @SerializedName("ldap_url")
    private String ldapUrl;

    @SerializedName("oidc_name")
    private String oidcName;

    @SerializedName("project_creation_restriction")
    private String projectCreationRestriction;

    @SerializedName("ldap_uid")
    private String ldapUid;

    @SerializedName("oidc_client_id")
    private String oidcClientId;

    @SerializedName("ldap_group_base_dn")
    private String ldapGroupBaseDn;

    @SerializedName("ldap_group_attribute_name")
    private String ldapGroupAttributeName;

    @SerializedName("email_insecure")
    private Boolean emailInsecure;

    @SerializedName("ldap_group_admin_dn")
    private String ldapGroupAdminDn;

    @SerializedName("email_username")
    private String emailUsername;

    @SerializedName("oidc_endpoint")
    private String oidcEndpoint;

    @SerializedName("oidc_client_secret")
    private String oidcClientSecret;

    @SerializedName("ldap_scope")
    private Integer ldap_scope;

    @SerializedName("count_per_project")
    private String countPerProject;

    @SerializedName("token_expiration")
    private Integer tokenExpiration;

    @SerializedName("ldap_group_search_scope")
    private Integer ldapGroupSearchScope;

    @SerializedName("email_ssl")
    private Boolean emailSsl;

    @SerializedName("email_port")
    private Integer emailPort;

    @SerializedName("email_host")
    private String emailHost;

    @SerializedName("email_from")
    private String emailFrom;

    public Boolean getOidcVerifyCert() {
        return oidcVerifyCert;
    }

    public void setOidcVerifyCert(Boolean oidcVerifyCert) {
        this.oidcVerifyCert = oidcVerifyCert;
    }

    public String getEmailIdentity() {
        return emailIdentity;
    }

    public void setEmailIdentity(String emailIdentity) {
        this.emailIdentity = emailIdentity;
    }

    public String getLdapGroupSearchFilter() {
        return ldapGroupSearchFilter;
    }

    public void setLdapGroupSearchFilter(String ldapGroupSearchFilter) {
        this.ldapGroupSearchFilter = ldapGroupSearchFilter;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public Boolean getSelfRegistration() {
        return selfRegistration;
    }

    public void setSelfRegistration(Boolean selfRegistration) {
        this.selfRegistration = selfRegistration;
    }

    public String getOidcScope() {
        return oidcScope;
    }

    public void setOidcScope(String oidcScope) {
        this.oidcScope = oidcScope;
    }

    public String getLdapSearchDn() {
        return ldapSearchDn;
    }

    public void setLdapSearchDn(String ldapSearchDn) {
        this.ldapSearchDn = ldapSearchDn;
    }

    public String getStoragePerProject() {
        return storagePerProject;
    }

    public void setStoragePerProject(String storagePerProject) {
        this.storagePerProject = storagePerProject;
    }

    public Object getScanAllPolicy() {
        return scanAllPolicy;
    }

    public void setScanAllPolicy(Object scanAllPolicy) {
        this.scanAllPolicy = scanAllPolicy;
    }

    public Boolean getVerifyRemoteCert() {
        return verifyRemoteCert;
    }

    public void setVerifyRemoteCert(Boolean verifyRemoteCert) {
        this.verifyRemoteCert = verifyRemoteCert;
    }

    public Integer getLdapTimeout() {
        return ldapTimeout;
    }

    public void setLdapTimeout(Integer ldapTimeout) {
        this.ldapTimeout = ldapTimeout;
    }

    public String getLdapBaseDn() {
        return ldapBaseDn;
    }

    public void setLdapBaseDn(String ldapBaseDn) {
        this.ldapBaseDn = ldapBaseDn;
    }

    public String getLdapFilter() {
        return ldapFilter;
    }

    public void setLdapFilter(String ldapFilter) {
        this.ldapFilter = ldapFilter;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getQuotaPerProjectEnable() {
        return quotaPerProjectEnable;
    }

    public void setQuotaPerProjectEnable(Boolean quotaPerProjectEnable) {
        this.quotaPerProjectEnable = quotaPerProjectEnable;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getOidcName() {
        return oidcName;
    }

    public void setOidcName(String oidcName) {
        this.oidcName = oidcName;
    }

    public String getProjectCreationRestriction() {
        return projectCreationRestriction;
    }

    public void setProjectCreationRestriction(String projectCreationRestriction) {
        this.projectCreationRestriction = projectCreationRestriction;
    }

    public String getLdapUid() {
        return ldapUid;
    }

    public void setLdapUid(String ldapUid) {
        this.ldapUid = ldapUid;
    }

    public String getOidcClientId() {
        return oidcClientId;
    }

    public void setOidcClientId(String oidcClientId) {
        this.oidcClientId = oidcClientId;
    }

    public String getLdapGroupBaseDn() {
        return ldapGroupBaseDn;
    }

    public void setLdapGroupBaseDn(String ldapGroupBaseDn) {
        this.ldapGroupBaseDn = ldapGroupBaseDn;
    }

    public String getLdapGroupAttributeName() {
        return ldapGroupAttributeName;
    }

    public void setLdapGroupAttributeName(String ldapGroupAttributeName) {
        this.ldapGroupAttributeName = ldapGroupAttributeName;
    }

    public Boolean getEmailInsecure() {
        return emailInsecure;
    }

    public void setEmailInsecure(Boolean emailInsecure) {
        this.emailInsecure = emailInsecure;
    }

    public String getLdapGroupAdminDn() {
        return ldapGroupAdminDn;
    }

    public void setLdapGroupAdminDn(String ldapGroupAdminDn) {
        this.ldapGroupAdminDn = ldapGroupAdminDn;
    }

    public String getEmailUsername() {
        return emailUsername;
    }

    public void setEmailUsername(String emailUsername) {
        this.emailUsername = emailUsername;
    }

    public String getOidcEndpoint() {
        return oidcEndpoint;
    }

    public void setOidcEndpoint(String oidcEndpoint) {
        this.oidcEndpoint = oidcEndpoint;
    }

    public String getOidcClientSecret() {
        return oidcClientSecret;
    }

    public void setOidcClientSecret(String oidcClientSecret) {
        this.oidcClientSecret = oidcClientSecret;
    }

    public Integer getLdap_scope() {
        return ldap_scope;
    }

    public void setLdap_scope(Integer ldap_scope) {
        this.ldap_scope = ldap_scope;
    }

    public String getCountPerProject() {
        return countPerProject;
    }

    public void setCountPerProject(String countPerProject) {
        this.countPerProject = countPerProject;
    }

    public Integer getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Integer tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public Integer getLdapGroupSearchScope() {
        return ldapGroupSearchScope;
    }

    public void setLdapGroupSearchScope(Integer ldapGroupSearchScope) {
        this.ldapGroupSearchScope = ldapGroupSearchScope;
    }

    public Boolean getEmailSsl() {
        return emailSsl;
    }

    public void setEmailSsl(Boolean emailSsl) {
        this.emailSsl = emailSsl;
    }

    public Integer getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }
}
