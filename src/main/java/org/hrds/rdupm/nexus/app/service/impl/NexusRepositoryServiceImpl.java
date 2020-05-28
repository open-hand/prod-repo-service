package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.eventhandler.payload.NexusRepositoryDeletePayload;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
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
import org.hrds.rdupm.util.DESEncryptUtil;
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
	@Autowired
	private NexusPushRepository nexusPushRepository;
	@Autowired
	private NexusAuthRepository nexusAuthRepository;
	@Autowired
	private NexusAuthService nexusAuthService;

	@Override
	public NexusRepositoryDTO getRepo(Long organizationId, Long projectId, Long repositoryId) {
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
	public NexusRepositoryCreateDTO createRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

		// 步骤
		// 1. 更新数据库数据
		// 2. 创建仓库
		// 3. 创建仓库默认拉取角色
		// 4. 创建仓库默认用户，默认赋予角色，上述创建的角色
		// 5. 是否允许匿名
		//     允许，赋予匿名用户权限，如： nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
		//     不允许，去除匿名用户权限，如：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse

		// 参数校验
		nexusRepoCreateDTO.validParam(baseServiceFeignClient, true);

		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);


		if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())) {
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
		nexusRepository.setRepoType(nexusRepoCreateDTO.getRepoType());
		nexusRepositoryRepository.insertSelective(nexusRepository);

		// 角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		// 发布角色
		nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName(), true, null, nexusRepoCreateDTO.getFormat());
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName(), null, nexusRepoCreateDTO.getFormat());

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRole.setNeRoleId(nexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);

		// 用户
		NexusServerUser nexusServerUser = new NexusServerUser();
		// 发布用户
		//nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId(), null);
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId(), null);

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
		nexusUserRepository.insertSelective(nexusUser);

		// 创建用户权限
		List<NexusAuth> nexusAuthList = nexusAuthService.createNexusAuth(Collections.singletonList(DetailsHelper.getUserDetails().getUserId()), nexusRepository.getRepositoryId(), NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
		nexusRepoCreateDTO.setNexusAuthList(nexusAuthList);

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
	public NexusRepositoryCreateDTO updateRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {

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
	public void deleteRepo(Long organizationId, Long projectId, Long repositoryId) {
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
		NexusUser nexusUser = nexusUserRepository.selectOne(userQuery);

		// 权限
		NexusAuth authQuery = new NexusAuth();
		authQuery.setRepositoryId(repositoryId);
		List<NexusAuth> nexusAuthList = nexusAuthRepository.select(authQuery);

		// 数据库数据删除
		nexusRepositoryRepository.deleteByPrimaryKey(nexusRepository);
		nexusRoleRepository.deleteByPrimaryKey(nexusRole);
		nexusUserRepository.deleteByPrimaryKey(nexusUser);
		nexusAuthRepository.batchDeleteByPrimaryKey(nexusAuthList);


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

		NexusPush nexusPush = new NexusPush();
		nexusPush.setType("MAVEN");
		List<NexusPush> nexusPushList = nexusPushRepository.select(nexusPush);

		List<String> pushRepoList = nexusClient.getNexusUserApi().validPush(nexusRepositoryRelatedDTO.getRepositoryList(), nexusRepositoryRelatedDTO.getUserName(),
				nexusPushList.stream().map(NexusPush::getRule).collect(Collectors.toList()));

		List<String> remainderRepo = new ArrayList<>(nexusRepositoryRelatedDTO.getRepositoryList());
		remainderRepo.removeAll(pushRepoList);
		if (CollectionUtils.isNotEmpty(remainderRepo)) {
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
		nexusRepository.setRepoType(NexusConstants.RepoType.MAVEN);
		nexusRepositoryRepository.insertSelective(nexusRepository);

		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(repositoryName, null, NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT);

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);

		// 用户
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(repositoryName, pullNexusServerRole.getId(), null);

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		// nexusUser.setNeUserId(nexusRepositoryRelatedDTO.getUserName());
		// nexusUser.setNeUserPassword(DESEncryptUtil.encode(nexusRepositoryRelatedDTO.getPassword()));
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(DESEncryptUtil.encode(pullNexusServerUser.getPassword()));
		// nexusUser.setIsDefault(1);
		nexusUserRepository.insertSelective(nexusUser);

		// TODO 关联仓库

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
	public Page<NexusRepositoryDTO> listRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO, String queryData) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusRepositoryDTO> resultAll = this.queryRepo(queryDTO, queryData, this.convertRepoTypeToFormat(queryDTO.getRepoType()));
		if (CollectionUtils.isEmpty(resultAll)) {
			return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
		}
		// remove配置信息
		nexusClient.removeNexusServerInfo();

		return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
	}

	@Override
	public List<NexusRepositoryDTO> listRepoAll(NexusRepositoryQueryDTO queryDTO, String queryData) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusRepositoryDTO> resultAll = this.queryRepo(queryDTO, queryData, this.convertRepoTypeToFormat(queryDTO.getRepoType()));
		// remove配置信息
		nexusClient.removeNexusServerInfo();
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
		if (queryDTO.getRepoType() != null) {
			query.setRepoType(queryDTO.getRepoType());
		}
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));
		this.setUserInfo(nexusRepositoryList);
		nexusServerRepositoryList.forEach(serverRepository -> {
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(nexusRepositoryMap.get(serverRepository.getName()), serverRepository);
			resultAll.add(nexusRepositoryDTO);
		});

	}

	/**
	 * 设置用户信息
	 * @param nexusRepositoryList 仓库列表
	 */
	private void setUserInfo(List<NexusRepository> nexusRepositoryList){
		//创建人ID去重，并获得创建人详细信息
		Set<Long> userIdSet = nexusRepositoryList.stream().map(AuditDomain::getCreatedBy).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(userIdSet)) {
			List<UserDTO> userDTOList = baseServiceFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]),true);
			Map<Long,UserDTO> userDtoMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId,dto->dto));
			if (CollectionUtils.isNotEmpty(userDTOList)) {
				nexusRepositoryList.forEach(repository -> {
					//设置创建人登录名、真实名称、创建人头像
					UserDTO userDTO = userDtoMap.get(repository.getCreatedBy());
					if(userDTO != null){
						repository.setCreatorImageUrl(userDTO.getImageUrl());
						repository.setCreatorLoginName(userDTO.getLoginName());
						repository.setCreatorRealName(userDTO.getRealName());
					}
				});
			}
		}
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
		Condition.Builder builder = Condition.builder(NexusRepository.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRepository.FIELD_ORGANIZATION_ID, queryDTO.getOrganizationId()));
		if (queryDTO.getRepoType() != null) {
			builder.where(Sqls.custom()
					.andEqualTo(NexusRepository.FIELD_REPO_TYPE, queryDTO.getRepoType()));
		}
		Condition condition = builder.build();
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
		if (queryDTO.getRepoType() != null) {
			query.setRepoType(queryDTO.getRepoType());
		}
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));
		// 设置用户信息
		this.setUserInfo(nexusRepositoryList);
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
		Condition.Builder builder = Condition.builder(NexusRepository.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRepository.FIELD_PROJECT_ID, queryDTO.getProjectId()));
		if (queryDTO.getRepoType() != null) {
			builder.where(Sqls.custom()
					.andEqualTo(NexusRepository.FIELD_REPO_TYPE, queryDTO.getRepoType()));
		}
		Condition condition = builder.build();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(condition);

		this.mavenRepoConvert(resultAll, nexusRepositoryList, nexusServerRepositoryMap);


	}
	private void mavenRepoConvert(List<NexusRepositoryDTO> resultAll,
								  List<NexusRepository> nexusRepositoryList,
								  Map<String, NexusServerRepository> nexusServerRepositoryMap){
		// 设置用户信息
		this.setUserInfo(nexusRepositoryList);
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
	public List<NexusRepositoryDTO> listRepoNameAll(Long projectId, Boolean excludeRelated, String repoType) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// 所有nexus服务仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		// 所有项目仓库数据
		List<String> repositoryNameList = new ArrayList<>();
		if (excludeRelated) {
			repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(null, repoType);
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
	public List<NexusRepositoryDTO> listRepoNameByProjectId(Long projectId, String repoType) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// nexus服务，仓库数据
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(this.convertRepoTypeToFormat(repoType));
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		List<String> serverRepositoryNameList = nexusServerRepositoryList.stream().map(NexusServerRepository::getName).collect(Collectors.toList());

		// 当前项目仓库数据
		List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId, repoType);
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
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT);
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));


		// 当前项目仓库数据
		List<String> repositoryNameList = nexusRepositoryRepository.getRepositoryByProject(projectId, NexusConstants.RepoType.MAVEN);
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

	/*@Override
	public List<NexusRepositoryDTO> listRepoPush(Long projectId, List<String> currentRepoName) {
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
		repositoryNameList.removeAll(currentRepoName);
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
	}*/

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

		// 返回信息
		NexusGuideDTO nexusGuideDTO = new NexusGuideDTO();
		// 设置拉取配置信息
		nexusGuideDTO.handlePullGuideValue(nexusServerRepository, nexusRepository, nexusUser);
		// 设置发布配置信息
		nexusGuideDTO.handlePushGuideValue(nexusServerRepository, nexusRepository, nexusUser, showPushFlag);
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusGuideDTO;
	}

	@Override
	public Page<NexusRepositoryDTO> listNpmRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO, String queryData) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusRepositoryDTO> resultAll = this.queryRepo(queryDTO, queryData, NexusApiConstants.NexusRepoFormat.NPM_FORMAT);
		if (CollectionUtils.isEmpty(resultAll)) {
			return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
		}
		// remove配置信息
		nexusClient.removeNexusServerInfo();

		return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), resultAll);
	}

	private List<NexusRepositoryDTO> queryRepo(NexusRepositoryQueryDTO queryDTO, String queryData, String nexusRepoFormat) {
		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(nexusRepoFormat);
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
				nexusRepositoryDTO.setProjectImgUrl(projectVO.getImageUrl());
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
	 * 制品库类型，转换为nexus format
	 * @return
	 */
	private String convertRepoTypeToFormat(String repoType) {
		if (NexusConstants.RepoType.MAVEN.equals(repoType)) {
			return NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT;
		} else if (NexusConstants.RepoType.NPM.equals(repoType)) {
			return NexusApiConstants.NexusRepoFormat.NPM_FORMAT;
		} else {
			return null;
		}
	}
}
