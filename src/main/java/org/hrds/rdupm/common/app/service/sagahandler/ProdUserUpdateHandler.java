package org.hrds.rdupm.common.app.service.sagahandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.harbor.domain.entity.User;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ProdUserUpdateHandler.class);

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private NexusServerConfigService configService;
	@Autowired
	private NexusServerConfigRepository nexusServerConfigRepository;
	@Autowired
	private NexusClient nexusClient;

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
		Map<String,Object> bodyMap = new HashMap<>(1);
		bodyMap.put("new_password",password);
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

		List<NexusServerConfig> serverConfigList =  nexusServerConfigRepository.selectAll();

		boolean errorFlag = false;
		List<String> errorInfoList = new ArrayList<>();
		for (NexusServerConfig serverConfig : serverConfigList) {
			try {
				configService.setNexusInfoByConfigId(nexusClient, serverConfig.getConfigId());
				NexusServerUser existUser = nexusClient.getNexusUserApi().getUsers(prodUser.getLoginName());
				if (existUser != null) {
					// 密码解密
					String password = DESEncryptUtil.decode(prodUser.getPassword());
					nexusClient.getNexusUserApi().changePassword(prodUser.getLoginName(), password);
				}
			} catch (Exception e) {
				String error = "密码更新错误, configId: " + serverConfig.getConfigId() + "; userLoginName: " + prodUser.getLoginName();
				errorInfoList.add(error);
				LOGGER.error(error, e);
				errorFlag = true;
			}
			// remove配置信息
		}
		if (errorFlag) {
			throw new CommonException(StringUtils.join(errorInfoList, ";    "));
		}
		nexusClient.removeNexusServerInfo();

		return message;
	}



}
