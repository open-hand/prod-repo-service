package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusProjectService;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusProjectServiceRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 制品库_nexus服务信息配置表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Service
public class NexusServerConfigServiceImpl implements NexusServerConfigService {
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;

	@Autowired
	private ProdUserRepository prodUserRepository;
	@Autowired
	private NexusProjectServiceRepository nexusProjectServiceRepository;
	@Autowired
	private NexusClient nexusClient;

	@Override
	public NexusServerConfig setNexusInfo(NexusClient nexusClient, Long projectId) {
		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryEnableServiceConfig(projectId);
		if (nexusServerConfig == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
		}
		NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
				nexusServerConfig.getUserName(),
				DESEncryptUtil.decode(nexusServerConfig.getPassword()));
		nexusClient.setNexusServerInfo(nexusServer);
		return nexusServerConfig;
	}

	@Override
	public NexusServerConfig setNexusDefaultInfo(NexusClient nexusClient) {
		NexusServerConfig queryConfig = new NexusServerConfig();
		queryConfig.setDefaultFlag(BaseConstants.Flag.YES);
		NexusServerConfig defaultInfo = nexusServerConfigRepository.selectOne(queryConfig);
		NexusServer nexusServer = new NexusServer(defaultInfo.getServerUrl(),
				defaultInfo.getUserName(),
				DESEncryptUtil.decode(defaultInfo.getPassword()));
		nexusClient.setNexusServerInfo(nexusServer);
		return defaultInfo;
	}

	@Override
	public NexusServerConfig setNexusInfoByConfigId(NexusClient nexusClient, Long configId) {
		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectByPrimaryKey(configId);
		if (nexusServerConfig == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
		}
		NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
				nexusServerConfig.getUserName(),
				DESEncryptUtil.decode(nexusServerConfig.getPassword()));
		nexusClient.setNexusServerInfo(nexusServer);
		return nexusServerConfig;
	}

	@Override
	public NexusServerConfig setNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId) {
		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryServiceConfigByRepositoryId(repositoryId);
		if (nexusServerConfig == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
		}
		NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
				nexusServerConfig.getUserName(),
				DESEncryptUtil.decode(nexusServerConfig.getPassword()));
		nexusClient.setNexusServerInfo(nexusServer);
		return nexusServerConfig;
	}

	@Override
	public NexusServerConfig setCurrentNexusInfoByRepositoryId(NexusClient nexusClient, Long repositoryId) {
		String userName = DetailsHelper.getUserDetails().getUsername();
		ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_LOGIN_NAME, userName).stream().findFirst().orElse(null);
		if (prodUser == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		String password = null;
		if (prodUser.getPwdUpdateFlag() == 1) {
			password = DESEncryptUtil.decode(prodUser.getPassword());
		} else {
			password = prodUser.getPassword();
		}

		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.queryServiceConfigByRepositoryId(repositoryId);
		if (nexusServerConfig == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SERVER_INFO_NOT_CONFIG);
		}
		NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
				prodUser.getLoginName(),
				password);
		nexusClient.setNexusServerInfo(nexusServer);
		return nexusServerConfig;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NexusServerConfig createServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {

		// 参数校验
		nexusServerConfig.validParam(nexusClient);

		nexusServerConfig.setDefaultFlag(BaseConstants.Flag.NO);
		nexusServerConfig.setTenantId(organizationId);
		nexusServerConfig.setPassword(DESEncryptUtil.encode(nexusServerConfig.getPassword()));
		nexusServerConfigRepository.insertSelective(nexusServerConfig);

		NexusProjectService nexusProjectService = new NexusProjectService();
		nexusProjectService.setConfigId(nexusServerConfig.getConfigId());
		nexusProjectService.setOrganizationId(organizationId);
		nexusProjectService.setProjectId(projectId);
		nexusProjectService.setEnableFlag(BaseConstants.Flag.NO);
		nexusProjectServiceRepository.insertSelective(nexusProjectService);

		nexusServerConfig.setProjectServiceId(nexusProjectService.getProjectServiceId());
		nexusServerConfig.setProjectId(nexusProjectService.getProjectId());
		return nexusServerConfig;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NexusServerConfig updateServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {
		return null;
	}


	@Override
	public NexusServerConfig updatePwd(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {
		NexusServerConfig existConfig = nexusServerConfigRepository.queryServiceConfig(nexusServerConfig.getConfigId(), projectId);
		if (existConfig == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		if (!DESEncryptUtil.decode(existConfig.getPassword()).equals(nexusServerConfig.getOldPassword())) {
			throw new CommonException(NexusMessageConstants.NEXUS_OLD_PASSWORD_ERROR);
		}

		String newPassword = nexusServerConfig.getPassword();

		// 新密码校验
		NexusServer nexusServer = new NexusServer(existConfig.getServerUrl(), existConfig.getUserName(), newPassword);
		nexusClient.setNexusServerInfo(nexusServer);
		List<NexusServerUser> nexusExistUser = null;
		try {
			nexusExistUser = nexusClient.getNexusUserApi().getUsers(existConfig.getUserName());
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				throw new CommonException(NexusMessageConstants.NEXUS_NEW_PASSWORD_ERROR);
			}
			if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
				throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_PERMISSIONS);
			}
			throw e;
		}

		// 数据库更新密码
		String encryptPassword = DESEncryptUtil.encode(newPassword);
		existConfig.setPassword(encryptPassword);
		nexusServerConfigRepository.updateOptional(existConfig, NexusServerConfig.FIELD_PASSWORD);
		return nexusServerConfig;
	}

	@Override
	public List<NexusServerConfig> listServerConfig(Long organizationId, Long projectId) {
		// 默认nexus服务信息查询
		NexusServerConfig defaultQuery = new NexusServerConfig();
		defaultQuery.setDefaultFlag(BaseConstants.Flag.YES);
		NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(defaultQuery);
		defaultConfig.setProjectId(projectId);

		// 项目下，自定义的nexus服务信息
		List<NexusServerConfig> nexusServerConfigList = nexusServerConfigRepository.queryList(organizationId, projectId);

		Integer enableFlag = nexusServerConfigList.stream().map(NexusServerConfig::getEnableFlag).filter(enableFlagValue -> enableFlagValue.equals(BaseConstants.Flag.YES)).findFirst().orElse(null);
		if (enableFlag == null) {
			// 没有启用的自定义的nexus服务, 设置默认的为启用
			defaultConfig.setEnableFlag(BaseConstants.Flag.YES);
		} else {
			defaultConfig.setEnableFlag(BaseConstants.Flag.NO);
		}
		List<NexusServerConfig> result = new ArrayList<>();
		result.add(defaultConfig);
		result.addAll(nexusServerConfigList);

		result = result.stream().peek(nexusServer -> nexusServer.setPassword(null)).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void enableProjectServerConfig(Long organizationId, Long projectId, NexusServerConfig nexusServerConfig) {
		NexusServerConfig existConfig = nexusServerConfigRepository.selectByPrimaryKey(nexusServerConfig.getConfigId());
		if (existConfig == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}

		if (existConfig.getDefaultFlag().equals(BaseConstants.Flag.YES)) {
			// 启用默认的服务
			// 直接更新该项目下所有服务为不启用
			// TODO 直接更新？
			nexusProjectServiceRepository.disAbleByProjectId(projectId);
		} else {
			// 启用自定义的服务
			// 将项目下的所有nexus服务都设置为不启用，启用该服务
			NexusServerConfig existConfigProject = nexusServerConfigRepository.queryServiceConfig(nexusServerConfig.getConfigId(), projectId);
			if (existConfigProject == null) {
				throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
			}

			NexusProjectService query = new NexusProjectService();
			query.setProjectServiceId(existConfigProject.getProjectServiceId());
			query.setProjectId(projectId);
			query.setOrganizationId(organizationId);
			NexusProjectService nexusProjectService = nexusProjectServiceRepository.selectOne(query);
			if (nexusProjectService == null) {
				throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
			}

			// 所有设为不启用
			nexusProjectServiceRepository.disAbleByProjectId(projectId);
			// 启用
			nexusProjectService.setEnableFlag(BaseConstants.Flag.YES);
			nexusProjectServiceRepository.updateOptional(nexusProjectService, NexusProjectService.FIELD_ENABLE_FLAG);
		}
	}
}
