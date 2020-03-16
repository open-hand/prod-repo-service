package org.hrds.rdupm.nexus.client.nexus;

import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
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
	private NexusRequest nexusUtils;


	@Override
	public void setNexusServerInfo(NexusServer nexusServer) {
		nexusUtils.setNexusServerInfo(nexusServer);
	}

	@Override
	public NexusRepositoryApi getRepositoryApi() {
		return nexusRepositoryApi;
	}
}
