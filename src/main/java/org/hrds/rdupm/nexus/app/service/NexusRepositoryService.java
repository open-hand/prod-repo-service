package org.hrds.rdupm.nexus.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerBlobStore;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;

import java.util.List;

/**
 * 制品库_nexus仓库信息表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface NexusRepositoryService {

	/**
	 * 仓库信息查询
	 * @param repositoryId 仓库表Id
	 * @return NexusRepositoryDTO
	 */
	NexusRepositoryDTO getRepo(Long organizationId, Long projectId, Long repositoryId);

	/**
	 * 创建maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusRepoCreateDTO 创建信息
	 * @return NexusRepositoryCreateDTO
	 */
	NexusRepositoryCreateDTO createRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO);

	/**
	 * 更新maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryId 仓库主键Id
	 * @param nexusRepoCreateDTO 创建信息
	 * @return NexusRepositoryCreateDTO
	 */
	NexusRepositoryCreateDTO updateRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO);

	/**
	 * 删除maven仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryId 仓库主键Id
	 */
	void deleteRepo(Long organizationId, Long projectId, Long repositoryId);

	/**
	 * 组织层 - 查询maven仓库列表
	 * @param pageRequest 分页参数
	 * @param queryDTO 查询参数
	 * @return Page<NexusRepositoryDTO>
	 */
	Page<NexusRepositoryDTO> listOrgRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO);

	/**
	 * 项目层 - 查询maven/npm仓库列表, 不分页
	 * @param queryDTO 查询参数
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoAll(NexusRepositoryQueryDTO queryDTO);


	/**
	 * 获取仓库名列表
	 * @param projectId 项目Id
	 * @param repoType 制品库类型
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoNameAll(Long projectId, String repoType);

	/**
	 * 获取仓库名列表 - 当前项目或组织的
	 * @param query 查询
	 * @param repoType 制品库类型
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listRepoName(NexusRepository query, String repoType);

	/**
	 * 包上传 - 仓库列表
	 * @param projectId 项目Id
	 * @param repoType 制品库类型
	 * @return List<NexusRepositoryDTO>
	 */
	List<NexusRepositoryDTO> listComponentRepo(Long projectId, String repoType);

	/**
	 * 查询maven 仓库配置指引信息
	 * @param repositoryName 仓库名称
	 * @param showPushFlag 是否返回发布的配置信息  true:返回  false:不反回
	 * @param repositoryId 仓库Id
	 * @return NexusGuideDTO
	 */
	NexusGuideDTO mavenRepoGuide(Long repositoryId, String repositoryName, Boolean showPushFlag);

//	/**
//	 * 查询maven仓库列表
//	 *
//	 * @param pageRequest 分页参数
//	 * @param queryDTO    查询参数
//	 * @param queryData   查看： NexusConstants.RepoQueryData
//	 * @return Page<NexusRepositoryDTO>
//	 */
//	Page<NexusRepositoryDTO> listNpmRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO, String queryData);

	/**
	 * 平台层-查询所有的nexus仓库信息
	 *
	 * @param pageRequest 分页参数
	 * @param queryDTO 查询参数
	 * @return 仓库信息列表
	 */
	Page<NexusRepositoryDTO> listNexusRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO);

	/**
	 * 平台层-仓库分配
	 *
	 * @param nexusRepoCreateDTO 创建关联关系(创建NexusRepository数据)
	 * @return
	 */
	NexusRepositoryCreateDTO repoDistribute(NexusRepositoryCreateDTO nexusRepoCreateDTO);

	String convertRepoTypeToFormat(String repoType);

	/**
	 * 仓库失效与生效
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryId 仓库Id
	 * @param enableFlag 标识
	 */
	void nexusRepoEnableAndDisAble(Long organizationId, Long projectId, Long repositoryId, String enableFlag);

	/**
	 * CI-流水线-获取项目下仓库列表
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repoType 制品类型
	 * @param type nexus仓库类型
	 * @return List<NexusRepoDTO>
	 */
	List<NexusRepoDTO> getRepoByProject(Long organizationId, Long projectId, String repoType, String type);

	/**
	 * CI-流水线-获取项目下仓库列表-包含用户信息
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repositoryIds 仓库主键list
	 * @return List<NexusRepoDTO>
	 */
	List<NexusRepoDTO> getRepoUserByProject(Long organizationId, Long projectId, List<Long> repositoryIds);

	/**
	 * 关联仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param nexusRepositoryRelatedDTO 关联仓库信息
	 * @return NexusRepositoryRelatedDTO
	 */
	NexusRepositoryRelatedDTO relatedMavenRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO);

	/**
	 * 关联仓库
	 * @param organizationId 组织Id
	 * @param projectId 项目Id
	 * @param repoType 类型
	 * @param repositoryName 仓库名
	 * @param serverConfig nexus服务配置
	 */
	void selfRelatedMavenRepo(Long organizationId, Long projectId, String repoType, String repositoryName, NexusServerConfig serverConfig);

}
