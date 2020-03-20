package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.model.RepositoryMavenRequest;

import java.util.List;

/**
 * 仓库API
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public interface NexusRepositoryApi {

	/**
	 * 获取nexus服务,仓库信息
	 * @return List<NexusRepository>
	 */
	List<NexusRepository> getRepository();

	/**
	 * 获取仓库信息，通过名称
	 * @param repositoryName 仓库名称
	 * @return NexusRepository
	 */
	NexusRepository getRepositoryByName(String repositoryName);

	/**
	 * 仓库是否存在
	 * @param repositoryName 仓库名称
	 * @return Boolean   true：存在   false：不存在
	 */
	Boolean repositoryExists(String repositoryName);

	/**
	 * 删除仓库信息
	 * @param repositoryName 仓库名称
	 */
	void deleteRepository(String repositoryName);

	/**
	 * maven仓库创建
	 * @param repositoryRequest 创建信息
	 */
	void createMavenRepository(RepositoryMavenRequest repositoryRequest);

	/**
	 * maven仓库更新
	 * @param repositoryRequest 更新信息
	 */
	void updateMavenRepository(RepositoryMavenRequest repositoryRequest);
}
