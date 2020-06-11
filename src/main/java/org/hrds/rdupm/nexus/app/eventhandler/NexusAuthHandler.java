package org.hrds.rdupm.nexus.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author weisen.yang@hand-china.com 2020/04/28 5:16 下午
 */
@Component
public class NexusAuthHandler {
	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private NexusClient nexusClient;
	@Autowired
	private NexusServerConfigService configService;

	@Autowired
	private ProdUserService prodUserService;
	@Autowired
	private ProdUserRepository prodUserRepository;

	@SagaTask(code = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE_USER, description = "分配权限：插入nexus用户与角色",
			sagaCode = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE, seq = 1, maxRetryCount = 3, outputSchemaClass = String.class)
	public String nexusAuthCreate(String message) {
		List<NexusAuth> nexusAuthList = JSONObject.parseArray(message, NexusAuth.class);

		NexusServerConfig serverConfig = configService.setNexusInfo(nexusClient);
		this.createUserAuth(nexusAuthList);

		nexusClient.removeNexusServerInfo();
		return message;
	}

	public void createUserAuth(List<NexusAuth> nexusAuthList) {
		Condition userCondition = Condition.builder(NexusUser.class)
				.where(Sqls.custom()
						.andIn(ProdUser.FIELD_USER_ID, nexusAuthList.stream().map(NexusAuth::getUserId).collect(Collectors.toList())))
				.build();
		List<ProdUser> prodUserList = prodUserRepository.selectByCondition(userCondition);
		Map<Long, ProdUser> prodUserMap = prodUserList.stream().collect(Collectors.toMap(ProdUser::getUserId, User -> User));
		for (NexusAuth nexusAuth : nexusAuthList) {
			// 正常不会进入，只是判断可能的情况
			if (prodUserMap.get(nexusAuth.getUserId()) == null) {
				String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
				ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password, 0);
				prodUserService.saveMultiUser(Collections.singletonList(prodUser));
				prodUserList.add(prodUser);
			}
		}
		prodUserMap = prodUserList.stream().collect(Collectors.toMap(ProdUser::getUserId, User -> User));


		for (NexusAuth nexusAuth : nexusAuthList) {
			ProdUser prodUser = prodUserMap.get(nexusAuth.getUserId());

			List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
			if (CollectionUtils.isEmpty(existUserList)) {
				// 创建用户
				NexusServerUser nexusServerUser = new NexusServerUser(nexusAuth.getLoginName(), nexusAuth.getRealName(), nexusAuth.getRealName(), prodUser.getPassword(), Collections.singletonList(nexusAuth.getNeRoleId()));
				nexusClient.getNexusUserApi().createUser(nexusServerUser);
			} else {
				// 更新用户
				NexusServerUser nexusServerUser = existUserList.get(0);
				nexusServerUser.getRoles().add(nexusAuth.getNeRoleId());
				nexusClient.getNexusUserApi().updateUser(nexusServerUser);
			}

		}
	}
}
