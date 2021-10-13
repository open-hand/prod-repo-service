package org.hrds.rdupm.init.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 2020/12/9
 */
@Component
@ConfigurationProperties(prefix = "nexus.proxy")
public class NexusProxyConfigProperties {
    /**
     * 代理服务的地址
     */
    private String url;

    /**
     * uri前缀
     */
    private String uriPrefix;
    /**
     * nexus服务的地址
     */
    private String base;

    private String servletUri;

    private String servicesGatewayUrl;

    private String serviceRoute;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServiceRoute() {
        return serviceRoute;
    }

    public void setServiceRoute(String serviceRoute) {
        this.serviceRoute = serviceRoute;
    }

    public String getServicesGatewayUrl() {
        return servicesGatewayUrl;
    }

    public void setServicesGatewayUrl(String servicesGatewayUrl) {
        this.servicesGatewayUrl = servicesGatewayUrl;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getServletUri() {
        return servletUri;
    }

    public void setServletUri(String servletUri) {
        this.servletUri = servletUri;
    }
}
