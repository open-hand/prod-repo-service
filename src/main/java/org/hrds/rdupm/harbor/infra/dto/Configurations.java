package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/24 21:07
 */
public class Configurations {

    @SerializedName("auth_mode")
    private ConfigurationAttributeStr authMode;

    @SerializedName("count_per_project")
    private ConfigurationAttributeInt countPerProject;

    @SerializedName("email_from")
    private ConfigurationAttributeStr emailFrom;

    @SerializedName("email_host")
    private ConfigurationAttributeStr emailHost;

    @SerializedName("email_identity")
    private ConfigurationAttributeStr emailIdentity;

    @SerializedName("email_insecure")
    private ConfigurationAttributeBool emailInsecure;

    @SerializedName("email_port")
    private ConfigurationAttributeInt emailPort;

    @SerializedName("email_ssl")
    private ConfigurationAttributeBool emailSsl;

    @SerializedName("email_username")
    private ConfigurationAttributeStr emailUsername;

    @SerializedName("http_authproxy_endpoint")
    private ConfigurationAttributeStr httpAuthproxyEndpoint;

    @SerializedName("http_authproxy_server_certificate")
    private ConfigurationAttributeStr httpAuthproxyServerCertificate;

    @SerializedName("http_authproxy_skip_search")
    private ConfigurationAttributeBool httpAuthproxySkipSearch;

    @SerializedName("http_authproxy_tokenreview_endpoint")
    private ConfigurationAttributeBool httpAuthproxyTokenreviewEndpoint;

    @SerializedName("http_authproxy_verify_cert")
    private ConfigurationAttributeBool httpAuthproxyVerifyCert;

    @SerializedName("ldap_base_dn")
    private ConfigurationAttributeStr ldapBaseDn;

    @SerializedName("ldap_filter")
    private ConfigurationAttributeStr ldapFilter;

    @SerializedName("ldap_group_admin_dn")
    private ConfigurationAttributeStr ldapGroupAdminDn;

    @SerializedName("ldap_group_attribute_name")
    private ConfigurationAttributeStr ldapGroupAttributeName;

    @SerializedName("ldap_group_base_dn")
    private ConfigurationAttributeStr ldapGroupBaseDn;

    @SerializedName("ldap_group_membership_attribute")
    private ConfigurationAttributeStr ldapGroupMembershipAttribute;

    @SerializedName("ldap_group_search_filter")
    private ConfigurationAttributeStr ldapGroupSearchFilter;

    @SerializedName("ldap_group_search_scope")
    private ConfigurationAttributeInt ldapGroupSearchScope;

    @SerializedName("ldap_scope")
    private ConfigurationAttributeInt ldapScope;

    @SerializedName("ldap_search_dn")
    private ConfigurationAttributeStr ldapSearchDn;

    @SerializedName("ldap_timeout")
    private ConfigurationAttributeInt ldapTimeout;

    @SerializedName("ldap_uid")
    private ConfigurationAttributeStr ldapUid;

    @SerializedName("ldap_url")
    private ConfigurationAttributeStr ldapUrl;

    @SerializedName("ldap_verify_cert")
    private ConfigurationAttributeBool ldapVerifyCert;

    @SerializedName("notification_enable")
    private ConfigurationAttributeBool notificationEnable;

    @SerializedName("oidc_client_id")
    private ConfigurationAttributeStr oidcClientId;

    @SerializedName("oidc_endpoint")
    private ConfigurationAttributeStr oidcEndpoint;

    @SerializedName("oidc_groups_claim")
    private ConfigurationAttributeStr oidcGroupsClaim;

    @SerializedName("oidc_name")
    private ConfigurationAttributeStr oidcName;

    @SerializedName("oidc_scope")
    private ConfigurationAttributeStr oidcScope;

    @SerializedName("oidc_verify_cert")
    private ConfigurationAttributeBool oidcVerifyCert;

    @SerializedName("project_creation_restriction")
    private ConfigurationAttributeStr projectCreationRestriction;

    @SerializedName("quota_per_project_enable")
    private ConfigurationAttributeBool quotaPerProjectEnable;

    @SerializedName("read_only")
    private ConfigurationAttributeBool readOnly;

    @SerializedName("robot_token_duration")
    private ConfigurationAttributeInt robotTokenDuration;

    @SerializedName("scan_all_policy")
    private ConfigurationAttributeObj scanAllPolicy;

    @SerializedName("self_registration")
    private ConfigurationAttributeBool selfRegistration;

    @SerializedName("storage_per_project")
    private ConfigurationAttributeInt storagePerProject;

    @SerializedName("token_expiration")
    private ConfigurationAttributeInt tokenExpiration;

    @SerializedName("uaa_client_id")
    private ConfigurationAttributeStr uaaClientId;

    @SerializedName("uaa_client_secret")
    private ConfigurationAttributeStr uaaClientSecret;

    @SerializedName("uaa_endpoint")
    private ConfigurationAttributeStr uaaEndpoint;

    @SerializedName("uaa_verify_cert")
    private ConfigurationAttributeBool uaaVerifyCert;

    public ConfigurationAttributeStr getAuthMode() {
        return authMode;
    }

    public void setAuthMode(ConfigurationAttributeStr authMode) {
        this.authMode = authMode;
    }

    public ConfigurationAttributeInt getCountPerProject() {
        return countPerProject;
    }

    public void setCountPerProject(ConfigurationAttributeInt countPerProject) {
        this.countPerProject = countPerProject;
    }

    public ConfigurationAttributeStr getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(ConfigurationAttributeStr emailFrom) {
        this.emailFrom = emailFrom;
    }

    public ConfigurationAttributeStr getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(ConfigurationAttributeStr emailHost) {
        this.emailHost = emailHost;
    }

    public ConfigurationAttributeStr getEmailIdentity() {
        return emailIdentity;
    }

    public void setEmailIdentity(ConfigurationAttributeStr emailIdentity) {
        this.emailIdentity = emailIdentity;
    }

    public ConfigurationAttributeBool getEmailInsecure() {
        return emailInsecure;
    }

    public void setEmailInsecure(ConfigurationAttributeBool emailInsecure) {
        this.emailInsecure = emailInsecure;
    }

    public ConfigurationAttributeInt getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(ConfigurationAttributeInt emailPort) {
        this.emailPort = emailPort;
    }

    public ConfigurationAttributeBool getEmailSsl() {
        return emailSsl;
    }

    public void setEmailSsl(ConfigurationAttributeBool emailSsl) {
        this.emailSsl = emailSsl;
    }

    public ConfigurationAttributeStr getEmailUsername() {
        return emailUsername;
    }

    public void setEmailUsername(ConfigurationAttributeStr emailUsername) {
        this.emailUsername = emailUsername;
    }

    public ConfigurationAttributeStr getHttpAuthproxyEndpoint() {
        return httpAuthproxyEndpoint;
    }

    public void setHttpAuthproxyEndpoint(ConfigurationAttributeStr httpAuthproxyEndpoint) {
        this.httpAuthproxyEndpoint = httpAuthproxyEndpoint;
    }

    public ConfigurationAttributeStr getHttpAuthproxyServerCertificate() {
        return httpAuthproxyServerCertificate;
    }

    public void setHttpAuthproxyServerCertificate(ConfigurationAttributeStr httpAuthproxyServerCertificate) {
        this.httpAuthproxyServerCertificate = httpAuthproxyServerCertificate;
    }

    public ConfigurationAttributeBool getHttpAuthproxySkipSearch() {
        return httpAuthproxySkipSearch;
    }

    public void setHttpAuthproxySkipSearch(ConfigurationAttributeBool httpAuthproxySkipSearch) {
        this.httpAuthproxySkipSearch = httpAuthproxySkipSearch;
    }

    public ConfigurationAttributeBool getHttpAuthproxyTokenreviewEndpoint() {
        return httpAuthproxyTokenreviewEndpoint;
    }

    public void setHttpAuthproxyTokenreviewEndpoint(ConfigurationAttributeBool httpAuthproxyTokenreviewEndpoint) {
        this.httpAuthproxyTokenreviewEndpoint = httpAuthproxyTokenreviewEndpoint;
    }

    public ConfigurationAttributeBool getHttpAuthproxyVerifyCert() {
        return httpAuthproxyVerifyCert;
    }

    public void setHttpAuthproxyVerifyCert(ConfigurationAttributeBool httpAuthproxyVerifyCert) {
        this.httpAuthproxyVerifyCert = httpAuthproxyVerifyCert;
    }

    public ConfigurationAttributeStr getLdapBaseDn() {
        return ldapBaseDn;
    }

    public void setLdapBaseDn(ConfigurationAttributeStr ldapBaseDn) {
        this.ldapBaseDn = ldapBaseDn;
    }

    public ConfigurationAttributeStr getLdapFilter() {
        return ldapFilter;
    }

    public void setLdapFilter(ConfigurationAttributeStr ldapFilter) {
        this.ldapFilter = ldapFilter;
    }

    public ConfigurationAttributeStr getLdapGroupAdminDn() {
        return ldapGroupAdminDn;
    }

    public void setLdapGroupAdminDn(ConfigurationAttributeStr ldapGroupAdminDn) {
        this.ldapGroupAdminDn = ldapGroupAdminDn;
    }

    public ConfigurationAttributeStr getLdapGroupAttributeName() {
        return ldapGroupAttributeName;
    }

    public void setLdapGroupAttributeName(ConfigurationAttributeStr ldapGroupAttributeName) {
        this.ldapGroupAttributeName = ldapGroupAttributeName;
    }

    public ConfigurationAttributeStr getLdapGroupBaseDn() {
        return ldapGroupBaseDn;
    }

    public void setLdapGroupBaseDn(ConfigurationAttributeStr ldapGroupBaseDn) {
        this.ldapGroupBaseDn = ldapGroupBaseDn;
    }

    public ConfigurationAttributeStr getLdapGroupMembershipAttribute() {
        return ldapGroupMembershipAttribute;
    }

    public void setLdapGroupMembershipAttribute(ConfigurationAttributeStr ldapGroupMembershipAttribute) {
        this.ldapGroupMembershipAttribute = ldapGroupMembershipAttribute;
    }

    public ConfigurationAttributeStr getLdapGroupSearchFilter() {
        return ldapGroupSearchFilter;
    }

    public void setLdapGroupSearchFilter(ConfigurationAttributeStr ldapGroupSearchFilter) {
        this.ldapGroupSearchFilter = ldapGroupSearchFilter;
    }

    public ConfigurationAttributeInt getLdapGroupSearchScope() {
        return ldapGroupSearchScope;
    }

    public void setLdapGroupSearchScope(ConfigurationAttributeInt ldapGroupSearchScope) {
        this.ldapGroupSearchScope = ldapGroupSearchScope;
    }

    public ConfigurationAttributeInt getLdapScope() {
        return ldapScope;
    }

    public void setLdapScope(ConfigurationAttributeInt ldapScope) {
        this.ldapScope = ldapScope;
    }

    public ConfigurationAttributeStr getLdapSearchDn() {
        return ldapSearchDn;
    }

    public void setLdapSearchDn(ConfigurationAttributeStr ldapSearchDn) {
        this.ldapSearchDn = ldapSearchDn;
    }

    public ConfigurationAttributeInt getLdapTimeout() {
        return ldapTimeout;
    }

    public void setLdapTimeout(ConfigurationAttributeInt ldapTimeout) {
        this.ldapTimeout = ldapTimeout;
    }

    public ConfigurationAttributeStr getLdapUid() {
        return ldapUid;
    }

    public void setLdapUid(ConfigurationAttributeStr ldapUid) {
        this.ldapUid = ldapUid;
    }

    public ConfigurationAttributeStr getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(ConfigurationAttributeStr ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public ConfigurationAttributeBool getLdapVerifyCert() {
        return ldapVerifyCert;
    }

    public void setLdapVerifyCert(ConfigurationAttributeBool ldapVerifyCert) {
        this.ldapVerifyCert = ldapVerifyCert;
    }

    public ConfigurationAttributeBool getNotificationEnable() {
        return notificationEnable;
    }

    public void setNotificationEnable(ConfigurationAttributeBool notificationEnable) {
        this.notificationEnable = notificationEnable;
    }

    public ConfigurationAttributeStr getOidcClientId() {
        return oidcClientId;
    }

    public void setOidcClientId(ConfigurationAttributeStr oidcClientId) {
        this.oidcClientId = oidcClientId;
    }

    public ConfigurationAttributeStr getOidcEndpoint() {
        return oidcEndpoint;
    }

    public void setOidcEndpoint(ConfigurationAttributeStr oidcEndpoint) {
        this.oidcEndpoint = oidcEndpoint;
    }

    public ConfigurationAttributeStr getOidcGroupsClaim() {
        return oidcGroupsClaim;
    }

    public void setOidcGroupsClaim(ConfigurationAttributeStr oidcGroupsClaim) {
        this.oidcGroupsClaim = oidcGroupsClaim;
    }

    public ConfigurationAttributeStr getOidcName() {
        return oidcName;
    }

    public void setOidcName(ConfigurationAttributeStr oidcName) {
        this.oidcName = oidcName;
    }

    public ConfigurationAttributeStr getOidcScope() {
        return oidcScope;
    }

    public void setOidcScope(ConfigurationAttributeStr oidcScope) {
        this.oidcScope = oidcScope;
    }

    public ConfigurationAttributeBool getOidcVerifyCert() {
        return oidcVerifyCert;
    }

    public void setOidcVerifyCert(ConfigurationAttributeBool oidcVerifyCert) {
        this.oidcVerifyCert = oidcVerifyCert;
    }

    public ConfigurationAttributeStr getProjectCreationRestriction() {
        return projectCreationRestriction;
    }

    public void setProjectCreationRestriction(ConfigurationAttributeStr projectCreationRestriction) {
        this.projectCreationRestriction = projectCreationRestriction;
    }

    public ConfigurationAttributeBool getQuotaPerProjectEnable() {
        return quotaPerProjectEnable;
    }

    public void setQuotaPerProjectEnable(ConfigurationAttributeBool quotaPerProjectEnable) {
        this.quotaPerProjectEnable = quotaPerProjectEnable;
    }

    public ConfigurationAttributeBool getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(ConfigurationAttributeBool readOnly) {
        this.readOnly = readOnly;
    }

    public ConfigurationAttributeInt getRobotTokenDuration() {
        return robotTokenDuration;
    }

    public void setRobotTokenDuration(ConfigurationAttributeInt robotTokenDuration) {
        this.robotTokenDuration = robotTokenDuration;
    }

    public ConfigurationAttributeObj getScanAllPolicy() {
        return scanAllPolicy;
    }

    public void setScanAllPolicy(ConfigurationAttributeObj scanAllPolicy) {
        this.scanAllPolicy = scanAllPolicy;
    }

    public ConfigurationAttributeBool getSelfRegistration() {
        return selfRegistration;
    }

    public void setSelfRegistration(ConfigurationAttributeBool selfRegistration) {
        this.selfRegistration = selfRegistration;
    }

    public ConfigurationAttributeInt getStoragePerProject() {
        return storagePerProject;
    }

    public void setStoragePerProject(ConfigurationAttributeInt storagePerProject) {
        this.storagePerProject = storagePerProject;
    }

    public ConfigurationAttributeInt getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(ConfigurationAttributeInt tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public ConfigurationAttributeStr getUaaClientId() {
        return uaaClientId;
    }

    public void setUaaClientId(ConfigurationAttributeStr uaaClientId) {
        this.uaaClientId = uaaClientId;
    }

    public ConfigurationAttributeStr getUaaClientSecret() {
        return uaaClientSecret;
    }

    public void setUaaClientSecret(ConfigurationAttributeStr uaaClientSecret) {
        this.uaaClientSecret = uaaClientSecret;
    }

    public ConfigurationAttributeStr getUaaEndpoint() {
        return uaaEndpoint;
    }

    public void setUaaEndpoint(ConfigurationAttributeStr uaaEndpoint) {
        this.uaaEndpoint = uaaEndpoint;
    }

    public ConfigurationAttributeBool getUaaVerifyCert() {
        return uaaVerifyCert;
    }

    public void setUaaVerifyCert(ConfigurationAttributeBool uaaVerifyCert) {
        this.uaaVerifyCert = uaaVerifyCert;
    }
}
