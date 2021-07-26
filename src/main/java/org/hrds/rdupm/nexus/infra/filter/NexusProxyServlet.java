package org.hrds.rdupm.nexus.infra.filter;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.hrds.rdupm.nexus.infra.constant.NexusProxyConstants;
import org.hzero.core.base.BaseConstants;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by wangxiang on 2020/12/9
 */
public class NexusProxyServlet extends ProxyServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NexusProxyServlet.class);

    @Value("${nexus.proxy.skipSSL:false}")
    private Boolean skipSSL;

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        super.service(servletRequest, servletResponse);
    }

    @Override
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
        super.copyRequestHeaders(servletRequest, proxyRequest);
    }

    @Override
    protected String rewritePathInfoFromRequest(HttpServletRequest servletRequest) {
        String pathInfo = super.rewritePathInfoFromRequest(servletRequest);
        Long configId = (Long) servletRequest.getAttribute(NexusProxyConstants.CONFIG_SERVER_ID);
        if (configId != null) {
            return BaseConstants.Symbol.SLASH + StringUtils.substringAfter(StringUtils.substringAfter(pathInfo, BaseConstants.Symbol.SLASH), BaseConstants.Symbol.SLASH);
        }
        return pathInfo;
    }

    @Override
    protected HttpClient createHttpClient() {
        if (!skipSSL) {
            return super.createHttpClient();
        }
        //在这里配置HttpClient的是否跳过SSL证书校验
        LOGGER.info("跳过ssl证书校验");
        return createSkipSslHttpClient();
    }

    private CloseableHttpClient createSkipSslHttpClient() {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        LayeredConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf != null ? csf : SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(sfr);
        pollingConnectionManager.setMaxTotal(200);
        pollingConnectionManager.setDefaultMaxPerRoute(40);

        return HttpClients.custom().setSSLSocketFactory(csf).setConnectionManager(pollingConnectionManager).build();
    }
}
