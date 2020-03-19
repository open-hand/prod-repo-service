package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusUserApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weisen.yang@hand-china.com 2020/3/18
 */
@Component
public class NexusUserHttpApi implements NexusUserApi{
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusUser> getUsers(String userId) {
		Map<String, Object> paramMap = null;
		if (StringUtils.isNotEmpty(userId)) {
			paramMap = new HashMap<>(2);
			paramMap.put("userId", userId);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.User.GET_USER_LIST, HttpMethod.GET, paramMap, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusUser.class);
	}

	@Override
	public void deleteUser(String userId) {
		String url = NexusUrlConstants.User.DELETE_USER + userId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
	}

	@Override
	public void createUser(NexusUser nexusUser) {
		// 唯一性校验
		List<NexusUser> nexusUserList = this.getUsers(nexusUser.getUserId());
		if (CollectionUtils.isNotEmpty(nexusUserList)) {
			throw new CommonException(NexusConstants.ErrorMessage.USER_EXIST);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.User.CREATE_USER, HttpMethod.POST, null, nexusUser);
	}

	@Override
	public void updateUser(NexusUser nexusUser) {
		String url = NexusUrlConstants.User.UPDATE_USER + nexusUser.getUserId();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusUser);
	}

	@Override
	public void changePassword(String userId, String newPassword, String oldPassword) {
		//  TODO 旧密码校验
		String url = NexusUrlConstants.User.CHANGE_PASSWORD.replace("{userId}", userId);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, newPassword, MediaType.TEXT_PLAIN_VALUE);
	}
}
