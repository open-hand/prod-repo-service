package org.hrds.rdupm.common.app.service.sagahandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.harbor.domain.entity.User;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:16 下午
 */
@Component
public class ProdUserUpdateHandler {

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private ObjectMapper objectMapper;

	@SagaTask(code = HarborConstants.HarborSagaCode.UPDATE_PWD_HARBOR,description = "更新用户：harbor",
			sagaCode = HarborConstants.HarborSagaCode.UPDATE_PWD,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	private String updateHarborUser(String message){
		ProdUser prodUser = null;
		try {
			prodUser = objectMapper.readValue(message, ProdUser.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		//校验Harbor中是否已存在用户
		Map<String,Object> paramMap = new HashMap<>(1);
		paramMap.put("username",prodUser.getLoginName());
		ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
		List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);
		Map<String,User> userMap = CollectionUtils.isEmpty(userList) ? new HashMap<>(1) : userList.stream().collect(Collectors.toMap(User::getUsername, dto->dto));
		User harborUser = userMap.get(prodUser.getLoginName());

		//更新Harbor中用户密码
		String password = DESEncryptUtil.decode(prodUser.getPassword());
		Map<String,Object> bodyMap = new HashMap<>(2);
		bodyMap.put("new_password",password);
		bodyMap.put("old_password",password);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.CHANGE_PASSWORD,null,bodyMap,true,harborUser.getUserId());

		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.UPDATE_PWD_NEXUS,description = "更新用户：nexus",
			sagaCode = HarborConstants.HarborSagaCode.UPDATE_PWD,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	private String updateNexusUser(String message){
		ProdUser prodUser = null;
		try {
			prodUser = objectMapper.readValue(message, ProdUser.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		//密码需要解密
		//TODO 更新nexus密码逻辑

		return message;
	}



}
