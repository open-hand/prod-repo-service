package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.api.NexusScriptApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(NexusRepositoryHttpApi.class);

	@Autowired
	private NexusRequest nexusRequest;
	@Autowired
	private NexusScriptApi nexusScriptApi;

	@Override
	public List<NexusServerRepository> getRepository(String nexusFormat) {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.GET_REPOSITORY_MANAGE_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();

		List<RepositoryMavenInfo> repositoryMavenInfoList = JSONObject.parseArray(response, RepositoryMavenInfo.class);

		List<NexusServerRepository> nexusServerRepositoryList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(repositoryMavenInfoList)) {
			repositoryMavenInfoList.forEach(repositoryMavenInfo -> {
				NexusServerRepository nexusServerRepository = repositoryMavenInfo.covertNexusServerRepository();
				if (nexusFormat != null) {
					if (nexusServerRepository.getFormat().equals(nexusFormat)) {
						// 过滤类型
						nexusServerRepositoryList.add(nexusServerRepository);
					}
				} else {
					nexusServerRepositoryList.add(nexusServerRepository);
				}
			});
		}
		return nexusServerRepositoryList;
	}

	@Override
	public NexusServerRepository getRepositoryByName(String repositoryName) {
		List<NexusServerRepository> repositoryList = this.getRepository(null);
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
		return nexusRepository != null;
	}

	@Override
	public void deleteRepository(String repositoryName) {
		String url = NexusUrlConstants.Repository.DELETE_REPOSITORY + repositoryName;
		try {
			ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.warn("nexus repository has been deleted");
			} else {
				throw e;
			}
		}
	}

	@Override
	public void createMavenRepository(RepositoryMavenInfo repositoryRequest) {
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

	@Override
	public void createAndUpdateNpmHosted(RepositoryMavenInfo repositoryRequest) {
		String param = JSONObject.toJSONString(repositoryRequest);
		nexusScriptApi.runScript(NexusApiConstants.ScriptName.CREATE_NPM_HOSTED, param);
	}

	@Override
	public void createAndUpdateNpmGroup(NexusServerMavenGroup nexusMavenGroup) {
		String param = JSONObject.toJSONString(nexusMavenGroup);
		nexusScriptApi.runScript(NexusApiConstants.ScriptName.CREATE_NPM_GROUP, param);
	}

	@Override
	public void createAndUpdateNpmProxy(NexusServerMavenProxy nexusMavenProxy) {
		String param = JSONObject.toJSONString(nexusMavenProxy);
		nexusScriptApi.runScript(NexusApiConstants.ScriptName.CREATE_NPM_PROXY, param);
	}
}
