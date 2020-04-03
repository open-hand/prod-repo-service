package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRoleApi;
import org.hrds.rdupm.nexus.client.nexus.api.NexusUserApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRole;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
	@Autowired
	private NexusRoleApi nexusRoleApi;
	@Autowired
	private NexusRepositoryApi nexusRepositoryApi;

	@Override
	public List<NexusServerUser> getUsers(String userId) {
		Map<String, Object> paramMap = null;
		if (StringUtils.isNotEmpty(userId)) {
			paramMap = new HashMap<>(2);
			paramMap.put("userId", userId);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.User.GET_USER_LIST, HttpMethod.GET, paramMap, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusServerUser.class);
	}

	@Override
	public void deleteUser(String userId) {
		String url = NexusUrlConstants.User.DELETE_USER + userId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
	}

	@Override
	public void createUser(NexusServerUser nexusUser) {
		// 唯一性校验
		List<NexusServerUser> nexusUserList = this.getUsers(nexusUser.getUserId());
		if (CollectionUtils.isNotEmpty(nexusUserList)) {
			throw new CommonException(NexusApiConstants.ErrorMessage.USER_EXIST);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.User.CREATE_USER, HttpMethod.POST, null, nexusUser);
	}

	@Override
	public void updateUser(NexusServerUser nexusUser) {
		String url = NexusUrlConstants.User.UPDATE_USER + nexusUser.getUserId();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusUser);
	}

	@Override
	public void changePassword(String userId, String newPassword) {
		//  TODO 旧密码校验
		String url = NexusUrlConstants.User.CHANGE_PASSWORD.replace("{userId}", userId);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, newPassword, MediaType.TEXT_PLAIN_VALUE);
	}

	@Override
	public List<String> validPush(List<String> repositoryList, String userName) {
		List<NexusServerUser> nexusServerUserList = this.getUsers(userName);
		if (CollectionUtils.isEmpty(nexusServerUserList)) {
			throw new CommonException("用户不存在");
		}

		// 该用户拥有的所有权限
		List<String> userPrivileges = new ArrayList<>();

		NexusServerUser nexusServerUser = nexusServerUserList.get(0);
		nexusServerUser.getRoles().forEach(roleId -> {
			NexusServerRole nexusServerRole = nexusRoleApi.getRoleById(roleId);
			if (nexusServerRole != null) {
				userPrivileges.addAll(nexusServerRole.getPrivileges());
			}
		});


		// TODO 返回与校验
		return repositoryList;
	}

	@Override
	public Boolean validUserNameAndPassword(String userName, String password, NexusServer currentNexusServer) {
		NexusServer nexusServer = new NexusServer(currentNexusServer.getBaseUrl(), userName, password);
		// 设置当前访问地址与用户
		nexusRequest.setNexusServerInfo(nexusServer);
		try {
			List<NexusServerRepository> repositoryList = nexusRepositoryApi.getRepository();
		} catch (NexusResponseException e) {
			// 返回状态为401, 表明用户、密码错误
			return e.getStatusCode() != HttpStatus.UNAUTHORIZED;
		} finally {
			// 设置回先前的设置
			nexusRequest.setNexusServerInfo(currentNexusServer);
		}
		return true;
	}
}
