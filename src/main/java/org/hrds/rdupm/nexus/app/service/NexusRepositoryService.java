package org.hrds.rdupm.nexus.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerBlobStore;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

import java.util.List;

/**
 * 制品库_nexus仓库信息表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface NexusRepositoryService {
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

	/**
	 * 删除maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryId 仓库主键Id
	 */
	void deleteMavenRepo(Long organizationId, Long projectId, Long repositoryId);

	/**
	 * 关联仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusRepositoryRelatedDTO 关联信息
	 * @return NexusRepositoryRelatedDTO
	 */
	NexusRepositoryRelatedDTO relatedMavenRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO);

	/**
	 * 关联仓库 关联仓库，单条数据
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusRepositoryRelatedDTO 关联信息
	 * @param repositoryName 仓库名称
	 * @param nexusServerConfig 配置信息
	 */
	void selfRelatedMavenRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO,
							  String repositoryName, NexusServerConfig nexusServerConfig);

	/**
	 * maven仓库 关联， 获取仓库列表
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @return List<NexusServerRepository>
	 */
	List<NexusServerRepository> listRelatedMavenRepo(Long organizationId, Long projectId);

	/**
	 * 查询maven仓库列表，自建或关联的
	 * @param pageRequest 分页参数
	 * @param queryDTO 查询参数
	 * @return PageInfo<NexusRepositoryDTO>
	 */
	PageInfo<NexusRepositoryDTO> listMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO);

	/**
	 * 查询maven仓库列表，项目之外的其它仓库
	 * @param pageRequest 分页参数
	 * @param queryDTO 查询参数
	 * @return PageInfo<NexusRepositoryDTO>
	 */
	PageInfo<NexusRepositoryDTO> listOtherMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO);

	/**
	 * 查询blob
	 * @return List<NexusServerBlobStore>
	 */
	List<NexusServerBlobStore> listMavenRepoBlob();

	/**
	 * 获取仓库名列表
	 * @param projectId 项目Id
	 * @param excludeRelated 是否排除所有项目已关联或新建的  true:需要 false:不需要
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoNameAll(Long projectId, Boolean excludeRelated);

	/**
	 * 获取仓库名列表 - 当前项目关联的
	 * @param projectId 项目Id
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoNameByProjectId(Long projectId);

	/**
	 * 包上传 - 仓库列表
	 * @param projectId 项目Id
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listComponentRepo(Long projectId);

	/**
	 * 发布权限编辑，仓库列表
	 * @param projectId 项目Id
	 * @param currentRepoName 当前默认仓库名称
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoPush(Long projectId, String currentRepoName);

	/**
	 * 查询maven 仓库配置指引信息
	 * @param repositoryName 仓库名称
	 * @param showPushFlag 是否返回发布的配置信息  true:返回  false:不反回
	 * @return NexusGuideDTO
	 */
	NexusGuideDTO mavenRepoGuide(String repositoryName, Boolean showPushFlag);
}
