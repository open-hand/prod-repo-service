package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.api.NexusScriptApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仓库API
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@Component
public class NexusRepositoryHttpApi implements NexusRepositoryApi{
	@Autowired
	private NexusRequest nexusRequest;
	@Autowired
	private NexusScriptApi nexusScriptApi;

	@Override
	public List<NexusServerRepository> getRepository() {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.GET_REPOSITORY_MANAGE_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		List<RepositoryMavenInfo> repositoryMavenInfoList = JSONObject.parseArray(response, RepositoryMavenInfo.class);

		List<NexusServerRepository> nexusServerRepositoryList = new ArrayList<>();
		repositoryMavenInfoList.forEach(repositoryMavenInfo -> {
			NexusServerRepository nexusServerRepository = repositoryMavenInfo.covertNexusServerRepository();
			nexusServerRepositoryList.add(nexusServerRepository);

		});
		return nexusServerRepositoryList;
	}

	@Override
	public NexusServerRepository getRepositoryByName(String repositoryName) {
		List<NexusServerRepository> repositoryList = this.getRepository();
		List<NexusServerRepository> queryList = repositoryList.stream().filter(nexusRepository -> nexusRepository.getName().equals(repositoryName)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(queryList)) {
			return queryList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Boolean repositoryExists(String repositoryName) {
		NexusServerRepository nexusRepository = this.getRepositoryByName(repositoryName);
		if (nexusRepository != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteRepository(String repositoryName) {
		String url = NexusUrlConstants.Repository.DELETE_REPOSITORY + repositoryName;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
	}

	@Override
	public void createMavenRepository(RepositoryMavenInfo repositoryRequest) {
		// 唯一性校验
		if (this.repositoryExists(repositoryRequest.getName())){
			throw new CommonException(NexusApiConstants.ErrorMessage.REPO_NAME_EXIST);
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.CREATE_MAVEN_HOSTED_REPOSITORY, HttpMethod.POST, null, repositoryRequest);

	}

	@Override
	public void updateMavenRepository(RepositoryMavenInfo repositoryRequest) {
		// 创建本地仓库
		String url = NexusUrlConstants.Repository.UPDATE_MAVEN_HOSTED_REPOSITORY + repositoryRequest.getName();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, repositoryRequest);

	}

	@Override
	public void createAndUpdateMavenGroup(NexusServerMavenGroup nexusMavenGroup) {
		String param = JSONObject.toJSONString(nexusMavenGroup);
		nexusScriptApi.runScript(NexusApiConstants.ScriptName.CREATE_MAVEN_GROUP, param);
	}

	@Override
	public void createAndUpdateMavenProxy(NexusServerMavenProxy nexusMavenProxy) {
		String param = JSONObject.toJSONString(nexusMavenProxy);
		nexusScriptApi.runScript(NexusApiConstants.ScriptName.CREATE_MAVEN_PROXY, param);
	}
}
