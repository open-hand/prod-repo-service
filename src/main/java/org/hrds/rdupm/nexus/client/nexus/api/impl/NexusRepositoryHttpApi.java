package org.hrds.rdupm.nexus.client.nexus.api.impl;

import com.alibaba.fastjson.JSONObject;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@Component
public class NexusRepositoryHttpApi implements NexusRepositoryApi{
	@Autowired
	private NexusRequest nexusUtils;

	@Override
	public List<NexusRepository> getRepository() {
		ResponseEntity<String> responseEntity = nexusUtils.exchange(NexusUrlConstants.Repository.GET_REPOSITORY_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusRepository.class);
	}
}
