package org.hrds.rdupm.nexus.client.nexus;

import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hzero.core.util.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
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
	private static ThreadLocal<NexusServer> NEXUS_SERVER_LOCAL = new ThreadLocal<>();


	private static final String AUTH_PRE = "Basic ";
	private static final String AUTH_HEADER= "Authorization";
	private static final String AND = "&";
	private static final String EQ = "=";
	private static final String QM = "?";


	private NexusServer getNexusServer(){
		if (NEXUS_SERVER_LOCAL.get() == null) {
			throw new CommonException(NexusApiConstants.ErrorMessage.NEXUS_INFO_NOT_CONF);
		}
		return NEXUS_SERVER_LOCAL.get();
	}

	private String getToken(){
		NexusServer nexusServer = NEXUS_SERVER_LOCAL.get();
		String basicInfo = nexusServer.getUsername() + ":" + nexusServer.getPassword();
		return  AUTH_PRE + Base64.getEncoder().encodeToString(basicInfo.getBytes());
	}
	private String setParam(String url, Map<String, Object> paramMap){
		if (paramMap == null || paramMap.isEmpty()) {
			return url;
		}
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url).append(QM);
		for (String key : paramMap.keySet()) {
			newUrl.append(key).append(EQ).append(paramMap.get(key)).append(AND);
		}
		return newUrl.toString().substring(0, newUrl.length() - AND.length());
	}
	private void handleResponseStatus(ResponseEntity<String> responseEntity){
		if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			throw new NexusResponseException(responseEntity.getStatusCode(), NexusApiConstants.ErrorMessage.NEXUS_USER_PASS_ERROR);
		} else if (responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
			throw new NexusResponseException(responseEntity.getStatusCode(), NexusApiConstants.ErrorMessage.NEXUS_ROLE_PRI_NOT_ASSIGNED);
		} else if (responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new NexusResponseException(responseEntity.getStatusCode(), NexusApiConstants.ErrorMessage.NEXUS_SERVER_ERROR);
		} else if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
			throw new NexusResponseException(responseEntity.getStatusCode(), responseEntity.getBody());
		} else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NexusResponseException(responseEntity.getStatusCode(), NexusApiConstants.ErrorMessage.RESOURCE_NOT_EXIST);
		}
	}


	/**
	 * 设置nexus服务信息
	 * @param nexusServer nexus服务信息
	 */
	public void setNexusServerInfo(NexusServer nexusServer){
		AssertUtils.notNull(nexusServer.getUsername(), "nexus username cannot null");
		AssertUtils.notNull(nexusServer.getPassword(), "nexus password cannot null");
		AssertUtils.notNull(nexusServer.getBaseUrl(), "nexus baseUrl cannot null");
		NEXUS_SERVER_LOCAL.set(nexusServer);
	}

	/**
	 * 取消nexus服务信息
	 */
	public void removeNexusServerInfo(){
		NEXUS_SERVER_LOCAL.remove();
	}

	/**
	 * 请求
	 * @param urlFix 请求地址(截掉IP与端口号后的)
	 * @param method 请求方式
	 * @param paramMap url ?后面接的参数
	 * @param body body的参数
	 *  mediaType 默认：application/json
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> exchange(String urlFix, HttpMethod method, Map<String, Object> paramMap, Object body){
		NexusServer nexusServer = this.getNexusServer();
		 String url = nexusServer.getBaseUrl() + urlFix;

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(type);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<Object> entity = new HttpEntity<>(body, headers);
		if (paramMap == null) {
			paramMap = new HashMap<>(2);
		}
		url = this.setParam(url, paramMap);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, method, entity, String.class, paramMap);
		} catch (RestClientException e) {
			throw new CommonException(NexusApiConstants.ErrorMessage.NEXUS_SERVER_ERROR);
		}
		this.handleResponseStatus(responseEntity);
		return responseEntity;
	}

	/**
	 * 请求
	 * @param urlFix 请求地址(截掉IP与端口号后的)
	 * @param method 请求方式
	 * @param paramMap url后面接的参数
	 * @param body body的参数
	 * @param mediaType 没传，默认：application/json
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> exchange(String urlFix, HttpMethod method, Map<String, Object> paramMap, Object body, String mediaType){
		NexusServer nexusServer = this.getNexusServer();
		String url = nexusServer.getBaseUrl() + urlFix;

		HttpHeaders headers = new HttpHeaders();
		MediaType type = null;
		if (mediaType != null) {
			type = MediaType.parseMediaType(mediaType);
		} else {
			type = MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE);
		}
		headers.setContentType(type);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<Object> entity = new HttpEntity<>(body, headers);
		if (paramMap == null) {
			paramMap = new HashMap<>(2);
		}
		url = this.setParam(url, paramMap);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, method, entity, String.class, paramMap);
		} catch (RestClientException e) {
			throw new CommonException(NexusApiConstants.ErrorMessage.NEXUS_SERVER_ERROR);
		}
		this.handleResponseStatus(responseEntity);
		return responseEntity;
	}

	/**
	 * 请求
	 * @param urlFix 请求地址(截掉IP与端口号后的)
	 * @param method 请求方式
	 * @param paramMap url后面接的参数
	 * @param body body的参数
	 *  默认：application/json
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> exchangeFormData(String urlFix, HttpMethod method, Map<String, Object> paramMap, MultiValueMap<String, Object> body){
		NexusServer nexusServer = this.getNexusServer();
		String url = nexusServer.getBaseUrl() + urlFix;

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType(MediaType.MULTIPART_FORM_DATA_VALUE);

		headers.setContentType(type);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		if (paramMap == null) {
			paramMap = new HashMap<>(2);
		}
		url = this.setParam(url, paramMap);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, method, entity, String.class, paramMap);
		} catch (RestClientException e) {
			throw new CommonException(NexusApiConstants.ErrorMessage.NEXUS_SERVER_ERROR);
		}
		this.handleResponseStatus(responseEntity);
		return responseEntity;
	}








}
