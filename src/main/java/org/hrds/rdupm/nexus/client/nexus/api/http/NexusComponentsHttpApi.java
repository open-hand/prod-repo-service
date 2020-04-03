package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusComponentsApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hzero.core.util.UUIDUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 组件API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Component
public class NexusComponentsHttpApi implements NexusComponentsApi {
	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public List<NexusServerComponent> getComponents(String repositoryName) {
		Map<String, Object> paramMap = new HashMap<>(2);
		paramMap.put("repository", repositoryName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Components.GET_COMPONENTS_LIST, HttpMethod.GET, paramMap, null);
		String response = responseEntity.getBody();
		ComponentResponse componentResponse = JSON.parseObject(response, ComponentResponse.class);
		List<NexusServerComponent> componentList = componentResponse.getItems();
		this.handleVersion(componentList);
		return componentList;
	}

	@Override
	public List<NexusServerComponentInfo> getComponentInfo(String repositoryName) {
		List<NexusServerComponent> componentList = this.getComponents(repositoryName);
		return this.componentGroup(componentList);
	}

	@Override
	public List<NexusServerComponent> searchComponent(NexusComponentQuery componentQuery) {
		Map<String, Object> paramMap = componentQuery.convertParam();
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Search.SEARCH_COMPONENT, HttpMethod.GET, paramMap, null);
		String response = responseEntity.getBody();
		ComponentResponse componentResponse = JSON.parseObject(response, ComponentResponse.class);
		List<NexusServerComponent> componentList = componentResponse.getItems();
		this.handleVersion(componentList);
		return componentList;
	}

	@Override
	public List<NexusServerComponentInfo> searchComponentInfo(NexusComponentQuery componentQuery) {
		List<NexusServerComponent> componentList = this.searchComponent(componentQuery);
		return this.componentGroup(componentList);
	}

	@Override
	public void deleteComponent(String componentId) {
		String url = NexusUrlConstants.Components.DELETE_COMPONENTS + componentId;
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		if (responseEntity.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
			throw new CommonException(NexusApiConstants.ErrorMessage.COMPONENT_ID_ERROR);
		}
	}

	@Override
	public void createMavenComponent(NexusServerComponentUpload componentUpload) {
		Map<String, Object> paramMap = new HashMap<>(2);
		paramMap.put(NexusServerComponentUpload.REPOSITORY_NAME, componentUpload.getRepositoryName());

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(NexusServerComponentUpload.GROUP_ID, componentUpload.getGroupId());
		body.add(NexusServerComponentUpload.ARTIFACT_ID, componentUpload.getArtifactId());
		body.add(NexusServerComponentUpload.VERSION, componentUpload.getVersion());

		// 上传文件类型
		List<String> extensionList = new ArrayList<>();
		for (int i = 0; i < componentUpload.getAssetUploads().size(); i++) {
			NexusServerAssetUpload assetUpload = componentUpload.getAssetUploads().get(i);
			body.add(NexusServerComponentUpload.ASSET_FILE.replace("{num}", String.valueOf(i+1)), assetUpload.getAssetName());
			body.add(NexusServerComponentUpload.ASSET_EXTENSION.replace("{num}", String.valueOf(i+1)), assetUpload.getExtension());
			extensionList.add(assetUpload.getExtension());
		}
		if (extensionList.contains(NexusServerAssetUpload.JAR)
				&& !extensionList.contains(NexusServerAssetUpload.POM)) {
			body.add(NexusServerComponentUpload.GENERATE_POM, true);
		} else {
			body.add(NexusServerComponentUpload.GENERATE_POM, false);
		}

		ResponseEntity<String> responseEntity = nexusRequest.exchangeFormData(NexusUrlConstants.Components.UPLOAD_COMPONENTS, HttpMethod.POST, paramMap, body);

	}

	/**
	 * 包组件分组处理
	 * @param componentList 组件（包）信息
	 * @return List<NexusServerComponentInfo>
	 */
	private List<NexusServerComponentInfo> componentGroup(List<NexusServerComponent> componentList){
		Map<String, NexusServerComponentInfo> componentInfoMap = new HashMap<>(16);
		for (NexusServerComponent component : componentList) {
			String path = component.getGroup() + "/" + component.getName() + "/" + component.getUseVersion();
			String key = component.getRepository() + path;
			if (componentInfoMap.get(key) == null) {
				NexusServerComponentInfo componentInfo = new NexusServerComponentInfo();
				BeanUtils.copyProperties(component, componentInfo);
				componentInfo.setPath(path);

				List<NexusServerComponent> components = new ArrayList<>();
				components.add(component);
				componentInfo.setComponents(components);

				componentInfoMap.put(key, componentInfo);
			} else {
				NexusServerComponentInfo componentInfo = componentInfoMap.get(key);
				componentInfo.getComponents().add(component);
			}
		}
		List<NexusServerComponentInfo> componentInfoList = new ArrayList<>(componentInfoMap.values());
		componentInfoList.forEach(componentInfo -> {
			// 生成主键
			componentInfo.setId(UUIDUtils.generateUUID());
			componentInfo.setVersion(componentInfo.getUseVersion());
			List<NexusServerComponent> components = componentInfo.getComponents();
			if (components.size() == 1){
				NexusServerComponent nexusServerComponent = components.get(0);
				if (nexusServerComponent.getUseVersion().equals(nexusServerComponent.getVersion())) {
					// 版本相同时，不用再有下级； 如 RELEASE 版本的jar包
					componentInfo.setComponents(new ArrayList<>());
				}
			}
			// 设置Id
			componentInfo.setComponentIds(components.stream().map(NexusServerComponent::getId).collect(Collectors.toList()));
		});

		return componentInfoList;
	}

	/**
	 * 版本处理
	 * @param componentList 组件（包）信息
	 */
	private void handleVersion(List<NexusServerComponent> componentList){
		componentList.forEach(nexusComponent -> {
			List<NexusServerAsset> assetList = nexusComponent.getAssets();
			if (CollectionUtils.isNotEmpty(assetList)) {
				NexusServerAsset asset = assetList.get(0);
				nexusComponent.setUseVersion(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(asset.getPath(), "/"), "/"));
			}
			nexusComponent.setComponentIds(Collections.singletonList(nexusComponent.getId()));
			// assets 前端不需要，数据量太大
			nexusComponent.setAssets(new ArrayList<>());
		});
	}
}