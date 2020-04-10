package org.hrds.rdupm.nexus.app.service.impl;

import org.hrds.rdupm.nexus.app.service.NexusInitService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
}
