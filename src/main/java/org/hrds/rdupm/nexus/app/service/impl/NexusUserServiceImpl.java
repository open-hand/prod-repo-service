package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.app.service.NexusUserService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 制品库_nexus仓库默认用户信息表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Service
public class NexusUserServiceImpl implements NexusUserService {
	@Autowired
	private NexusUserRepository nexusUserRepository;
	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;
	@Autowired
	private NexusServerConfigService configService;
	@Autowired
	private NexusClient nexusClient;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePassword(NexusUser nexusUser) {
		NexusUser existUser = nexusUserRepository.selectByUserId(nexusUser.getUserId());
		if (existUser == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		if (!existUser.getOrganizationId().equals(nexusUser.getOrganizationId())
				|| !existUser.getProjectId().equals(nexusUser.getProjectId())) {
			throw new CommonException(NexusMessageConstants.NEXUS_NOT_CHANGE_OTHER_REPO_PWD);
		}
		if (!existUser.getNeUserPassword().equals(nexusUser.getOldNeUserPassword())) {
			throw new CommonException(NexusMessageConstants.NEXUS_OLD_PASSWORD_ERROR);
		}

		nexusUserRepository.updateOptional(nexusUser, NexusUser.FIELD_NE_USER_PASSWORD);

		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		nexusClient.getNexusUserApi().changePassword(existUser.getNeUserId(), nexusUser.getNeUserPassword());
		// remove配置信息
		nexusClient.removeNexusServerInfo();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePushAuth(NexusUser nexusUser) {
		NexusUser existUser = nexusUserRepository.selectByUserId(nexusUser.getUserId());
		if (existUser == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		// 查询更新前数据
		List<String> existOtherRepoName = nexusUserRepository.getOtherRepositoryNames(existUser.getNeUserId());

		// 删除更新前数据
		NexusUser delete = new NexusUser();
		delete.setNeUserId(existUser.getNeUserId());
		delete.setIsDefault(0);
		nexusUserRepository.delete(delete);

		// 待更新数据
		List<String> updateOtherRepoName = new ArrayList<>();
		List<NexusUser> updateNexusUserList = new ArrayList<>();

		List<String> otherRepositoryNameList = nexusUser.getOtherRepositoryName();
		if (CollectionUtils.isNotEmpty(otherRepositoryNameList)) {
			// 排除默认仓库
			otherRepositoryNameList.remove(existUser.getNeRepositoryName());

			NexusRepository queryRepo = new NexusRepository();
			otherRepositoryNameList.forEach(otherRepositoryName ->{
				queryRepo.setNeRepositoryName(otherRepositoryName);
				queryRepo.setProjectId(existUser.getProjectId());
				NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(queryRepo);
				if (nexusRepository != null) {
					updateOtherRepoName.add(nexusRepository.getNeRepositoryName());
					NexusUser updateUser = new NexusUser();
					updateUser.setIsDefault(0);
					updateUser.setRepositoryId(nexusRepository.getRepositoryId());
					updateUser.setNeUserId(existUser.getNeUserId());
					updateNexusUserList.add(updateUser);
				}
			});
		}

		// 数据库更新
		if (CollectionUtils.isNotEmpty(updateNexusUserList)) {
			nexusUserRepository.batchInsertSelective(updateNexusUserList);
		}

		// nexus 信息更新

		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		NexusServerRole serverRole = nexusClient.getNexusRoleApi().getRoleById(existUser.getNeRoleId());
		// 删除以前仓库的权限
		existOtherRepoName.forEach(serverRole::removeDefPushPri);
		// 添加现在仓库的权限
		updateOtherRepoName.forEach(serverRole::addDefPushPri);
		nexusClient.getNexusRoleApi().updateRole(serverRole);

		// remove配置信息
		nexusClient.removeNexusServerInfo();

	}
}
