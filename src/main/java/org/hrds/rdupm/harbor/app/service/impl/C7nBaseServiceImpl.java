package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 文档库-用户表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-02-18 10:35:56
 */
@Service
public class C7nBaseServiceImpl implements C7nBaseService {

	@Resource
	private BaseFeignClient baseFeignClient;

	@Override
	public Map<String, UserDTO> listUsersByLoginNames(Set<String> userNameSet) {
		ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listUsersByLoginNames(userNameSet.toArray(new String[userNameSet.size()]),true);
		if (!CollectionUtils.isEmpty(responseEntity.getBody())) {
			return responseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getLoginName, dto->dto));
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public Map<Long, ProjectDTO> queryProjectByIds(Set<Long> projectIdSet) {
		ResponseEntity<List<ProjectDTO>> projectResponseEntity = baseFeignClient.queryByIds(projectIdSet);
		if (!CollectionUtils.isEmpty(projectResponseEntity.getBody())) {
			return projectResponseEntity.getBody().stream().collect(Collectors.toMap(ProjectDTO::getId,dto->dto));
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public ProjectDTO queryProjectById(Long projectId) {
		ResponseEntity<ProjectDTO> responseEntity = baseFeignClient.query(projectId);
		if (responseEntity != null) {
			return responseEntity.getBody();
		} else {
			return new ProjectDTO();
		}
	}

	@Override
	public Map<Long, UserDTO> listUsersByIds(Set<Long> userIdSet) {
		ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]),true);
		if (!CollectionUtils.isEmpty(userDtoResponseEntity.getBody())) {
			return userDtoResponseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getId, dto->dto));
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public Map<Long, UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIds(Long projectId, Set<Long> userIdSet) {
		ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
		if (!CollectionUtils.isEmpty(responseEntity.getBody())) {
			return responseEntity.getBody().stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId,dto->dto));
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public UserDTO listUserById(Long userId) {
		Set<Long> userIdSet = new HashSet<>(1);
		userIdSet.add(userId);
		Map<Long, UserDTO> map = this.listUsersByIds(userIdSet);
		return map.get(userId);
	}

	@Override
	public UserDTO queryByLoginName(String loginName) {
		Set<String> userNameSet = new HashSet<>(1);
		userNameSet.add(loginName);
		Map<String, UserDTO> map = this.listUsersByLoginNames(userNameSet);
		return map.get(loginName);
	}

	@Override
	public List<UserDTO> listProjectUsersByIdName(Long projectId, String name) {
		ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listUsersByName(projectId,name);
		if (!CollectionUtils.isEmpty(responseEntity.getBody())) {
			return responseEntity.getBody();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public UserDTO listProjectOwnerById(Long projectId) {
		ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listProjectOwnerById(projectId);
		if (!CollectionUtils.isEmpty(responseEntity.getBody())) {
			return 	responseEntity.getBody().stream().findFirst().orElse(null);
		} else {
			return null;
		}
	}

}

