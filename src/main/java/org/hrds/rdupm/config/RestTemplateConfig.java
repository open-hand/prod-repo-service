package org.hrds.rdupm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author weisen.yang@hand-china.com 2020/6/1
 */
@Configuration
public class RestTemplateConfig {

	@Primary
	@Bean
	public RestTemplate yhRestTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(601000);
		requestFactory.setReadTimeout(601000);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
}
