package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.init.config.NexusDefaultInitConfiguration;
import org.hrds.rdupm.init.service.NexusDefaultInitTask;
import org.hrds.rdupm.nexus.app.service.NexusInitService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 制品库_nexus仓库 初始化
 * @author weisen.yang@hand-china.com 2020/4/7
 */
@Component
public class NexusInitServiceImpl implements NexusInitService {
	private static final Logger logger = LoggerFactory.getLogger(NexusInitServiceImpl.class);
	private final static String SERVER_NAME = "默认服务";
	@Autowired
	private NexusClient nexusClient;
	@Autowired
	private NexusServerConfigService configService;
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;
	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;
	@Autowired
	private NexusDefaultInitConfiguration initConfiguration;

	@Override
	public void initDefaultNexusServer() {
		logger.info("Nexus默认服务初始化，数据插入、更新");
		NexusServerConfig queryConfig = new NexusServerConfig();
		queryConfig.setDefaultFlag(1);
		NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(queryConfig);
		if (defaultConfig == null) {
			// 数据库无数据
			NexusServerConfig nexusServerConfig = new NexusServerConfig();
			nexusServerConfig.setServerName(SERVER_NAME);
			nexusServerConfig.setServerUrl(initConfiguration.getServerUrl());
			nexusServerConfig.setUserName(initConfiguration.getUsername());
			nexusServerConfig.setPassword(DESEncryptUtil.encode(initConfiguration.getPassword()));
			nexusServerConfig.setEnableAnonymousFlag(initConfiguration.getEnableAnonymousFlag());
			if (initConfiguration.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
				nexusServerConfig.setAnonymous(initConfiguration.getAnonymousUser());
				nexusServerConfig.setAnonymousRole(initConfiguration.getAnonymousRole());
			}
			nexusServerConfig.setDefaultFlag(BaseConstants.Flag.YES);
			nexusServerConfigRepository.insertSelective(nexusServerConfig);
		} else {
			// 数据库有数据
			defaultConfig.setServerUrl(initConfiguration.getServerUrl());
			defaultConfig.setUserName(initConfiguration.getUsername());
			defaultConfig.setPassword(DESEncryptUtil.encode(initConfiguration.getPassword()));
			defaultConfig.setEnableAnonymousFlag(initConfiguration.getEnableAnonymousFlag());
			if (initConfiguration.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)) {
				defaultConfig.setAnonymous(initConfiguration.getAnonymousUser());
				defaultConfig.setAnonymousRole(initConfiguration.getAnonymousRole());
			} else {
				defaultConfig.setAnonymous(null);
				defaultConfig.setAnonymousRole(null);
			}
			nexusServerConfigRepository.updateOptional(defaultConfig, NexusServerConfig.FIELD_SERVER_URL,
					NexusServerConfig.FIELD_USER_NAME, NexusServerConfig.FIELD_PASSWORD,
					NexusServerConfig.FIELD_ENABLE_ANONYMOUS_FLAG, NexusServerConfig.FIELD_ANONYMOUS,
					NexusServerConfig.FIELD_ANONYMOUS_ROLE);
		}
	}

	@Override
	public List<NexusServerConfig> initScript() {
		List<NexusServerConfig> errorList = new ArrayList<>();
		try {
			List<NexusServerConfig> serverConfigList = nexusServerConfigRepository.selectAll();
			serverConfigList.forEach(nexusServerConfig -> {
				try {
					// 设置并返回当前nexus服务信息
					configService.setNexusInfoByConfigId(nexusClient, nexusServerConfig.getConfigId());
					nexusClient.initData();
				} catch (Exception e) {
					logger.error("脚本初始化失败", e);
					errorList.add(nexusServerConfig);
				}
			});
		} finally {
			// remove配置信息
			nexusClient.removeNexusServerInfo();
		}
		return errorList;
	}

	@Override
	public void initAnonymous() {
		try {
			NexusServerConfig queryConfig = new NexusServerConfig();
			queryConfig.setDefaultFlag(1);
			NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(queryConfig);
			configService.setNexusInfoByConfigId(nexusClient, defaultConfig.getConfigId());

			List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(null);
			Map<String, String> map = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, NexusServerRepository::getFormat));

			if (!BaseConstants.Flag.YES.equals(defaultConfig.getEnableAnonymousFlag())) {
				return;
			}

			NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(defaultConfig.getAnonymousRole());
			if (anonymousRole == null) {
				throw new CommonException("default anonymous role not found: " + defaultConfig.getAnonymousRole());
			}

			Condition condition = Condition.builder(NexusRepository.class)
					.where(Sqls.custom()
							.andEqualTo(NexusRepository.FIELD_ALLOW_ANONYMOUS, 1)
							.andEqualTo(NexusRepository.FIELD_CONFIG_ID, defaultConfig.getConfigId()))
					.build();
			List<NexusRepository> repositoryList = nexusRepositoryRepository.selectByCondition(condition);
			List<String> repositoryNames = repositoryList.stream().map(NexusRepository::getNeRepositoryName).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(repositoryNames)) {
				repositoryNames.forEach(repositoryName -> {
					String format = map.get(repositoryName);
					if (format != null) {
						anonymousRole.setPullPri(repositoryName, 1, format);
					}
				});
				nexusClient.getNexusRoleApi().updateRole(anonymousRole);
			}
		} catch (Exception e) {
			logger.error("匿名初始化失败", e);
		} finally {
			nexusClient.removeNexusServerInfo();
		}
	}
}
