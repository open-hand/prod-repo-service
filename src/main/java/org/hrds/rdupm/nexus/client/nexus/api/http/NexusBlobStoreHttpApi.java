package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusBlobStoreApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusBlobStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Component
public class NexusBlobStoreHttpApi implements NexusBlobStoreApi{
	@Autowired
	private NexusRequest nexusUtils;

	@Override
	public List<NexusBlobStore> getBlobStore() {
		ResponseEntity<String> responseEntity = nexusUtils.exchange(NexusUrlConstants.BlobStore.GET_BLOB_STORE_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusBlobStore.class);
	}
}
