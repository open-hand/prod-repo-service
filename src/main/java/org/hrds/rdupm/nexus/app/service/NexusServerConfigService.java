package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

/**
 * 制品库_nexus服务信息配置表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusServerConfigService {


	/**
	 * 设置并返回，当前nexus服务信息
	 * @param nexusClient nexus服务client
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusInfo(NexusClient nexusClient);
}
