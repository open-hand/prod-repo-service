package org.hrds.rdupm.harbor.config;

import java.nio.charset.StandardCharsets;

import org.hrds.rdupm.nexus.client.nexus.config.NexusCustomErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 启动注入自定义RestTemplate
 *
 * @author ke.li@hand-china.com
 */
@Configuration
public class HarborRestConfiguration {

    @Bean(name = "hrdsHarborRestTemplate")
    public RestTemplate harborRestTemplate(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        //60s
        requestFactory.setConnectTimeout(601000);
        requestFactory.setReadTimeout(601000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        restTemplate.setErrorHandler(new NexusCustomErrorHandler());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}