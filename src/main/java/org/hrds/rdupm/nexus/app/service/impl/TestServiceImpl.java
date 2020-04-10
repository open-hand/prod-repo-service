package org.hrds.rdupm.nexus.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.app.service.TestService;
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
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weisen.yang@hand-china.com 2020/4/8
 */
@Component
public class TestServiceImpl implements TestService {
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
	@Transactional(rollbackFor = Exception.class)
	@Saga(code = NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE,
			description = "创建maven仓库",
			inputSchemaClass = NexusRepositoryCreateDTO.class)
	public NexusRepositoryCreateDTO createMavenRepo(Long organizationId, Long projectId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {
//
//		// 步骤
//		// 1. 更新数据库数据
//		// 2. 创建仓库
//		// 3. 创建仓库默认角色，赋予权限：nx-repository-view-maven2-[仓库名]-*
//		// 4. 创建仓库默认用户，默认赋予角色，上述创建的角色
//		// 5. 是否允许匿名
//		//     允许，赋予匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
//		//     不允许，去除匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
//
//		// 参数校验
//		nexusRepoCreateDTO.validParam(baseServiceFeignClient, true);
//
//		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);
//
//
//		if (nexusClient.getRepositoryApi().repositoryExists(nexusRepoCreateDTO.getName())){
//			throw new CommonException(NexusApiConstants.ErrorMessage.REPO_NAME_EXIST);
//		}
//
//		// 1. 数据库数据更新
//		// 仓库
//		NexusRepository nexusRepository = new NexusRepository();
//		nexusRepository.setConfigId(serverConfig.getConfigId());
//		nexusRepository.setNeRepositoryName(nexusRepoCreateDTO.getName());
//		nexusRepository.setOrganizationId(organizationId);
//		nexusRepository.setProjectId(projectId);
//		nexusRepository.setAllowAnonymous(nexusRepoCreateDTO.getAllowAnonymous());
//		nexusRepositoryRepository.insertSelective(nexusRepository);
//		// 角色
//		// 发布角色
//		NexusServerRole nexusServerRole = new NexusServerRole();
//		nexusServerRole.createDefPushRole(nexusRepoCreateDTO.getName(), true, null);
//		// 拉取角色
//		NexusServerRole pullNexusServerRole = new NexusServerRole();
//		pullNexusServerRole.createDefPullRole(nexusRepoCreateDTO.getName(), null);
//
//		NexusRole nexusRole = new NexusRole();
//		nexusRole.setRepositoryId(nexusRepository.getRepositoryId());
//		nexusRole.setNeRoleId(nexusServerRole.getId());
//		nexusRole.setNePullRoleId(pullNexusServerRole.getId());
//		nexusRoleRepository.insertSelective(nexusRole);
//		// 用户
//		// 发布用户
//		NexusServerUser nexusServerUser = new NexusServerUser();
//		nexusServerUser.createDefPushUser(nexusRepoCreateDTO.getName(), nexusServerRole.getId(), null);
//		// 拉取用户
//		NexusServerUser pullNexusServerUser = new NexusServerUser();
//		pullNexusServerUser.createDefPullUser(nexusRepoCreateDTO.getName(), pullNexusServerRole.getId(), null);
//
//		NexusUser nexusUser = new NexusUser();
//		nexusUser.setRepositoryId(nexusRepository.getRepositoryId());
//		nexusUser.setNeUserId(nexusServerUser.getUserId());
//		nexusUser.setNeUserPassword(nexusServerUser.getPassword());
//		nexusUser.setNePullUserId(pullNexusServerUser.getUserId());
//		nexusUser.setNePullUserPassword(pullNexusServerUser.getPassword());
//		nexusUser.setIsDefault(1);
//		nexusUserRepository.insertSelective(nexusUser);
//
//		producer.apply(StartSagaBuilder.newBuilder()
//						.withSagaCode(NexusSagaConstants.NexusMavenRepoCreate.MAVEN_REPO_CREATE)
//						.withLevel(ResourceLevel.PROJECT)
//						.withRefType("mavenRepo")
//						.withSourceId(projectId),
//				builder -> {
//					builder.withPayloadAndSerialize(nexusRepoCreateDTO)
//							.withRefId(String.valueOf(nexusRepository.getRepositoryId()))
//							.withSourceId(projectId);
//				});
//		nexusClient.removeNexusServerInfo();
		return nexusRepoCreateDTO;
	}

	@Override
	@Saga(code = NexusSagaConstants.NexusMavenRepoUpdate.MAVEN_REPO_UPDATE,
			description = "更新maven仓库",
			inputSchemaClass = NexusRepository.class)
	public NexusRepositoryCreateDTO updateMavenRepo(Long organizationId, Long projectId, Long repositoryId, NexusRepositoryCreateDTO nexusRepoCreateDTO) {
		// 参数校验
		nexusRepoCreateDTO.validParam(baseServiceFeignClient, false);

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




		// TODO 多次调用，数据一致性
		return nexusRepoCreateDTO;
	}
}
