package org.hrds.rdupm.nexus.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerBlobStore;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

import java.io.IOException;
import java.util.List;

/**
 * 制品库_nexus仓库信息表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface TestService {
	/**
	 * 创建maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusRepoCreateDTO 创建信息
	 * @return NexusRepositoryCreateDTO
	 */
	NexusRepositoryCreateDTO createMavenRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO);

	/**
	 * 更新maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryId 仓库主键Id
	 * @param nexusRepoCreateDTO 创建信息
	 * @return NexusRepositoryCreateDTO
	 */
	NexusRepositoryCreateDTO updateMavenRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO);

//	/**
//	 * 创建maven仓库: 创建nexus server仓库
//	 * @param message 创建信息
//	 * @return NexusRepository
//	 */
//	NexusRepository createMavenRepoSaga(String message) throws IOException;
//
//	/**
//	 * 创建maven仓库：创建角色
//	 * @param message 创建信息
//	 * @return NexusRepository
//	 */
//	NexusRepository createMavenRepoRoleSaga(String message) throws IOException;
//
//	/**
//	 * 创建maven仓库：创建用户
//	 * @param message 创建信息
//	 * @return NexusRepository
//	 */
//	NexusRepository createMavenRepoUserSaga(String message) throws IOException;


}
