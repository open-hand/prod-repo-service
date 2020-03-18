package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.nexus.client.nexus.api.NexusRepositoryApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusRepository;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.model.RepositoryMavenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 仓库API
 * @author weisen.yang@hand-china.com 2020/3/16
 */
@Component
public class NexusRepositoryHttpApi implements NexusRepositoryApi{
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusRepository> getRepository() {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.GET_REPOSITORY_MANAGE_LIST, HttpMethod.GET, null, null);
		String response = responseEntity.getBody();
		return JSONObject.parseArray(response, NexusRepository.class);
	}

	@Override
	public void deleteRepository(String repositoryName) {
		String url = NexusUrlConstants.Repository.DELETE_REPOSITORY + repositoryName;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
			// TODO 异常信息定义
			throw new CommonException("待删除仓库不存在");
		}
		System.out.println(responseEntity.getStatusCode());
		System.out.println(responseEntity.getBody());
	}

	@Override
	public void createMavenRepository(RepositoryMavenRequest repositoryRequest) {
		ResponseEntity<String> responseEntity = null;
		if (NexusConstants.RepositoryType.HOSTED.equals(repositoryRequest.getType())) {
			// 创建本地仓库
			responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.CREATE_MAVEN_HOSTED_REPOSITORY, HttpMethod.POST, null, repositoryRequest);

		} else if (NexusConstants.RepositoryType.PROXY.equals(repositoryRequest.getType())) {
			// 创建代理仓库
			responseEntity = nexusRequest.exchange(NexusUrlConstants.Repository.CREATE_MAVEN_PROXY_REPOSITORY, HttpMethod.POST, null, repositoryRequest);
		} else {
			throw new CommonException("仓库类型错误");
		}
		// TODO 400
		System.out.println(responseEntity.getStatusCode());

	}

	@Override
	public void updateMavenRepository(RepositoryMavenRequest repositoryRequest) {
		ResponseEntity<String> responseEntity = null;
		if (NexusConstants.RepositoryType.HOSTED.equals(repositoryRequest.getType())) {
			// 创建本地仓库
			String url = NexusUrlConstants.Repository.UPDATE_MAVEN_HOSTED_REPOSITORY + repositoryRequest.getName();
			responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, repositoryRequest);
		} else if (NexusConstants.RepositoryType.PROXY.equals(repositoryRequest.getType())) {
			// 创建代理仓库
			String url = NexusUrlConstants.Repository.UPDATE_MAVEN_PROXY_REPOSITORY + repositoryRequest.getName();
			responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, repositoryRequest);
		} else {
			throw new CommonException("仓库类型错误");
		}
		// TODO 400
		System.out.println(responseEntity.getStatusCode());
	}
}
