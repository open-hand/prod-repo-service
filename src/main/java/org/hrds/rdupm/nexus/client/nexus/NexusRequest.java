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
	@Qualifier("hrdsRestTemplate")
	private RestTemplate restTemplate;
	// TODO 调用remove
	private static ThreadLocal<NexusServer> nexusServerLocal = new ThreadLocal<NexusServer>();


	private static final String AUTH_PRE = "Basic ";
	private static final String AUTH_HEADER= "Authorization";
	private static final String MEDIA_TYPE= "application/json";

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



	public void setNexusServerInfo(NexusServer nexusServer){
		AssertUtils.notNull(nexusServer.getUsername(), "nexus username cannot null");
		AssertUtils.notNull(nexusServer.getPassword(), "nexus password cannot null");
		AssertUtils.notNull(nexusServer.getBaseUrl(), "nexus baseUrl cannot null");
		if (nexusServerLocal.get() == null) {
			nexusServerLocal.set(nexusServer);
		}
	}

	public ResponseEntity<String> exchange(String urlFix, HttpMethod method, Map<String, Object> paramMap, T body){
		NexusServer nexusServer = this.getNexusServer();
		String url = nexusServer.getBaseUrl() + urlFix;

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType(MEDIA_TYPE);
		headers.setContentType(type);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<T> entity = new HttpEntity<>(body, headers);
		if (paramMap == null) {
			paramMap = new HashMap<>(2);
		}

		try {
			return restTemplate.exchange(url, method, entity, String.class, paramMap);
		} catch (Exception e) {
			// TODO
			throw new CommonException(e.getMessage());
		}
	}






}
