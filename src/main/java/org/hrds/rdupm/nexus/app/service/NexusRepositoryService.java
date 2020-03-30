package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.api.dto.ProdRepositoryDTO;

/**
 * 制品库_nexus仓库信息表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface NexusRepositoryService {

	/**
	 * 创建maven 制品仓库
	 * @param tenantId 租户Id
	 * @param prodRepositoryDTO 创建信息
	 * @return  ProdRepositoryDTO
	 */
	ProdRepositoryDTO createProdRepo(Long tenantId, ProdRepositoryDTO prodRepositoryDTO);

	/**
	 * 创建maven仓库
	 * @param tenantId 租户Id
	 * @param projectId 项目Id
	 * @param nexusRepoCreateDTO 创建信息
	 * @return NexusRepositoryCreateDTO
	 */
	NexusRepositoryCreateDTO createMavenRepo(Long tenantId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO);
}
