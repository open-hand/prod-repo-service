package org.hrds.rdupm.nexus.client.nexus.api.http;

import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusScriptApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * nexus 脚本相关api
 * @author weisen.yang@hand-china.com 2020/3/20
 */
@Component
public class NexusScriptHttpApi implements NexusScriptApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(NexusScriptHttpApi.class);

	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public void uploadScript(NexusServerScript nexusScript) {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Script.UPLOAD_SCRIPT, HttpMethod.POST, null, nexusScript);
	}

	@Override
	public void updateScript(String scriptName, NexusServerScript nexusScript) {
		String url = NexusUrlConstants.Script.UPDATE_SCRIPT.replace("{scriptName}", scriptName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusScript);

	}

	@Override
	public void runScript(String scriptName, String param) {
		String url = NexusUrlConstants.Script.RUN_SCRIPT.replace("{scriptName}", scriptName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.POST, null, param, MediaType.TEXT_PLAIN_VALUE);
	}

	@Override
	public void deleteScript(String scriptName) {
		String url = NexusUrlConstants.Script.DELETE_SCRIPT.replace("{scriptName}", scriptName);
		try {
			ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.warn("nexus script has been deleted");
			} else {
				throw e;
			}
		}
	}
}
