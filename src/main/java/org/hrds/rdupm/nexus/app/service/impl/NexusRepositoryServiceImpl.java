package org.hrds.rdupm.nexus.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryQueryDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryRelatedDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.*;
import org.hrds.rdupm.nexus.domain.repository.*;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


	@Override
	@Transactional(rollbackFor = Exception.class)
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
		nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName());
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName());

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNeRoleId(nexusServerRole.getId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);
		// 用户
		// 发布用户
		NexusServerUser nexusServerUser = new NexusServerUser();
		nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId());
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId());

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		nexusUser.setNeUserId(nexusServerUser.getUserId());
		nexusUser.setNeUserPassword(nexusServerUser.getPassword());
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(pullNexusServerUser.getPassword());
		nexusUser.setIsDefault(1);
		nexusUserRepository.insertSelective(nexusUser);


		// 2. 创建仓库
		switch (nexusRepoCreateDTO.getType()) {
			case NexusApiConstants.RepositoryType.HOSTED:
				// 创建本地仓库
				nexusClient.getRepositoryApi().createMavenRepository(nexusRepoCreateDTO.convertMavenHostedRequest());
				break;
			case NexusApiConstants.RepositoryType.PROXY:
				// 创建代理仓库
				nexusClient.getRepositoryApi().createAndUpdateMavenProxy(nexusRepoCreateDTO.convertMavenProxyRequest());
				break;
			case NexusApiConstants.RepositoryType.GROUP:
				// 创建仓库组
				nexusClient.getRepositoryApi().createAndUpdateMavenGroup(nexusRepoCreateDTO.convertMavenGroupRequest());
				break;
			default:break;
		}

		// 3. 创建默认角色
		nexusClient.getNexusRoleApi().createRole(nexusServerRole);
		nexusClient.getNexusRoleApi().createRole(pullNexusServerRole);

		// 4. 创建默认用户
		nexusClient.getNexusUserApi().createUser(nexusServerUser);
		nexusClient.getNexusUserApi().createUser(pullNexusServerUser);

		// 5. 匿名访问出来
		if (nexusRepoCreateDTO.getAllowAnonymous() == 1) {
			// 允许匿名
			NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
			anonymousRole.setPullPri(nexusRepoCreateDTO.getName(), 1);
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		// TODO 多次调用，数据一致性


		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
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


		// 创建更新
		switch (nexusRepoCreateDTO.getType()) {
			case NexusApiConstants.RepositoryType.HOSTED:
				// 创建本地仓库
				nexusClient.getRepositoryApi().updateMavenRepository(nexusRepoCreateDTO.convertMavenHostedRequest());
				break;
			case NexusApiConstants.RepositoryType.PROXY:
				// 创建代理仓库
				nexusClient.getRepositoryApi().createAndUpdateMavenProxy(nexusRepoCreateDTO.convertMavenProxyRequest());
				break;
			case NexusApiConstants.RepositoryType.GROUP:
				// 创建仓库组
				nexusClient.getRepositoryApi().createAndUpdateMavenGroup(nexusRepoCreateDTO.convertMavenGroupRequest());
				break;
			default:break;
		}


		// 匿名访问
		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
		anonymousRole.setPullPri(nexusRepoCreateDTO.getName(), nexusRepoCreateDTO.getAllowAnonymous());
		nexusClient.getNexusRoleApi().updateRole(anonymousRole);

		// TODO 多次调用，数据一致性

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepoCreateDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
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

		// nexus数据删除
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		if (nexusRepository.getIsRelated() == 0) {
			// 创建的仓库
			// 仓库
			nexusClient.getRepositoryApi().deleteRepository(nexusRepository.getNeRepositoryName());
			// 角色
			if (nexusRole.getNePullRoleId() != null) {
				nexusClient.getNexusRoleApi().deleteRole(nexusRole.getNePullRoleId());
			}
			if (nexusRole.getNeRoleId() != null) {
				nexusClient.getNexusRoleApi().deleteRole(nexusRole.getNeRoleId());
			}
			// 用户
			if (nexusUser.getNePullUserId() != null) {
				nexusClient.getNexusUserApi().deleteUser(nexusUser.getNePullUserId());
			}
			if (nexusUser.getNeUserId() != null) {
				nexusClient.getNexusUserApi().deleteUser(nexusUser.getNeUserId());
			}
		} else {
			// 关联的仓库
			// 角色
			if (nexusRole.getNePullRoleId() != null) {
				nexusClient.getNexusRoleApi().deleteRole(nexusRole.getNePullRoleId());
			}
			// 用户
			if (nexusUser.getNePullUserId() != null) {
				nexusClient.getNexusUserApi().deleteUser(nexusUser.getNePullUserId());
			}
		}
		nexusClient.removeNexusServerInfo();

		// TODO 数据一致性

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

		repositoryList.forEach(repositoryName -> {
			self().selfRelatedMavenRepo(organizationId, projectId, nexusRepositoryRelatedDTO, repositoryName, serverConfig);
		});

		nexusClient.removeNexusServerInfo();

		return nexusRepositoryRelatedDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
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

		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(repositoryName);

		NexusRole nexusRole = new NexusRole();
		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
		nexusRoleRepository.insertSelective(nexusRole);

		// 用户
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(repositoryName, pullNexusServerRole.getId());

		NexusUser nexusUser = new NexusUser();
		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
		nexusUser.setNeUserId(nexusRepositoryRelatedDTO.getUserName());
		nexusUser.setNeUserPassword(nexusRepositoryRelatedDTO.getPassword());
		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
		nexusUser.setNePullUserPassword(pullNexusServerUser.getPassword());
		nexusUser.setIsDefault(1);
		nexusUserRepository.insertSelective(nexusUser);

		// nexus服务数据
		// 创建拉取角色
		nexusClient.getNexusRoleApi().createRole(pullNexusServerRole);

		List<NexusServerUser> nexusServerUserList = nexusClient.getNexusUserApi().getUsers(nexusRepositoryRelatedDTO.getUserName());
		if (CollectionUtils.isNotEmpty(nexusServerUserList)) {
			NexusServerUser nexusServerUser = nexusServerUserList.get(0);
			nexusServerUser.getRoles().add(pullNexusServerRole.getId());
			nexusClient.getNexusUserApi().updateUser(nexusServerUser);
		}
		// 默认允许匿名
		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(nexusServerConfig.getAnonymousRole());
		anonymousRole.setPullPri(repositoryName, 1);
		nexusClient.getNexusRoleApi().updateRole(anonymousRole);

		// 创建拉取用户
		nexusClient.getNexusUserApi().createUser(pullNexusServerUser);
	}

	@Override
	public PageInfo<NexusRepositoryDTO> listMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO) {
		configService.setNexusInfo(nexusClient);

		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return PageConvertUtils.convert();
		}
		Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, a -> a, (k1, k2) -> k1));

		NexusRepository query = new NexusRepository();
		query.setProjectId(queryDTO.getProjectId());
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);

		// 数据筛选
		nexusRepositoryList = nexusRepositoryList.stream().filter(nexusRepository ->
				nexusServerRepositoryMap.keySet().contains(nexusRepository.getNeRepositoryName())).collect(Collectors.toList());


		// 查询参数
		if (queryDTO.getName() != null) {
			nexusRepositoryList = nexusRepositoryList.stream().filter(nexusRepository ->
					nexusRepository.getNeRepositoryName().contains(queryDTO.getName())).collect(Collectors.toList());
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		nexusRepositoryList.forEach(nexusRepository -> {
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(nexusRepository, nexusServerRepositoryMap.get(nexusRepository.getNeRepositoryName()));
			resultAll.add(nexusRepositoryDTO);
		});

		// remove配置信息
		nexusClient.removeNexusServerInfo();

		return PageConvertUtils.convert(pageRequest.getPage() + 1, pageRequest.getSize(), resultAll);
	}

	@Override
	public PageInfo<NexusRepositoryDTO> listOtherMavenRepo(PageRequest pageRequest, NexusRepositoryQueryDTO queryDTO) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return PageConvertUtils.convert();
		}

		// 所有项目数据
		NexusRepository query = new NexusRepository();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));

		// 当前项目数据
		List<NexusRepository> projectList = nexusRepositoryList.stream().filter(nexusRepository -> nexusRepository.getProjectId().equals(queryDTO.getProjectId())).collect(Collectors.toList());
		Map<String, NexusRepository> projectMap = projectList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));


		// 数据筛选
		nexusServerRepositoryList = nexusServerRepositoryList.stream().filter(serverRepository ->
				!projectMap.keySet().contains(serverRepository.getName())).collect(Collectors.toList());

		// 查询参数
		if (queryDTO.getName() != null) {
			nexusServerRepositoryList = nexusServerRepositoryList.stream().filter(serverRepository ->
					serverRepository.getName().contains(queryDTO.getName())).collect(Collectors.toList());
		}

		List<NexusRepositoryDTO> resultAll = new ArrayList<>();
		nexusServerRepositoryList.forEach(serverRepository -> {
			NexusRepositoryDTO nexusRepositoryDTO = new NexusRepositoryDTO();
			nexusRepositoryDTO.convert(nexusRepositoryMap.get(serverRepository.getName()), serverRepository);
			resultAll.add(nexusRepositoryDTO);
		});

		// remove配置信息
		nexusClient.removeNexusServerInfo();

		return PageConvertUtils.convert(pageRequest.getPage() + 1, pageRequest.getSize(), resultAll);
	}

	@Override
	public List<NexusServerBlobStore> listMavenRepoBlob() {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		List<NexusServerBlobStore> blobStoreList = nexusClient.getBlobStoreApi().getBlobStore();
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return blobStoreList;
	}

	@Override
	public List<NexusServerRepository> groupRepo() {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository();
		if (CollectionUtils.isEmpty(nexusServerRepositoryList)) {
			return new ArrayList<>();
		}
		// 所有项目数据
		NexusRepository query = new NexusRepository();
		List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(query);
		Map<String, NexusRepository> nexusRepositoryMap = nexusRepositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, a -> a, (k1, k2) -> k1));

		// 数据筛选
		nexusServerRepositoryList = nexusServerRepositoryList.stream().filter(serverRepository ->
				!nexusRepositoryMap.keySet().contains(serverRepository.getName())).collect(Collectors.toList());

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusServerRepositoryList;
	}
}
