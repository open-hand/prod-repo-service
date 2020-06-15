package org.hrds.rdupm.nexus.domain.repository;

import org.hrds.rdupm.nexus.api.dto.NexusRepoDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;

import java.util.List;

/**
 * 制品库_nexus仓库信息表资源库
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface NexusRepositoryRepository extends BaseRepository<NexusRepository> {

	/**
	 * 查询项目关联或自建的仓库
	 * @param projectId 项目Id
	 * @param repoType 制品库类型
	 * @param configId nexus服务配置Id
	 * @return 仓库名称列表
	 */
	List<String> getRepositoryByProject(Long projectId, String repoType, Long configId);

	/**
	 * 查询仓库信息
	 * @param nexusRepository 参数
	 * @param configId nexus服务配置Id
	 * @return 仓库列表
	 */
	List<NexusRepository> listRepositoryByProject(NexusRepository nexusRepository, Long configId);

	/**
	 * 平台层仓库分配插入仓库数据
	 *
	 * @param nexusRepository
	 */
    Long distributeRepoInsert(NexusRepository nexusRepository);

	/**
	 * CI-流水线-获取项目下仓库列表-包含用户信息
	 * @param repositoryIds 主键Id
	 * @return List<NexusRepoDTO>
	 */
	List<NexusRepoDTO> selectInfoByIds(List<Long> repositoryIds);

	/**
	 * 组织层 - 仓库列表查询
	 * @param organizationId 组织Id
	 * @param repoType 仓库类型
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listOrgRepo(Long organizationId, String repoType);

}
