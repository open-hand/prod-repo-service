package org.hrds.rdupm.nexus.client.nexus;

import org.hrds.rdupm.nexus.client.nexus.api.*;
import org.hrds.rdupm.nexus.client.nexus.api.http.NexusComponentsHttpApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@Component
public class NexusHttpClient implements NexusClient {
	@Autowired
	private NexusRepositoryApi nexusRepositoryApi;
	@Autowired
	private NexusComponentsHttpApi nexusComponentsHttpApi;
	@Autowired
	private NexusPrivilegeApi nexusPrivilegeApi;
	@Autowired
	private NexusBlobStoreApi nexusBlobStoreApi;
	@Autowired
	private NexusRoleApi nexusRoleApi;
	@Autowired
	private NexusUserApi nexusUserApi;
	@Autowired
	private NexusScriptApi nexusScriptApi;
	@Autowired
	private NexusRequest nexusRequest;


	@Override
	public void setNexusServerInfo(NexusServer nexusServer) {
		nexusRequest.setNexusServerInfo(nexusServer);
	}

	@Override
	public void initData() {
		// 脚本数据
		nexusScriptApi.initScript();
		// 默认角色数据
		NexusServerRole serverRole = nexusRoleApi.getRoleById(NexusApiConstants.defaultRole.DEFAULT_ROLE);
		if (serverRole == null) {
			NexusServerRole newRole = new NexusServerRole();
			newRole.setId(NexusApiConstants.defaultRole.DEFAULT_ROLE);
			newRole.setName(NexusApiConstants.defaultRole.DEFAULT_ROLE);
			newRole.setDescription(NexusApiConstants.defaultRole.DEFAULT_ROLE_NAME);
			nexusRoleApi.createRole(newRole);
		}
	}

	@Override
	public void removeNexusServerInfo() {
		nexusRequest.removeNexusServerInfo();
	}

	@Override
	public NexusRepositoryApi getRepositoryApi() {
		return nexusRepositoryApi;
	}

	@Override
	public NexusComponentsApi getComponentsApi() {
		return nexusComponentsHttpApi;
	}

	@Override
	public NexusPrivilegeApi getPrivilegeApi() {
		return nexusPrivilegeApi;
	}

	@Override
	public NexusBlobStoreApi getBlobStoreApi() {
		return nexusBlobStoreApi;
	}

	@Override
	public NexusRoleApi getNexusRoleApi() {
		return nexusRoleApi;
	}

	@Override
	public NexusUserApi getNexusUserApi() {
		return nexusUserApi;
	}

	@Override
	public NexusScriptApi getNexusScriptApi() {
		return nexusScriptApi;
	}
}
