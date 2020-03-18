package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusUserApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
		if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
			// TODO 异常信息定义
			throw new CommonException("待删除用户不存在");
		}
	}

	@Override
	public void createUser(NexusUser nexusUser) {
		// 唯一性校验
		List<NexusUser> nexusUserList = this.getUsers(nexusUser.getUserId());
		if (CollectionUtils.isNotEmpty(nexusUserList)) {
			throw new CommonException("用户ID对应用户已存在");
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.User.CREATE_USER, HttpMethod.POST, null, nexusUser);
		if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
			// TODO 异常信息定义
			throw new CommonException("信息");
		}
		// TODO 400
	}

	@Override
	public void updateUser(NexusUser nexusUser) {
		String url = NexusUrlConstants.User.UPDATE_USER + nexusUser.getUserId();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusUser);
		if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
			// TODO 异常信息定义
			throw new CommonException("待更新用户不存在");
		}
		// TODO 404、400
	}

	@Override
	public void changePassword(String userId, String newPassword, String oldPassword) {
		//  TODO 旧密码校验
		String url = NexusUrlConstants.User.CHANGE_PASWORD.replace("{userId}", userId);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, newPassword, MediaType.TEXT_PLAIN_VALUE);
		if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
			// TODO 异常信息定义
			throw new CommonException("对应用户已不存在");
		}

	}
}
