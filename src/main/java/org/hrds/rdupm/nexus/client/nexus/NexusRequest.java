package org.hrds.rdupm.nexus.client.nexus;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.ss.formula.functions.T;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hzero.core.util.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@Component
public class NexusRequest {
	@Autowired
	@Qualifier("hrdsNexusRestTemplate")
	private RestTemplate restTemplate;
	// TODO 调用remove
	private static ThreadLocal<NexusServer> nexusServerLocal = new ThreadLocal<NexusServer>();


	private static final String AUTH_PRE = "Basic ";
	private static final String AUTH_HEADER= "Authorization";
	private static final String MEDIA_TYPE= "application/json";
	private static final String AND = "&";
	private static final String EQ = "=";


	private NexusServer getNexusServer(){
		if (nexusServerLocal.get() == null) {
			// todo
			throw new CommonException("nexus server info is null");
		}
		return nexusServerLocal.get();
	}

	private String getToken(){
		NexusServer nexusServer = nexusServerLocal.get();
		String basicInfo = nexusServer.getUsername() + ":" + nexusServer.getPassword();
		return  AUTH_PRE + Base64.getEncoder().encodeToString(basicInfo.getBytes());
	}
	private String setParam(String url, Map<String, Object> paramMap){
		if (paramMap == null || paramMap.isEmpty()) {
			return url;
		}
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url).append("?");
		for (String key : paramMap.keySet()) {
			newUrl.append(key).append("=").append(paramMap.get(key)).append("&");
		}
		return newUrl.toString().substring(0, newUrl.length() - "&".length());
	}



	public void setNexusServerInfo(NexusServer nexusServer){
		AssertUtils.notNull(nexusServer.getUsername(), "nexus username cannot null");
		AssertUtils.notNull(nexusServer.getPassword(), "nexus password cannot null");
		AssertUtils.notNull(nexusServer.getBaseUrl(), "nexus baseUrl cannot null");
		if (nexusServerLocal.get() == null) {
			nexusServerLocal.set(nexusServer);
		}
	}

	public ResponseEntity<String> exchange(String urlFix, HttpMethod method, Map<String, Object> paramMap, Object body){
		NexusServer nexusServer = this.getNexusServer();
		 String url = nexusServer.getBaseUrl() + urlFix;

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType(MEDIA_TYPE);
		headers.setContentType(type);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<Object> entity = new HttpEntity<>(body, headers);
		if (paramMap == null) {
			paramMap = new HashMap<>(2);
		}
		url = this.setParam(url, paramMap);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, method, entity, String.class, paramMap);
		this.handleResponseStatus(responseEntity);
		return responseEntity;
	}

	public void handleResponseStatus(ResponseEntity<String> responseEntity){
		if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			throw new CommonException("请检查用户或密码是否正确");
		} else if (responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
			throw new CommonException("nexus角色权限未分配");
		}


	}








}
