package org.hrds.rdupm.harbor.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 启动注入自定义RestTemplate
 *
 * @author xiuhong.chen@hand-china.com
 */
@Configuration
public class HarborRestConfiguration {

    private static final int MAK_TIMEOUT = 601000;

    @Value("harbor.skipSSL:false")
    private Boolean skipSSL;

    @Bean(name = "hrdsHarborRestTemplate")
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        if (skipSSL) {
            try {
                return new RestTemplate(factory);
            } catch (Exception e) {
                throw new RuntimeException("Unable to initiate RestTemplate for access wechat-proxy.");
            }
        } else {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(MAK_TIMEOUT);
            requestFactory.setReadTimeout(MAK_TIMEOUT);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            return restTemplate;
        }
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        LayeredConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf != null ? csf : SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(sfr);
        pollingConnectionManager.setMaxTotal(200);
        pollingConnectionManager.setDefaultMaxPerRoute(40);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).setConnectionManager(pollingConnectionManager).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(MAK_TIMEOUT);
        factory.setReadTimeout(MAK_TIMEOUT);
        factory.setConnectionRequestTimeout(MAK_TIMEOUT);
        return factory;
    }
}