package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
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
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
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
	@Autowired
	private NexusClient nexusClient;
	@Autowired
	private NexusServerConfigService configService;
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;
	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;

	@Override
	public void initScript() {
		try {
			List<NexusServerConfig> serverConfigList = nexusServerConfigRepository.selectAll();
			serverConfigList.forEach(nexusServerConfig -> {
				// 设置并返回当前nexus服务信息
				configService.setNexusInfoByConfigId(nexusClient, nexusServerConfig.getConfigId());
				nexusClient.getNexusScriptApi().initScript();
			});
		} finally {
			// remove配置信息
			nexusClient.removeNexusServerInfo();
		}
	}

	@Override
	public void initAnonymous(List<String> repositoryNames) {
		NexusServerConfig queryConfig = new NexusServerConfig();
		queryConfig.setDefaultFlag(1);
		NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(queryConfig);
		configService.setNexusInfoByConfigId(nexusClient, defaultConfig.getConfigId());

		List<NexusServerRepository> nexusServerRepositoryList = nexusClient.getRepositoryApi().getRepository(null);
		Map<String, String> map = nexusServerRepositoryList.stream().collect(Collectors.toMap(NexusServerRepository::getName, NexusServerRepository::getFormat));

		if (CollectionUtils.isEmpty(repositoryNames)) {
			repositoryNames = nexusServerRepositoryList.stream().map(NexusServerRepository::getName).collect(Collectors.toList());
		}

		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(defaultConfig.getAnonymousRole());
		if (anonymousRole == null) {
			throw new CommonException("default anonymous role not found: " + defaultConfig.getAnonymousRole());
		}

		Condition condition = Condition.builder(NexusRepository.class)
				.where(Sqls.custom()
						.andEqualTo(NexusRepository.FIELD_ALLOW_ANONYMOUS, 0))
				.build();
		List<NexusRepository> repositoryList = nexusRepositoryRepository.selectByCondition(condition);
		// Map<String, String> map = repositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, NexusRepository::getRepoType));
		repositoryNames.removeAll(repositoryList.stream().map(NexusRepository::getNeRepositoryName).collect(Collectors.toList()));
		if (CollectionUtils.isNotEmpty(repositoryNames)) {
			repositoryNames.forEach(repositoryName -> {
				String format = map.get(repositoryName);
				anonymousRole.setPullPri(repositoryName, 1, format);
			});
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		nexusClient.removeNexusServerInfo();
	}
}
