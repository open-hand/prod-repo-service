package org.hrds.rdupm.nexus.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.eventhandler.payload.NexusRepositoryDeletePayload;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.*;
import org.hrds.rdupm.nexus.domain.repository.*;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hrds.rdupm.nexus.infra.util.VelocityUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus仓库信息表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@Service
public class NexusRepositoryServiceImpl implements NexusRepositoryService, AopProxy<NexusRepositoryService> {

	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;
	@Autowired
	private NexusRoleRepository nexusRoleRepository;
	@Autowired
	private NexusUserRepository nexusUserRepository;
	@Autowired
	private NexusClient nexusClient;
	@Autowired
	private NexusServerConfigService configService;
	@Autowired
	private BaseServiceFeignClient baseServiceFeignClient;
	@Autowired
	private TransactionalProducer producer;

	@Override
	public NexusRepositoryDTO getMavenRepo(Long organizationId, Long projectId, Long repositoryId) {
		configService.setNexusInfo(nexusClient);

		NexusRepository query = new NexusRepository();
		query.setRepositoryId(repositoryId);
		query.setOrganizationId(organizationId);
		query.setProjectId(projectId);

		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		if (nexusRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
		if (nexusServerRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
		nexusRepositoryDTO.convert(nexusRepository, nexusServerRepository);
		nexusClient.removeNexusServerInfo();
		return nexusRepositoryDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@Saga(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			description = "创建maven仓库",
			inputSchemaClass = NexusRepositoryCreateDTO.class)
	public NexusRepositoryCreateDTO createMavenRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

		// 步骤
		// 1. 更新数据库数据
		// 2. 创建仓库
		// 3. 创建仓库默认角色，赋予权限：nx-repository-view-maven2-[仓库名]-*
		// 4. 创建仓库默认用户，默认赋予角色，上述创建的角色
		// 5. 是否允许匿名
		//     允许，赋予匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
		//     不允许，去除匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse

		// 参数校验
		nexusRepoCreateDTO.validParam(baseServiceFeignClient, true);

		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);


		if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())){
			throw new CommonException(NexusApiConstants.ErrorMessage.REPO_NAME_EXIST);
		}

		// 1. 数据库数据更新
		// 仓库
		NexusRepository nexusRepository = new NexusRepository();
		nexusRepository.setConfigId(serverConfig.getConfigId());
		nexusRepository.setNeRepositoryName(nexusRepoCreateDTO.getName());
		nexusRepository.setOrganizationId(organizationId);
		nexusRepository.setProjectId(projectId);
		nexusRepository.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
		nexusRepositoryRepository.insertSelective(nexusRepository);
		// 角色
		// 发布角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName(), true, null);
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName(), null);

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNeRoleId(nexusServerRole.getId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);
		// 用户
		// 发布用户
		NexusServerUser nexusServerUser = new NexusServerUser();
		nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId(), null);
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId(), null);

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		nexusUser.setNeUserId(nexusServerUser.getUserId());
		nexusUser.setNeUserPassword(nexusServerUser.getPassword());
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(pullNexusServerUser.getPassword());
		nexusUser.setIsDefault(1);
		nexusUserRepository.insertSelective(nexusUser);

		producer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("mavenRepo")
						.withSourceId(projectId),
				builder -> {
					builder.withPayloadAndSerialize(nexusRepoCreateDTO)
							.withRefId(String.valueOf(nexusRepository.getRepositoryId()))
							.withSourceId(projectId);
				});


		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepoCreateDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@Saga(code = NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE,
			description = "更新maven仓库",
			inputSchemaClass = NexusRepositoryCreateDTO.class)
	public NexusRepositoryCreateDTO updateMavenRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

		// 参数校验
		nexusRepoCreateDTO.validParam(baseServiceFeignClient, false);

		// 设置并返回当前nexus服务信息
		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
		if (nexusRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		if (!nexusRepository.getProjectId().equals(projectId)) {
			throw new CommonException(NexusMessageConstants.NEXUS_MAVEN_REPO_NOT_CHANGE_OTHER_PRO);
		}
		if (!nexusRepository.getAllowAnonymous().equals(nexusRepoCreateDTO.getAllowAnonymous())) {
			nexusRepository.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
			nexusRepositoryRepository.updateOptional(nexusRepository, NexusRepository.FIELD_ALLOW_ANONYMOUS);
		}

		producer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("updateMavenRepo")
						.withSourceId(projectId),
				builder -> {
					builder.withPayloadAndSerialize(nexusRepoCreateDTO)
							.withRefId(String.valueOf(nexusRepository.getRepositoryId()))
							.withSourceId(projectId);
				});

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepoCreateDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@Saga(code = NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE,
			description = "删除maven仓库",
			inputSchemaClass = NexusRepositoryDeletePayload.class)
	public void deleteMavenRepo(Long organizationId, Long projectId, Long repositoryId) {
		// 仓库
		NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
		if (nexusRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		// 角色
		NexusRole roleQuery = new NexusRole();
		roleQuery.setRepositoryId(repositoryId);
		NexusRole nexusRole = nexusRoleRepository.selectOne(roleQuery);
		// 用户
		NexusUser userQuery = new NexusUser();
		userQuery.setRepositoryId(repositoryId);
		userQuery.setIsDefault(1);
		NexusUser nexusUser = nexusUserRepository.selectOne(userQuery);

		// 数据库数据删除
		nexusRepositoryRepository.deleteByPrimaryKey(nexusRepository);
		nexusRoleRepository.deleteByPrimaryKey(nexusRole);
		nexusUserRepository.deleteByPrimaryKey(nexusUser);


		NexusRepositoryDeletePayload deletePayload = new NexusRepositoryDeletePayload();
		deletePayload.setNexusRepository(nexusRepository);
		deletePayload.setNexusRole(nexusRole);
		deletePayload.setNexusUser(nexusUser);
		producer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("deleteMavenRepo")
						.withSourceId(projectId),
				builder -> {
					builder.withPayloadAndSerialize(deletePayload)
							.withRefId(String.valueOf(nexusRepository.getRepositoryId()))
							.withSourceId(projectId);
				});

	}

	@Override
	public NexusRepositoryRelatedDTO relatedMavenRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {

		// 设置并返回当前nexus服务信息
		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);
		// 校验权限
		List<String> pushRepoList = nexusClient.getNexusUserApi().validPush(nexusRepositoryRelatedDTO.getRepositoryList(), nexusRepositoryRelatedDTO.getUserName());

		List<String> remainderRepo = new ArrayList<>(nexusRepositoryRelatedDTO.getRepositoryList());
		remainderRepo.removeAll(pushRepoList);
		if (CollectionUtils.isNotEmpty(remainderRepo)) {
			// TODO 用户{0}，对以下仓库没有发布权限：{1}
			throw new CommonException("用户：" + nexusRepositoryRelatedDTO.getUserName() + ",对以下仓库没有发布权限：" + StringUtils.join(",", remainderRepo));
		}

		// 用户名、密码校验
		Boolean userFlag = nexusClient.getNexusUserApi().validUserNameAndPassword(
				nexusRepositoryRelatedDTO.getUserName(), nexusRepositoryRelatedDTO.getPassword(),
				new NexusServer(serverConfig.getServerUrl(), serverConfig.getUserName(), serverConfig.getPassword()));
		if (!userFlag) {
			throw new CommonException(NexusMessageConstants.NEXUS_USER_AND_PASSWORD_ERROR);
		}

		List<String> repositoryList = nexusRepositoryRelatedDTO.getRepositoryList();
		// 关联校验
		repositoryList.forEach(repositoryName -> {
			NexusRepository query = new NexusRepository();
			query.setNeRepositoryName(repositoryName);
			NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
			if (nexusRepository != null) {
				throw new CommonException(NexusMessageConstants.NEXUS_REPO_ALREADY_RELATED, repositoryName);
			}
		});

		List<String> errorNameList = new ArrayList<>();
		repositoryList.forEach(repositoryName -> {
			try {
				self().selfRelatedMavenRepo(organizationId, projectId, nexusRepositoryRelatedDTO, repositoryName, serverConfig);
			} catch (Exception e) {
				errorNameList.add(repositoryName);
			}
		});
		if (CollectionUtils.isNotEmpty(errorNameList)) {
			throw new CommonException(NexusMessageConstants.NEXUS_REPO_RELATED_ERROR, StringUtils.join(errorNameList, ", "));
		}

		nexusClient.removeNexusServerInfo();

		return nexusRepositoryRelatedDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	@Saga(code = NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED,
			description = "关联maven仓库",
			inputSchemaClass = NexusRepository.class)
	public void selfRelatedMavenRepo(Long organizationId, Long projectId, NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO, String repositoryName, NexusServerConfig nexusServerConfig) {
		// 数据库数据更新
		// 仓库
		NexusRepository nexusRepository = new NexusRepository();
		nexusRepository.setConfigId(nexusServerConfig.getConfigId());
		nexusRepository.setNeRepositoryName(repositoryName);
		nexusRepository.setOrganizationId(organizationId);
		nexusRepository.setProjectId(projectId);
		nexusRepository.setAllowAnonymous(1);
		nexusRepository.setIsRelated(1);
		nexusRepositoryRepository.insertSelective(nexusRepository);

		// 发布角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		nexusServerRole.createDefPushRole(repositoryName, false, null);
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(repositoryName, null);

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRole.setNeRoleId(nexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);

		// 用户
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(repositoryName, pullNexusServerRole.getId(), null);

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		nexusUser.setNeUserId(nexusRepositoryRelatedDTO.getUserName());
		nexusUser.setNeUserPassword(nexusRepositoryRelatedDTO.getPassword());
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(pullNexusServerUser.getPassword());
		nexusUser.setIsDefault(1);
		nexusUserRepository.insertSelective(nexusUser);

		producer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("relatedMavenRepo")
						.withSourceId(projectId),
				builder -> {
					builder.withPayloadAndSerialize(nexusRepository)
							.withRefId(String.valueOf(nexusRepository.getRepositoryId()))
							.withSourceId(projectId);
				});
	}

	@Override
	public List<NexusServerRepository> listRelatedMavenRepo(Long organizationId, Long projectId) {
		// TODO
		return null;
	}

//	@Override
//	public PageInfo<NexusRepositoryDTO> listMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO) {
//		configService.setNexusInfo(nexusClient);
//
//		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
//		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
//			return PageConvertUtils.convert();
//		}
//		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));
//
//		NexusRepository query = new NexusRepository();
//		query.setProjectId(queryDTO.getProjectId());
//		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
//
//		// 数据筛选
//		nexusRepositoryList = nexusRepositoryList.stream().filter(nexusRepository ->
//				nexusServerRepositoryMap.keySet().contains(nexusRepository.getNeRepositoryName())).collect(Collectors.toList());
//
//
//		// 查询参数
//		if (queryDTO.getName() != null) {
//			nexusRepositoryList = nexusRepositoryList.stream().filter(nexusRepository ->
//					nexusRepository.getNeRepositoryName().contains(queryDTO.getName())).collect(Collectors.toList());
//		}
//
//		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
//		nexusRepositoryList.forEach(nexusRepository -> {
//			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
//			nexusRepositoryDTO.convert(nexusRepository, nexusServerRepositoryMap.get(nexusRepository.getNeRepositoryName()));
//			resultAll.add(nexusRepositoryDTO);
//		});
//
//		// remove配置信息
//		nexusClient.removeNexusServerInfo();
//
//		return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
//	}

	@Override
	public PageInfo<NexusRepositoryDTO> listMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO, String queryData) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusRepositoryDTO> resultAll = this.queryMavenRepo(queryDTO, queryData);
		if (CollectionUtils.isEmpty(resultAll)) {
			return PageConvertUtils.convert();
		}
		// remove配置信息
		nexusClient.removeNexusServerInfo();

		return PageConvertUtils.convert(pageRequest.getPage() + 1, pageRequest.getSize(), resultAll);
	}

	@Override
	public List<NexusRepositoryDTO> listMavenRepoAll(NexusRepositoryQueryDTO queryDTO, String queryData) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusRepositoryDTO> resultAll = this.queryMavenRepo(queryDTO, queryData);
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return resultAll;
	}


	private List<NexusRepositoryDTO> queryMavenRepo(NexusRepositoryQueryDTO queryDTO, String queryData){
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		switch (queryData) {
			case NexusConstants.RepoQueryData.REPO_ALL:
				this.mavenRepoAll(nexusServerRepositoryList, queryDTO, resultAll);
				break;
			case NexusConstants.RepoQueryData.REPO_EXCLUDE_PROJECT:
				this.mavenRepoExcludeProject(nexusServerRepositoryList, queryDTO, resultAll);
				break;
			case NexusConstants.RepoQueryData.REPO_ORG:
				this.mavenRepoOrg(nexusServerRepositoryList, queryDTO, resultAll);
				break;
			case NexusConstants.RepoQueryData.REPO_PROJECT:
				this.mavenRepoProject(nexusServerRepositoryList, queryDTO, resultAll);
				break;
			default:
				break;
		}

		// 项目名称查询
		Set<Long> projectIdSet = resultAll.stream().map(NexusRepositoryDTO::getProjectId).collect(Collectors.toSet());
		List<ProjectVO> projectVOList = baseServiceFeignClient.queryByIds(projectIdSet);
		Map<Long, ProjectVO> projectVOMap = projectVOList.stream().collect(Collectors.toMap(ProjectVO::getId, a -> a, (k1, k2) -> k1));
		resultAll.forEach(nexusRepositoryDTO -> {
			ProjectVO projectVO = projectVOMap.get(nexusRepositoryDTO.getProjectId());
			if (projectVO != null) {
				nexusRepositoryDTO.setProjectName(projectVO.getName());
			}
		});

		// 查询参数
		if (queryDTO.getRepositoryName() != null) {
			resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
					nexusRepositoryDTO.getName().toLowerCase().contains(queryDTO.getRepositoryName().toLowerCase())).collect(Collectors.toList());
		}
		if (queryDTO.getType() != null) {
			resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
					nexusRepositoryDTO.getType().toLowerCase().contains(queryDTO.getType().toLowerCase())).collect(Collectors.toList());
		}
		if (queryDTO.getVersionPolicy() != null) {
			resultAll = resultAll.stream().filter(nexusRepositoryDTO ->
					nexusRepositoryDTO.getVersionPolicy() != null && nexusRepositoryDTO.getVersionPolicy().toLowerCase().contains(queryDTO.getVersionPolicy().toLowerCase())).collect(Collectors.toList());
		}
		return resultAll;
	}

	/**
	 * 查询所有仓库-信息处理
	 * @param nexusServerRepositoryList nexus服务仓库信息
	 * @param queryDTO 查询参数
	 * @param resultAll 放回结果
	 */
	private void mavenRepoAll(List<NexusServerRepository> nexusServerRepositoryList, NexusRepositoryQueryDTO queryDTO, List<NexusRepositoryDTO> resultAll){
		// 所有项目仓库数据
		NexusRepository query = new NexusRepository();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));

		nexusServerRepositoryList.forEach(serverRepository -> {
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(nexusRepositoryMap.get(serverRepository.getName()), serverRepository);
			resultAll.add(nexusRepositoryDTO);
		});

	}

	/**
	 * 查询某个组织仓库-信息处理
	 * @param nexusServerRepositoryList nexus服务仓库信息
	 * @param queryDTO 查询参数
	 * @param resultAll 放回结果
	 */
	private void mavenRepoOrg(List<NexusServerRepository> nexusServerRepositoryList, NexusRepositoryQueryDTO queryDTO, List<NexusRepositoryDTO> resultAll){

		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));

		// 查询某个组织项目数据
		Condition condition = Condition.builder(NexusRepository.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRepository.FIELD_ORGANIZATION_ID, queryDTO.getOrganizationId()))
				.build();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(condition);

		this.mavenRepoConvert(resultAll, nexusRepositoryList, nexusServerRepositoryMap);
	}

	/**
	 * 查询排除当前项目创建或关联的仓库后的仓库-信息处理
	 * @param nexusServerRepositoryList nexus服务仓库信息
	 * @param queryDTO 查询参数
	 * @param resultAll 放回结果
	 */
	private void mavenRepoExcludeProject(List<NexusServerRepository> nexusServerRepositoryList, NexusRepositoryQueryDTO queryDTO, List<NexusRepositoryDTO> resultAll){

		// 所有项目仓库数据
		NexusRepository query = new NexusRepository();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));

		// 过滤数据，排除当前项目的
		Set<String> currentProject = nexusRepositoryList.stream().filter(nexusRepository -> nexusRepository.getProjectId().equals(queryDTO.getProjectId()))
				.map(NexusRepository::getNeRepositoryName).collect(Collectors.toSet());
		nexusServerRepositoryList = nexusServerRepositoryList.stream().filter(nexusRoleRepository ->
				!currentProject.contains(nexusRoleRepository.getName())
		).collect(Collectors.toList());

		nexusServerRepositoryList.forEach(serverRepository -> {
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(nexusRepositoryMap.get(serverRepository.getName()), serverRepository);
			resultAll.add(nexusRepositoryDTO);
		});
	}

	/**
	 * 查询当前项目下创建或关联的仓库-信息处理
	 * @param nexusServerRepositoryList nexus服务仓库信息
	 * @param queryDTO 查询参数
	 * @param resultAll 放回结果
	 */
	private void mavenRepoProject(List<NexusServerRepository> nexusServerRepositoryList, NexusRepositoryQueryDTO queryDTO, List<NexusRepositoryDTO> resultAll){

		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));

		// 查询某个项目项目数据
		Condition condition = Condition.builder(NexusRepository.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRepository.FIELD_PROJECT_ID, queryDTO.getProjectId()))
				.build();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(condition);

		this.mavenRepoConvert(resultAll, nexusRepositoryList, nexusServerRepositoryMap);


	}
	private void mavenRepoConvert(List<NexusRepositoryDTO> resultAll,
								  List<NexusRepository> nexusRepositoryList,
								  Map<String, NexusServerRepository> nexusServerRepositoryMap){
		nexusRepositoryList.forEach(repository -> {
			NexusServerRepository nexusServerRepository = nexusServerRepositoryMap.get(repository.getNeRepositoryName());
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(repository, nexusServerRepository);
			resultAll.add(nexusRepositoryDTO);

		});
	}


	@Override
	public List<NexusServerBlobStore> listMavenRepoBlob() {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusServerBlobStore> blobStoreList = nexusClient.getBlobStoreApi().getBlobStore();
		blobStoreList.forEach(blobStore -> {
			blobStore.setBlobCount(null);
			blobStore.setTotalSizeInBytes(null);
			blobStore.setAvailableSpaceInBytes(null);
		});


		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return blobStoreList;
	}

	@Override
	public List<NexusRepositoryDTO> listRepoNameAll(Long projectId, Boolean excludeRelated) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// 所有nexus服务仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		// 所有项目仓库数据
		List<String> repositoryNameList = new ArrayList<>();
		if (excludeRelated) {
			repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(null);
		}
		List<String> finalRepositoryNameList = repositoryNameList;


		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		nexusServerRepositoryList.forEach(serverRepository -> {
			if (!finalRepositoryNameList.contains(serverRepository.getName())) {
				NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
				nexusRepositoryDTO.setName(serverRepository.getName());
				resultAll.add(nexusRepositoryDTO);
			}
		});

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return resultAll;
	}

	@Override
	public List<NexusRepositoryDTO> listRepoNameByProjectId(Long projectId) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// nexus服务，仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		List<String> serverRepositoryNameList = nexusServerRepositoryList.stream().map(NexusServerRepository::getName).collect(Collectors.toList());

		// 当前项目仓库数据
		List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId);
		if (CollectionUtils.isEmpty(repositoryNameList)) {
			return new ArrayList<>();
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		repositoryNameList.forEach(repositoryName -> {
			if (serverRepositoryNameList.contains(repositoryName)) {
				NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
				nexusRepositoryDTO.setName(repositoryName);
				resultAll.add(nexusRepositoryDTO);
			}
		});
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return resultAll;
	}

	@Override
	public List<NexusRepositoryDTO> listComponentRepo(Long projectId) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// nexus服务，仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));


		// 当前项目仓库数据
		List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId);
		if (CollectionUtils.isEmpty(repositoryNameList)) {
			return new ArrayList<>();
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		repositoryNameList.forEach(repositoryName -> {
			NexusServerRepository serverRepository = nexusServerRepositoryMap.get(repositoryName);
			if (serverRepository != null && NexusApiConstants.VersionPolicy.RELEASE.equals(serverRepository.getVersionPolicy())
					&& NexusApiConstants.RepositoryType.HOSTED.equals(serverRepository.getType())) {
				// 包上传时，需要限制为RELEASE
				NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
				nexusRepositoryDTO.setName(repositoryName);
				resultAll.add(nexusRepositoryDTO);
			}
		});
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return resultAll;
	}

	@Override
	public List<NexusRepositoryDTO> listRepoPush(Long projectId, String currentRepoName) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// nexus服务，仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));


		// 当前项目仓库数据
		List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId);
		// 排除当前仓库
		repositoryNameList.remove(currentRepoName);
		if (CollectionUtils.isEmpty(repositoryNameList)) {
			return new ArrayList<>();
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		repositoryNameList.forEach(repositoryName -> {
			NexusServerRepository serverRepository = nexusServerRepositoryMap.get(repositoryName);
			if (serverRepository != null && NexusApiConstants.RepositoryType.HOSTED.equals(serverRepository.getType())) {
				// 限制为hosted， proxy与group不需要
				NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
				nexusRepositoryDTO.setName(repositoryName);
				resultAll.add(nexusRepositoryDTO);
			}
		});
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return resultAll;
	}

	@Override
	public NexusGuideDTO mavenRepoGuide(String repositoryName, Boolean showPushFlag) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		NexusRepository query = new NexusRepository();
		query.setNeRepositoryName(repositoryName);
		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		NexusUser nexusUser = null;
		if (nexusRepository != null) {
			NexusUser queryUser = new NexusUser();
			queryUser.setRepositoryId(nexusRepository.getRepositoryId());
			nexusUser = nexusUserRepository.selectOne(queryUser);
		}



		NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(repositoryName);
		if (nexusServerRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		Map<String, Object> map = new HashMap<>(16);
		map.put("versionPolicy", nexusServerRepository.getVersionPolicy());
		map.put("repositoryName", nexusServerRepository.getName());
		map.put("url", nexusServerRepository.getUrl());
		map.put("type", nexusServerRepository.getType());

		NexusGuideDTO nexusGuideDTO = new NexusGuideDTO();

		// 拉取信息
		nexusGuideDTO.setPullServerFlag(nexusRepository != null && nexusRepository.getAllowAnonymous() != 1);
		if (nexusGuideDTO.getPullServerFlag() && nexusUser != null) {
			// 要显示的时候，返回数据
			map.put("username", nexusUser.getNePullUserId());
			nexusGuideDTO.setPullServerInfo(VelocityUtils.getJsonString(map, VelocityUtils.SET_SERVER_FILE_NAME));
			nexusGuideDTO.setPullPassword(nexusUser.getNePullUserPassword());
			nexusGuideDTO.setPullServerInfoPassword(nexusGuideDTO.getPullServerInfo().replace("[password]", nexusGuideDTO.getPullPassword()));
		}
		// pom 仓库配置
		nexusGuideDTO.setPullPomRepoInfo(VelocityUtils.getJsonString(map, VelocityUtils.POM_REPO_FILE_NAME));

		// 发布信息
		if (nexusServerRepository.getType().equals(NexusApiConstants.RepositoryType.GROUP)
				|| nexusServerRepository.getType().equals(NexusApiConstants.RepositoryType.PROXY)) {
			// group 与 proxy 不需要
			nexusGuideDTO.setShowPushFlag(false);
		} else {
			nexusGuideDTO.setShowPushFlag(showPushFlag);
			if (showPushFlag) {
				// 为true时，处理发布的信息
				if (nexusUser == null) {
					throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
				}
				map.put("username", nexusUser.getNeUserId());
				nexusGuideDTO.setPushPassword(nexusUser.getNeUserPassword());
				nexusGuideDTO.setPushServerInfo(VelocityUtils.getJsonString(map, VelocityUtils.SET_SERVER_FILE_NAME));
				nexusGuideDTO.setPushServerInfoPassword(nexusGuideDTO.getPushServerInfo().replace("[password]", nexusGuideDTO.getPushPassword()));

				nexusGuideDTO.setPushPomManageInfo(VelocityUtils.getJsonString(map, VelocityUtils.POM_MANGE_FILE_NAME));
				nexusGuideDTO.setPushCmd(NexusGuideDTO.PUSH_CMD);
			}
		}

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusGuideDTO;
	}
}
