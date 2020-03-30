package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 制品库_nexus服务信息配置表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Service
public class NexusServerConfigServiceImpl implements NexusServerConfigService {
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;

	@Override
	public NexusServerConfig setNexusInfo(NexusClient nexusClient) {
		NexusServerConfig queryConfig = new NexusServerConfig();
		queryConfig.setEnabled(1);
		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectOne(queryConfig);
		if (nexusServerConfig == null) {
			// TODO
			throw new CommonException("nexus服务信息未配置，请联系管理员配置");
		}
		NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(), nexusServerConfig.getUserName(), nexusServerConfig.getPassword());
		nexusClient.setNexusServerInfo(nexusServer);
		return nexusServerConfig;
	}
}
