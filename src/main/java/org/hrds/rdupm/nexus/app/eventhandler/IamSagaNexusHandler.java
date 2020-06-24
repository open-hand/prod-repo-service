package org.hrds.rdupm.nexus.app.eventhandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.app.service.sagahandler.IamSagaHandler;
import org.hrds.rdupm.nexus.app.service.NexusAuthSageService;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hzero.core.base.AopProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 删除用户角色，同步删除制品用户权限
 *
 * @author weisen.yang@hand-china.com
 */
@Component
public class IamSagaNexusHandler implements AopProxy<IamSagaNexusHandler> {

	public static final String NEXUS_DELETE_AUTH = "rdupm-nexus-delete-auth";

	private String project = "project";

	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;
	@Autowired
	private NexusAuthSageService nexusAuthSageService;



	@SagaTask(code = NEXUS_DELETE_AUTH, description = " 制品库删除权限同步事件-nexus(maven与npm)", sagaCode = IamSagaHandler.IAM_DELETE_MEMBER_ROLE, maxRetryCount = 3, seq = 1)
	public String delete(String payload) {
		List<IamGroupMemberVO> iamGroupMemberVOList = new Gson().fromJson(payload, new TypeToken<List<IamGroupMemberVO>>() {}.getType());
		iamGroupMemberVOList.forEach(dto -> {
			if (project.equals(dto.getResourceType())) {
				List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.select(NexusRepository.FIELD_PROJECT_ID, dto.getResourceId());
				if (CollectionUtils.isNotEmpty(nexusRepositoryList)) {
					// 分别发送sagtask
					nexusRepositoryList.forEach(nexusRepository -> {
						nexusAuthSageService.handlerRepo(nexusRepository, dto.getUserId());
					});
				}
			}
		});
		return payload;
	}





}
