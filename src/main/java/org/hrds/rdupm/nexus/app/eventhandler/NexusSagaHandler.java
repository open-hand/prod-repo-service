package org.hrds.rdupm.nexus.app.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
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


	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_REPO,
			description = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_DEC_REPO,
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			seq = NexusSagaConstants.NexusMavenRepoCreate.REPO_SEQ)
	public NexusRepository createMavenRepoSaga(String message) throws IOException {
		NexusRepositoryCreateDTO nexusRepoCreateDTO = objectMapper.readValue(message, NexusRepositoryCreateDTO.class);

		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		NexusRepository query = new NexusRepository();
		query.setNeRepositoryName(nexusRepoCreateDTO.getName());
		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		if (nexusRepository == null) {
			throw new CommonException("nexus repository is not create, repoName is " + nexusRepoCreateDTO.getName());
		}

		if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())){
			return nexusRepository;
		}

		// 创建仓库
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
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_ROLE,
			description = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_DEC_ROLE,
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			seq = NexusSagaConstants.NexusMavenRepoCreate.ROLE_SEQ)
	public NexusRepository createMavenRepoRoleSaga(String message) throws IOException {
		NexusRepository nexusRepository = objectMapper.readValue(message, NexusRepository.class);

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
		// 发布角色
		NexusServerRole nexusServerRole = new NexusServerRole();
		nexusServerRole.createDefPushRole(nexusRepository.getNeRepositoryName(), true, nexusRole.getNeRoleId());
		// 拉取角色
		NexusServerRole pullNexusServerRole = new NexusServerRole();
		pullNexusServerRole.createDefPullRole(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId());

		// 创建角色
		nexusClient.getNexusRoleApi().createRole(nexusServerRole);
		nexusClient.getNexusRoleApi().createRole(pullNexusServerRole);

		// 匿名访问
		if (nexusRepository.getAllowAnonymous() == 1) {
			// 允许匿名
			NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
			anonymousRole.setPullPri(nexusRepository.getNeRepositoryName(), 1);
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

	@SagaTask(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_USER,
			description = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE_DEC_USER,
			sagaCode = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			seq = NexusSagaConstants.NexusMavenRepoCreate.USER_SEQ)
	public NexusRepository createMavenRepoUserSaga(String message) throws IOException {
		NexusRepository nexusRepository = objectMapper.readValue(message, NexusRepository.class);

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

		Condition userCondition = Condition.builder(NexusUser.class)
				.where(Sqls.custom()
						.andEqualTo(NexusUser.FIELD_REPOSITORY_ID, nexusRepository.getRepositoryId())
						.andEqualTo(NexusUser.FIELD_IS_DEFAULT, 1))
				.build();
		List<NexusUser> userList = nexusUserRepository.selectByCondition(userCondition);
		if (CollectionUtils.isEmpty(userList)) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		NexusUser nexusUser = userList.get(0);

		// 用户
		// 发布用户
		NexusServerUser nexusServerUser = new NexusServerUser();
		nexusServerUser.createDefPushUser(nexusRepository.getNeRepositoryName(), nexusRole.getNeRoleId(), null);
		// 拉取用户
		NexusServerUser pullNexusServerUser = new NexusServerUser();
		pullNexusServerUser.createDefPullUser(nexusRepository.getNeRepositoryName(), nexusRole.getNePullRoleId(), null);

		// 创建用户
		nexusClient.getNexusUserApi().createUser(nexusServerUser);
		nexusClient.getNexusUserApi().createUser(pullNexusServerUser);


		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return nexusRepository;
	}

}
