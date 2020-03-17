package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.RepositoryRequest;

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
	 * 删除仓库信息
	 * @param repositoryName 仓库名称
	 */
	void deleteRepository(String repositoryName);

	/**
	 * maven仓库创建
	 * @param repositoryRequest 创建信息
	 */
	void createMavenRepository(RepositoryRequest repositoryRequest);

	/**
	 * maven仓库更新
	 * @param repositoryRequest 更新信息
	 */
	void updateMavenRepository(RepositoryRequest repositoryRequest);
}
