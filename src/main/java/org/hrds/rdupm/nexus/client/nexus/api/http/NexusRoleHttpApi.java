package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRoleApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * nexus 角色API
 * @author weisen.yang@hand-china.com 2020/3/18
 */
@Component
public class NexusRoleHttpApi implements NexusRoleApi{
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusRole> getRoles() {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Role.GET_ROLE_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusRole.class);
	}

	@Override
	public NexusRole getRoleById(String roleId) {
		String url = NexusUrlConstants.Role.GET_ROLE_BY_ID + roleId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSON.parseObject(response, NexusRole.class);
	}

	@Override
	public void deleteRole(String roleId) {
		String url = NexusUrlConstants.Role.DELETE_ROLE + roleId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
	}

	@Override
	public void createRole(NexusRole nexusRole) {
		// 唯一性校验
		NexusRole existRole = this.getRoleById(nexusRole.getId());
		if (existRole != null) {
			throw new CommonException(NexusConstants.ErrorMessage.ROLE_EXIST);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Role.CREATE_ROLE, HttpMethod.POST, null, nexusRole);
	}

	@Override
	public void updateRole(NexusRole nexusRole) {
		String url = NexusUrlConstants.Role.UPDATE_ROLE + nexusRole.getId();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusRole);
	}
}
