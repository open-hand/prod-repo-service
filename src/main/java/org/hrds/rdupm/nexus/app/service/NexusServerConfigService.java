package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

import java.util.List;

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
	 * 修改密码
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusServerConfig 修改信息
	 * @return NexusServerConfig
	 */
	NexusServerConfig updatePwd(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig);

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
}
