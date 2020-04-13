package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.service.NexusInitService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

	@Override
	public void initScript() {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		nexusClient.getNexusScriptApi().initScript();

		// remove配置信息
		nexusClient.removeNexusServerInfo();
	}

	@Override
	public void initAnonymous(List<String> repositoryNames) {
		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);

		if (CollectionUtils.isEmpty(repositoryNames)) {
			List<NexusServerRepository> nexusServerRepositoryList =nexusClient.getRepositoryApi().getRepository();
			repositoryNames = nexusServerRepositoryList.stream().map(NexusServerRepository::getName).collect(Collectors.toList());
		}

		NexusServerRole anonymousRole = nexusClient.getNexusRoleApi().getRoleById(serverConfig.getAnonymousRole());
		if (anonymousRole == null) {
			throw new CommonException("default anonymous role not found: " + serverConfig.getAnonymousRole());
		}
		if (CollectionUtils.isNotEmpty(repositoryNames)) {
			repositoryNames.forEach(repositoryName -> {
				anonymousRole.setPullPri(repositoryName, 1);
			});
			nexusClient.getNexusRoleApi().updateRole(anonymousRole);
		}

		nexusClient.removeNexusServerInfo();
	}
}
