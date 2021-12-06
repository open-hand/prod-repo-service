package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.common.api.vo.UserNexusInfo;
import org.hrds.rdupm.nexus.api.vo.NexusServerConfigVO;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

import java.util.List;

/**
 * 制品库_nexus服务信息配置表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusServerConfigService {


	/**
	 * 设置并返回项目下启用nexus服务信息 - 设置admin信息
	 * @param nexusClient nexus服务client
	 * @param projectId 项目Id
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusInfo(NexusClient nexusClient, Long projectId);

	/**
	 * 设置并返回Choerodon默认nexus服务 - 设置admin信息
	 * @param nexusClient nexus服务client
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusDefaultInfo(NexusClient nexusClient);

	/**
	 * 设置并返回当前主键对应nexus服务信息 - 设置admin信息
	 * @param nexusClient nexus服务client
	 * @param configId nexus服务配置主键Id
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusInfoByConfigId(NexusClient nexusClient, Long configId);
	/**
	 * 设置并返回当前仓库对应的nexus服务信息 - 设置admin信息
	 * @param nexusClient nexus服务client
	 * @param repositoryId 仓库主键Id
	 * @return NexusServerConfig
	 */
	NexusServerConfig setNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId);

	/**
	 * 设置当前用户为nexus服务访问用户 - 设置当前用户信息
	 * @param nexusClient nexus服务client
	 * @param repositoryId 仓库主键Id
	 * @return NexusServerConfig
	 */
	NexusServer setCurrentNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId);


	/**
	 * 制品库-创建自定义nexus服务
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusProjectService 服务配置信息
	 * @return NexusServerConfig 服务配置信息
	 */
	NexusServerConfig createServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusProjectService);

	/**
	 * nexus服务配置信息更新
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusServerConfig nexusServerConfig
	 * @return NexusServerConfig
	 */
	NexusServerConfig updateServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig);


	/**
	 * 项目层-nexus服务信息列表
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @return List<NexusServerConfig>
	 */
	List<NexusServerConfig> listServerConfig(Long organizationId, Long projectId);

	/**
	 * 项目层-nexus服务启用
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusServerConfig 启用服务
	 */
	void enableProjectServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig);

	NexusServerConfigVO queryNexusServiceConfigById(Long nexusServiceConfigId);

	void auditNexusLog(UserNexusInfo userNexusInfo);

}
