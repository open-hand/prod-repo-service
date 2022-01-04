package org.hrds.rdupm.config;

import java.util.HashMap;
import java.util.Map;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
//import org.hrds.rdupm.nexus.infra.filter.NexusFilter;
//import org.hrds.rdupm.nexus.infra.filter.NexusProxyServlet;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author weisen.yang@hand-china.com 2020/6/1
 */
@Configuration
public class RestTemplateConfig {

//    @Autowired
//    private NexusFilter nexusFilter;

    @Autowired
    private NexusProxyConfigProperties nexusProxyConfigProperties;

    @Bean(name = "yhRestTemplate")
    public RestTemplate yhRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(601000);
        requestFactory.setReadTimeout(601000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


//	@Bean
//    public FilterRegistrationBean<NexusFilter> registerAuthFilter() {
//        FilterRegistrationBean<NexusFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(nexusFilter);
//        registration.addUrlPatterns(nexusProxyConfigProperties.getServletUri());
//        registration.setOrder(1);
//        return registration;
//    }

//	@Bean
//	public ServletRegistrationBean<ProxyServlet> proxyServletRegistration() {
//		ServletRegistrationBean<ProxyServlet> registrationBean = new ServletRegistrationBean<>(new NexusProxyServlet());
//		registrationBean.addUrlMappings(nexusProxyConfigProperties.getServletUri());
//		//设置网址以及参数
//		Map<String, String> params = new HashMap<>(2);
//		params.put("targetUri", nexusProxyConfigProperties.getBase());
//		params.put("log", "true");
//		registrationBean.setInitParameters(params);
//		return registrationBean;
//	}
}
