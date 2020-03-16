package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;

import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public interface NexusRepositoryApi {

	/**
	 * 获取nexus服务,仓库信息
	 * @return List<NexusRepository>
	 */
	List<NexusRepository> getRepository();
	
}
