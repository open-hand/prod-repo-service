package org.hrds.rdupm.nexus.infra.repository.impl;

import org.hrds.rdupm.nexus.api.dto.NexusRepoDTO;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 制品库_nexus仓库信息表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@Component
public class NexusRepositoryRepositoryImpl extends BaseRepositoryImpl<NexusRepository> implements NexusRepositoryRepository {

	@Autowired
	private NexusRepositoryMapper nexusRepositoryMapper;

	@Override
	public List<String> getRepositoryByProject(Long projectId, String repoType) {
		return nexusRepositoryMapper.getRepositoryByProject(projectId, repoType);
	}

	@Override
	public List<NexusRepository> listRepositoryByProject(NexusRepository nexusRepository) {
		return nexusRepositoryMapper.listRepositoryByProject(nexusRepository);
	}

	@Override
	public Long distributeRepoInsert(NexusRepository nexusRepository) {
		return nexusRepositoryMapper.distributeRepoInsert(nexusRepository);
	}

	@Override
	public List<NexusRepoDTO> selectInfoByIds(List<Long> repositoryIds) {
		return nexusRepositoryMapper.selectInfoByIds(repositoryIds);
	}
}
