package org.hrds.rdupm.nexus.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:16 下午
 */
@Component
public class NexusAuthHandler {
	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private ProdUserService prodUserService;

	@SagaTask(code = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE_USER, description = "分配权限：插入nexus用户与角色",
			sagaCode = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE, seq = 1, maxRetryCount = 3, outputSchemaClass = String.class)
	private String nexusAuthCreate(String message) {
		List<NexusAuth> nexusAuthList = JSONObject.parseArray(message, NexusAuth.class);
		List<ProdUser> prodUserList = new ArrayList<>();

		for(NexusAuth nexusAuth : nexusAuthList){
			//数据库插入制品库用户
			String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
			ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password,0);
			prodUserList.add(prodUser);
			// TODO
		}
		prodUserService.saveMultiUser(prodUserList);
		return message;
	}


}
