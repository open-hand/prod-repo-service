package org.hrds.rdupm.nexus.app.eventhandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.sagahandler.IamSagaHandler;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.app.service.NexusAuthSageService;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.annotation.NexusOperateLog;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
	@Transactional
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
