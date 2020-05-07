package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 制品库_nexus服务信息配置表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Service
public class NexusServerConfigServiceImpl implements NexusServerConfigService {
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;

	@Override
	public NexusServerConfig setNexusInfo(NexusClient nexusClient) {
		NexusServerConfig queryConfig = new NexusServerConfig();
		queryConfig.setEnabled(1);
		NexusServerConfig nexusServerConfig = nexusServerConfigRepository.selectOne(queryConfig);
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
	@Transactional(rollbackFor = Exception.class)
	public NexusServerConfig createServerConfig(NexusServerConfig nexusServerConfig) {

		NexusServerConfig exist = this.queryServerConfig();
		if (exist != null) {
			throw new CommonException("已有nexus服务配置，不允许再新增，请编辑更新");
		}
		nexusServerConfig.setEnabled(1);
		nexusServerConfigRepository.insertSelective(nexusServerConfig);
		return nexusServerConfig;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public NexusServerConfig updateServerConfig(NexusServerConfig nexusServerConfig) {
		NexusServerConfig exist = nexusServerConfigRepository.selectByPrimaryKey(nexusServerConfig);
		if (exist == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		nexusServerConfig.setPassword(DESEncryptUtil.encode(nexusServerConfig.getPassword()));
		nexusServerConfigRepository.updateOptional(nexusServerConfig, NexusServerConfig.FIELD_SERVER_NAME,
				NexusServerConfig.FIELD_SERVER_URL, NexusServerConfig.FIELD_USER_NAME, NexusServerConfig.FIELD_PASSWORD);
		return nexusServerConfig;
	}

	@Override
	public NexusServerConfig queryServerConfig() {
		NexusServerConfig query = new NexusServerConfig();
		query.setEnabled(1);
		List<NexusServerConfig> nexusServerConfigList = nexusServerConfigRepository.select(query);
		if (CollectionUtils.isEmpty(nexusServerConfigList)) {
			return null;
		} else if (nexusServerConfigList.size() >= 2){
			throw new CommonException(NexusMessageConstants.NEXUS_SERVER_CONFIG_MUL);
		} else {
			return nexusServerConfigList.get(0);
		}
	}
}
