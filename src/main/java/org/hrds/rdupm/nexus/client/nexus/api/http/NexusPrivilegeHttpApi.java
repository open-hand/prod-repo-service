package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusPrivilegeApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Component
public class NexusPrivilegeHttpApi implements NexusPrivilegeApi{
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusPrivilege> getPrivileges() {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Privileges.GET_PRIVILEGES_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusPrivilege.class);
	}

	@Override
	public List<NexusPrivilege> getPrivileges(String name) {
		List<NexusPrivilege> privilegeList = this.getPrivileges();
		return privilegeList.stream().filter(nexusPrivilege -> nexusPrivilege.getName().contains(name)).collect(Collectors.toList());
	}
}
