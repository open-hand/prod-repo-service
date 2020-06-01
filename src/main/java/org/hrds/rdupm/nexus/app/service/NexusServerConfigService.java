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
	 * 设置并返回当前nexus服务信息
	 * @param nexusClient nexus服务client
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusInfo(NexusClient nexusClient);

	/**
	 * 设置当前用户为nexus服务访问用户
	 * @param nexusClient nexus服务client
	 */
	void setCurrentNexusInfo(NexusClient nexusClient);

	/**
	 * nexus服务配置信息创建
	 * @param nexusServerConfig nexusServerConfig
	 * @return NexusServerConfig
	 */
	NexusServerConfig createServerConfig(NexusServerConfig nexusServerConfig);

	/**
	 * nexus服务配置信息更新
	 * @param nexusServerConfig nexusServerConfig
	 * @return NexusServerConfig
	 */
	NexusServerConfig updateServerConfig(NexusServerConfig nexusServerConfig);

	/**
	 * 查询 nexus服务信息配置
	 * @return NexusServerConfig
	 */
	NexusServerConfig queryServerConfig();

}
