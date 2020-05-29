package org.hrds.rdupm.nexus.app.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.eventhandler.payload.NexusRepositoryDeletePayload;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.*;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/4/8
 */
@Component
public class NexusSagaHandler {
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
	private ObjectMapper objectMapper;
	@Autowired
	private NexusAuthHandler nexusAuthHandler;
	@Autowired
	private NexusRepositoryService nexusRepositoryService;


	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_REPO,
			description = "创建仓库: 创建nexus server仓库",
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			maxRetryCount = 3,
			seq = NexusSagaConstants.NexusMavenRepoCreate.REPO_SEQ)
	public NexusRepository createRepoSaga(String message) {
		NexusRepositoryCreateDTO nexusRepoCreateDTO = null;
		try {
			nexusRepoCreateDTO = objectMapper.readValue(message, NexusRepositoryCreateDTO.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}

		configService.setNexusInfo(nexusClient);

		NexusRepository query = new NexusRepository();
		query.setNeRepositoryName(nexusRepoCreateDTO.getName());
		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		if (nexusRepository == null) {
			throw new CommonException("nexus repository is not create, repoName is " + nexusRepoCreateDTO.getName());
		}
		nexusRepository.setNexusAuthList(nexusRepoCreateDTO.getNexusAuthList());

		if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())){
			return nexusRepository;
		}

		// 创建仓库
		if (nexusRepository.getRepoType().equals(NexusConstants.RepoType.MAVEN)) {
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
		}

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_ROLE,
			description = "创建仓库：创建角色",
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			maxRetryCount = 3,
			seq = NexusSagaConstants.NexusMavenRepoCreate.ROLE_SEQ)
	public NexusRepository createRepoRoleSaga(String message) {
		NexusRepository nexusRepository = null;
		try {
			nexusRepository = objectMapper.readValue(message, NexusRepository.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}

		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		NexusRepository exist = nexusRepositoryRepository.selectByPrimaryKey(nexusRepository);
		if (exist == null) {
			throw new CommonException("nexus repository is not create, repoName is " + nexusRepository.getNeRepositoryName());
		}

		Condition roleCondition = Condition.builder(NexusRole.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRole.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()))
				.build();
		List<NexusRole> roleList = nexusRoleRepository.selectByCondition(roleCondition);
		if (CollectionUtils.isEmpty(roleList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusRole nexusRole = roleList.get(0);

		// 角色
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId(), nexusRepositoryService.convertRepoTypeToFormat(exist.getRepoType()));

		// 发布角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		nexusServerRole.createDefPushRole(nexusRepository.getNeRepositoryName(), true, nexusRole.getNeRoleId(), nexusRepositoryService.convertRepoTypeToFormat(exist.getRepoType()));

		// 创建拉取角色
		NexusServerRole pullExist = nexusClient.getNexusRoleApi().getRoleById(pullNexusServerRole.getId());
		if (pullExist == null) {
			// 角色不存在，创建
			nexusClient.getNexusRoleApi().createRole(pullNexusServerRole);
		}
		// 创建发布角色
		NexusServerRole pushExist = nexusClient.getNexusRoleApi().getRoleById(nexusServerRole.getId());
		if (pushExist == null) {
			// 角色不存在，创建
			nexusClient.getNexusRoleApi().createRole(nexusServerRole);
		}

		// 匿名访问
		if (nexusRepository.getAllowAnonymous() == 1) {
			// 允许匿名
			NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
			if (anonymousRole == null) {
				throw new CommonException("default anonymous role not found:" + serverConfig.getAnonymousRole());
			}
			anonymousRole.setPullPri(nexusRepository.getNeRepositoryName(), 1, nexusRepositoryService.convertRepoTypeToFormat(exist.getRepoType()));
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_USER,
			description = "创建仓库：创建用户",
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			maxRetryCount = 3,
			seq = NexusSagaConstants.NexusMavenRepoCreate.USER_SEQ)
	public NexusRepository createRepoUserSaga(String message) {
		NexusRepository nexusRepository = null;
		try {
			nexusRepository = objectMapper.readValue(message, NexusRepository.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}

		configService.setNexusInfo(nexusClient);

		NexusRepository exist = nexusRepositoryRepository.selectByPrimaryKey(nexusRepository);
		if (exist == null) {
			throw new CommonException("nexus repository is not create, repoName is " + nexusRepository.getNeRepositoryName());
		}

		Condition roleCondition = Condition.builder(NexusRole.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRole.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()))
				.build();
		List<NexusRole> roleList = nexusRoleRepository.selectByCondition(roleCondition);
		if (CollectionUtils.isEmpty(roleList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusRole nexusRole = roleList.get(0);

		Condition userCondition = Condition.builder(NexusUser.class)
				.where(Sqls.custom()
						.andEqualTo(NexusUser.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()))
				.build();
		List<NexusUser> userList = nexusUserRepository.selectByCondition(userCondition);
		if (CollectionUtils.isEmpty(userList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusUser nexusUser = userList.get(0);

		// 用户
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId(), nexusUser.getNePullUserId());
		pullNexusServerUser.setPassword(DESEncryptUtil.decode(nexusUser.getNePullUserPassword()));

		// 创建用户
		List<NexusServerUser> pullExistUserList = nexusClient.getNexusUserApi().getUsers(pullNexusServerUser.getUserId());
		if (CollectionUtils.isEmpty(pullExistUserList)) {
			nexusClient.getNexusUserApi().createUser(pullNexusServerUser);
		}

		// 创建用户权限
		nexusAuthHandler.createUserAuth(nexusRepository.getNexusAuthList());

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE_REPO,
			description = "更新仓库",
			sagaCode = NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE,
			maxRetryCount = 3,
			seq = 1)
	public String updateRepoSaga(String message) {
		NexusRepositoryCreateDTO nexusRepoCreateDTO = null;
		try {
			nexusRepoCreateDTO = objectMapper.readValue(message, NexusRepositoryCreateDTO.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		// 创建更新
		if (nexusRepoCreateDTO.getRepoType().equals(NexusConstants.RepoType.MAVEN)) {
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
		}



		// 匿名访问
		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
		if (anonymousRole == null) {
			throw new CommonException("default anonymous role not found:" + serverConfig.getAnonymousRole());
		}
		anonymousRole.setPullPri(nexusRepoCreateDTO.getName(), nexusRepoCreateDTO.getAllowAnonymous(), nexusRepositoryService.convertRepoTypeToFormat(nexusRepoCreateDTO.getRepoType()));
		nexusClient.getNexusRoleApi().updateRole(anonymousRole);

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return message;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE_REPO,
			description = "删除仓库",
			sagaCode = NexusSagaConstants.NexusMavenRepoDelete.MAVEN_REPO_DELETE,
			maxRetryCount = 3,
			seq = 1)
	public String deleteRepoSaga(String message) {

		NexusRepositoryDeletePayload deletePayload = null;
		try {
			deletePayload = objectMapper.readValue(message, NexusRepositoryDeletePayload.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		NexusRepository nexusRepository = deletePayload.getNexusRepository();
		NexusRole nexusRole = deletePayload.getNexusRole();
		NexusUser nexusUser = deletePayload.getNexusUser();
		List<NexusAuth> nexusAuthList = deletePayload.getNexusAuthList();

		// nexus数据删除
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		// 仓库
		nexusClient.getRepositoryApi().deleteRepository(nexusRepository.getNeRepositoryName());
		// 默认角色
		if (nexusRole.getNePullRoleId() != null) {
			nexusClient.getNexusRoleApi().deleteRole(nexusRole.getNePullRoleId());
		}
		// 默认用户
		if (nexusUser.getNePullUserId() != null) {
			nexusClient.getNexusUserApi().deleteUser(nexusUser.getNePullUserId());
		}

		nexusClient.removeNexusServerInfo();
		return message;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED_REPO,
			description = "关联仓库",
			sagaCode = NexusSagaConstants.NexusMavenRepoRelated.MAVEN_REPO_RELATED,
			maxRetryCount = 3,
			seq = 1)
	public String relatedRepoSaga(String message) {
		// TODO 关联仓库
		NexusRepository nexusRepository = null;
		try {
			nexusRepository = objectMapper.readValue(message, NexusRepository.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}

		// 设置并返回当前nexus服务信息
		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		NexusRepository exist = nexusRepositoryRepository.selectByPrimaryKey(nexusRepository);
		if (exist == null) {
			throw new CommonException("nexus repository is not create, repoName is " + nexusRepository.getNeRepositoryName());
		}

		// 1. 角色
		Condition roleCondition = Condition.builder(NexusRole.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRole.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()))
				.build();
		List<NexusRole> roleList = nexusRoleRepository.selectByCondition(roleCondition);
		if (CollectionUtils.isEmpty(roleList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusRole nexusRole = roleList.get(0);

		// 发布角色
		//NexusServerRole nexusServerRole = new NexusServerRole();
		//nexusServerRole.createDefPushRole(nexusRepository.getNeRepositoryName(), true, nexusRole.getNeRoleId());
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId(), nexusRepositoryService.convertRepoTypeToFormat(exist.getRepoType()));

		// 创建角色
		/*NexusServerRole pushExist = nexusClient.getNexusRoleApi().getRoleById(nexusServerRole.getId());
		if (pushExist == null) {
			// 角色不存在，创建
			nexusClient.getNexusRoleApi().createRole(nexusServerRole);
		}*/
		NexusServerRole pullExist = nexusClient.getNexusRoleApi().getRoleById(pullNexusServerRole.getId());
		if (pullExist == null) {
			// 角色不存在，创建
			nexusClient.getNexusRoleApi().createRole(pullNexusServerRole);
		}

		// 2. 用户
		Condition userCondition = Condition.builder(NexusUser.class)
				.where(Sqls.custom()
						.andEqualTo(NexusUser.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId()))
				.build();
		List<NexusUser> userList = nexusUserRepository.selectByCondition(userCondition);
		if (CollectionUtils.isEmpty(userList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusUser nexusUser = userList.get(0);

		// 发布用户
		// 为发布用户赋予发布角色
		/*List<NexusServerUser> nexusServerUserList = nexusClient.getNexusUserApi().getUsers(nexusUser.getNeUserId());
		if (CollectionUtils.isNotEmpty(nexusServerUserList)) {
			NexusServerUser nexusServerUser = nexusServerUserList.get(0);
			nexusServerUser.getRoles().add(nexusServerRole.getId());
			nexusClient.getNexusUserApi().updateUser(nexusServerUser);
		} else {
			throw new CommonException(NexusMessageConstants.NEXUS_REPO_RELATED_EFAULT_USER_IS_NULL, nexusRepository.getNeRepositoryName());
		}*/

		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId(), nexusUser.getNePullUserId());
		pullNexusServerUser.setPassword(DESEncryptUtil.decode(nexusUser.getNePullUserPassword()));

		List<NexusServerUser> pullExistUserList = nexusClient.getNexusUserApi().getUsers(pullNexusServerUser.getUserId());
		if (CollectionUtils.isEmpty(pullExistUserList)) {
			nexusClient.getNexusUserApi().createUser(pullNexusServerUser);
		}

		// 3. 默认允许匿名
		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
		if (anonymousRole == null) {
			throw new CommonException("default anonymous role not found:" + serverConfig.getAnonymousRole());
		}
		anonymousRole.setPullPri(nexusRepository.getNeRepositoryName(), 1,  nexusRepositoryService.convertRepoTypeToFormat(exist.getRepoType()));
		nexusClient.getNexusRoleApi().updateRole(anonymousRole);

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return message;
	}

	@SagaTask(code = NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE_ROLE,
			description = "平台层-仓库分配：创建角色",
			sagaCode = NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE,
			maxRetryCount = 3,
			seq = 1)
	public NexusRepository repoDistributeRoleSaga(String message) {
		return this.createRepoRoleSaga(message);
	}

	@SagaTask(code = NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE_USER,
			description = "平台层-仓库分配：创建用户",
			sagaCode = NexusSagaConstants.NexusRepoDistribute.SITE_NEXUS_REPO_DISTRIBUTE,
			maxRetryCount = 3,
			seq = 2)
	public NexusRepository repoDistributeUserSaga(String message) {
		return this.createRepoUserSaga(message);
	}

}
