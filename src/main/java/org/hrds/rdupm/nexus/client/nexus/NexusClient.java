package org.hrds.rdupm.nexus.client.nexus;

import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public interface NexusClient {

	/**
	 * 设置nexus服务信息
	 * @param nexusServer 服务信息
	 */
	void setNexusServerInfo(NexusServer nexusServer);

	/**
	 * 获取仓库API类
	 * @return NexusRepositoryApi
	 */
	NexusRepositoryApi getRepositoryApi();
}
