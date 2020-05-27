package org.hrds.rdupm.nexus.domain.repository;

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
	 * @return 仓库名称列表
	 */
	List<String> getRepositoryByProject(Long projectId, String repoType);
    
}
