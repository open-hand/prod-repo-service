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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.tr;

/**
 * @author weisen.yang@hand-china.com 2020/3/18
 */
@Component
public class NexusUserHttpApi implements NexusUserApi{

	private static final Logger LOGGER = LoggerFactory.getLogger(NexusUserHttpApi.class);

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
		List<NexusServerUser> nexusServerUsers = JSONObject.parseArray(response, NexusServerUser.class);
		if (CollectionUtils.isEmpty(nexusServerUsers)) {
			return new ArrayList<>();
		}
		return nexusServerUsers.stream().filter(nexusServerUser -> nexusServerUser.getUserId().equals(userId)).collect(Collectors.toList());
	}

	@Override
	public void deleteUser(String userId) {
		String url = NexusUrlConstants.User.DELETE_USER + userId;
		try {
			ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.warn("nexus user has been deleted");
			} else {
				throw e;
			}
		}
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
		String url = NexusUrlConstants.User.CHANGE_PASSWORD.replace("{userId}", userId);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, newPassword, MediaType.TEXT_PLAIN_VALUE);
	}

	@Override
	public List<String> validPush(List<String> repositoryList, String userName, List<String> ruleList) {
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
		if (CollectionUtils.isEmpty(userPrivileges)) {
			return new ArrayList<>();
		}

		List<String> result = new ArrayList<>();

		// 遍历仓库，分仓库校验
		repositoryList.forEach(repositoryName -> {
			Boolean flag = this.validRule(userPrivileges, ruleList, repositoryName);
			if (flag) {
				result.add(repositoryName);
			}
		});

		return result;
	}

	/**
	 * 发布规则校验
	 * @param userPrivileges 该用户拥有的所有权限
	 * @param ruleList 规则列表
	 * @param repositoryName 仓库名
	 * @return true: 当前权限里，有该仓库的发布权限     false: 当前权限里，无该仓库的发布权限
	 */
	private Boolean validRule(List<String> userPrivileges, List<String> ruleList, String repositoryName){
		for (String ruleArray : ruleList) {
			List<String> rules = Arrays.asList(StringUtils.split(ruleArray, ","));
			List<String> rulesNew = rules.stream().map(rule -> {return rule.replaceAll("\\{repositoryName}", repositoryName);}).collect(Collectors.toList());

			Boolean flag = this.includeRule(userPrivileges, rulesNew);
			if (flag) {
				// 为true,表明已经匹配了某个规则
				return true;
			}
		}
		return false;
	}

	private Boolean includeRule(List<String> userPrivileges, List<String> rules){
		for(String rule : rules) {
			if (!userPrivileges.contains(rule)) {
				// 当前已有规则里，不包含该规则
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean validUserNameAndPassword(String userName, String password, NexusServer currentNexusServer) {
		NexusServer nexusServer = new NexusServer(currentNexusServer.getBaseUrl(), userName, password);
		// 设置当前访问地址与用户
		nexusRequest.setNexusServerInfo(nexusServer);
		try {
			List<NexusServerRepository> repositoryList = nexusRepositoryApi.getRepository(null);
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
