package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusComponentsApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组件API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Component
public class NexusComponentsHttpApi implements NexusComponentsApi {
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusComponent> getComponents(String repositoryName) {
		Map<String, Object> paramMap = new HashMap<>(2);
		paramMap.put("repository", repositoryName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Components.GET_COMPONENTS_LIST, HttpMethod.GET, paramMap, null);
		String response = responseEntity.getBody();
		ComponentResponse componentResponse = JSON.parseObject(response, ComponentResponse.class);
		List<NexusComponent> componentList = componentResponse.getItems();
		componentList.forEach(nexusComponent -> {
			List<NexusAsset> assetList = nexusComponent.getAssets();
			if (CollectionUtils.isNotEmpty(assetList)) {
				NexusAsset asset = assetList.get(0);
				nexusComponent.setUseVersion(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(asset.getPath(), "/"), "/"));
			}
		});
		return componentList;
	}

	@Override
	public List<NexusComponentInfo> getComponentInfo(String repositoryName) {
		Map<String, NexusComponentInfo> componentInfoMap = new HashMap<>(16);
		List<NexusComponent> componentList = this.getComponents(repositoryName);
		for (NexusComponent component : componentList) {
			String path = component.getGroup() + "/" + component.getName() + "/" + component.getUseVersion();
			if (componentInfoMap.get(path) == null) {
				NexusComponentInfo componentInfo = new NexusComponentInfo();
				BeanUtils.copyProperties(component, componentInfo);
				componentInfo.setPath(path);

				List<NexusComponent> components = new ArrayList<>();
				components.add(component);
				componentInfo.setComponents(components);

				componentInfoMap.put(path, componentInfo);
			} else {
				NexusComponentInfo componentInfo = componentInfoMap.get(path);
				componentInfo.getComponents().add(component);
			}
		}
		return new ArrayList<>(componentInfoMap.values());
	}

	@Override
	public void deleteComponent(String componentId) {
		String url = NexusUrlConstants.Components.DELETE_COMPONENTS + componentId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		if (responseEntity.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
			throw new CommonException(NexusConstants.ErrorMessage.COMPONENT_ID_ERROR);
		}
	}

	@Override
	public void createMavenComponent(NexusComponentUpload componentUpload) {
		Map<String, Object> paramMap = new HashMap<>(2);
		paramMap.put(NexusComponentUpload.REPOSITORY_NAME, componentUpload.getRepositoryName());

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(NexusComponentUpload.GROUP_ID, componentUpload.getGroupId());
		body.add(NexusComponentUpload.ARTIFACT_ID, componentUpload.getArtifactId());
		body.add(NexusComponentUpload.VERSION, componentUpload.getVersion());
		body.add(NexusComponentUpload.GENERATE_POM, true);
		for (int i = 0; i < componentUpload.getAssetUploads().size(); i++) {
			NexusAssetUpload assetUpload = componentUpload.getAssetUploads().get(i);
			body.add(NexusComponentUpload.ASSET_FILE.replace("{num}", String.valueOf(i+1)), assetUpload.getAssetName());
			body.add(NexusComponentUpload.ASSET_EXTENSION.replace("{num}", String.valueOf(i+1)), assetUpload.getExtension());
		}
		ResponseEntity<String> responseEntity = nexusRequest.exchangeFormData(NexusUrlConstants.Components.UPLOAD_COMPONENTS, HttpMethod.POST, paramMap, body);

	}
}
