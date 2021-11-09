package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.core.exception.CommonException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.api.vo.AssetQuery;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusComponentsApi;
import org.hrds.rdupm.nexus.client.nexus.api.NexusScriptApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
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
 *
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Component
public class NexusComponentsHttpApi implements NexusComponentsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(NexusComponentsHttpApi.class);

    @Autowired
    private NexusRequest nexusRequest;
    @Autowired
    private NexusScriptApi nexusScriptApi;
    @Autowired
    @Lazy
    private NexusRepositoryHttpApi nexusRepositoryHttpApi;

    @Override
    public List<NexusServerComponent> searchMavenComponent(NexusComponentQuery componentQuery) {
        Map<String, Object> paramMap = componentQuery.convertMavenParam();
        ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Search.SEARCH_COMPONENT, HttpMethod.GET, paramMap, null);
        String response = responseEntity.getBody();
        ComponentResponse componentResponse = JSON.parseObject(response, ComponentResponse.class);
        List<NexusServerComponent> componentList = componentResponse.getItems();
        componentList = componentList.stream().filter(nexusServerComponent -> StringUtils.equals(nexusServerComponent.getFormat(), NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT)).collect(Collectors.toList());
        this.handleVersion(componentList);
        return componentList;
    }

    @Override
    public List<NexusServerComponent> searchComponentScript(NexusComponentQuery componentQuery) {
        AssertUtils.notNull(componentQuery.getRepositoryName(), "repositoryName not null");
        String param = JSONObject.toJSONString(componentQuery);
        NexusScriptResult nexusScriptResult = nexusScriptApi.runScript(NexusApiConstants.ScriptName.COMPONENT_LIST_QUERY, param);
        String result = nexusScriptResult.getResult();
        ComponentResponse componentResponse = JSON.parseObject(result, ComponentResponse.class);
        return componentResponse.getItems();
    }

    @Override
    public List<NexusServerComponentInfo> searchMavenComponentInfo(NexusComponentQuery componentQuery) {
        List<NexusServerComponent> componentList = this.searchComponentScript(componentQuery);
        componentList = componentList.stream().filter(nexusServerComponent -> StringUtils.equals(nexusServerComponent.getFormat(), NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT)).collect(Collectors.toList());
        this.handleVersion(componentList);
        return this.mavenComponentGroup(componentList);
    }

    @Override
    public List<NexusServerComponent> searchNpmComponent(NexusComponentQuery componentQuery) {
        List<NexusServerComponent> componentList = this.searchComponentScript(componentQuery);
        componentList = componentList.stream().filter(nexusServerComponent -> StringUtils.equals(nexusServerComponent.getFormat(), NexusApiConstants.NexusRepoFormat.NPM_FORMAT)).collect(Collectors.toList());
        return componentList;
    }

    @Override
    public List<NexusServerComponentInfo> searchNpmComponentInfo(NexusComponentQuery componentQuery) {
        List<NexusServerComponent> componentList = this.searchNpmComponent(componentQuery);
        return this.npmComponentGroup(componentList);
    }

    @Override
    public void deleteComponent(String componentId) {
        String url = NexusUrlConstants.Components.DELETE_COMPONENTS + componentId;
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
        } catch (NexusResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.warn("nexus component has been deleted");
                return;
            } else {
                throw e;
            }
        }
        if (responseEntity.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            throw new CommonException(NexusApiConstants.ErrorMessage.COMPONENT_ID_ERROR);
        }
    }

    @Override
    public void deleteComponentScript(NexusComponentDeleteParam deleteParam) {
        AssertUtils.notNull(deleteParam.getRepositoryName(), "repositoryName not null");
        AssertUtils.notNull(deleteParam.getComponents(), "components not null");
        String param = JSONObject.toJSONString(deleteParam);
        NexusScriptResult nexusScriptResult = nexusScriptApi.runScript(NexusApiConstants.ScriptName.COMPONENT_DELETE, param);
    }

    @Override
    public void createMavenComponent(NexusServerComponentUpload componentUpload, NexusServer currentNexusServer) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put(NexusServerComponentUpload.REPOSITORY_NAME, componentUpload.getRepositoryName());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 上传文件类型
        List<String> extensionList = new ArrayList<>();
        for (int i = 0; i < componentUpload.getAssetUploads().size(); i++) {
            NexusServerAssetUpload assetUpload = componentUpload.getAssetUploads().get(i);
            body.add(NexusServerComponentUpload.ASSET_FILE.replace("{num}", String.valueOf(i + 1)), assetUpload.getAssetName());
            body.add(NexusServerComponentUpload.ASSET_EXTENSION.replace("{num}", String.valueOf(i + 1)), assetUpload.getExtension());
            extensionList.add(assetUpload.getExtension());
        }
        if (extensionList.contains(NexusServerAssetUpload.JAR)
                && !extensionList.contains(NexusServerAssetUpload.POM)) {
            body.add(NexusServerComponentUpload.GENERATE_POM, true);
        } else {
            body.add(NexusServerComponentUpload.GENERATE_POM, false);
        }

        if (!extensionList.contains(NexusServerAssetUpload.POM)) {
            AssertUtils.notNull(componentUpload.getGroupId(), "groupId not null");
            AssertUtils.notNull(componentUpload.getArtifactId(), "artifactId not null");
            AssertUtils.notNull(componentUpload.getVersion(), "version not null");
            body.add(NexusServerComponentUpload.GROUP_ID, componentUpload.getGroupId());
            body.add(NexusServerComponentUpload.ARTIFACT_ID, componentUpload.getArtifactId());
            body.add(NexusServerComponentUpload.VERSION, componentUpload.getVersion());
        }

        componentExceptHandler(paramMap, body, currentNexusServer);
    }

    @Override
    public void createNpmComponent(String repositoryName, InputStreamResource streamResource, NexusServer currentNexusServer) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put(NexusServerComponentUpload.REPOSITORY_NAME, repositoryName);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(NexusServerComponentUpload.NPM_TGX, streamResource);
        componentExceptHandler(paramMap, body, currentNexusServer);
    }

    @Override
    public NexusServerAsset findAsset(String repositoryName, String path) {
        AssetQuery assetQuery = new AssetQuery();
        assetQuery.setPath(path);
        assetQuery.setRepositoryName(repositoryName);
        NexusScriptResult nexusScriptResult = nexusScriptApi.runScript(NexusApiConstants.ScriptName.FIND_ASSET, JSONObject.toJSONString(assetQuery));
        String result = nexusScriptResult.getResult();
        NexusServerAsset nexusServerAsset = JSON.parseObject(result, NexusServerAsset.class);
        return nexusServerAsset;
    }

    private void componentExceptHandler(Map<String, Object> paramMap, MultiValueMap<String, Object> body, NexusServer currentNexusServer) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = nexusRequest.exchangeFormData(NexusUrlConstants.Components.UPLOAD_COMPONENTS, HttpMethod.POST, paramMap, body, currentNexusServer);
        } catch (NexusResponseException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String bodyStr = e.getMessage();
                if (bodyStr != null && bodyStr.contains("Repository does not allow updating assets")) {
                    throw new NexusResponseException(e.getStatusCode(), NexusApiConstants.ErrorMessage.REPO_NOT_UPDATE_ASSET);
                }
            }
            throw e;
        }
    }

    /**
     * maven包组件分组处理
     *
     * @param componentList 组件（包）信息
     * @return List<NexusServerComponentInfo>
     */
    private List<NexusServerComponentInfo> mavenComponentGroup(List<NexusServerComponent> componentList) {
        Map<String, NexusServerComponentInfo> componentInfoMap = new HashMap<>(16);
        for (NexusServerComponent component : componentList) {
            //String path = component.getGroup() + "/" + component.getName() + "/" + component.getUseVersion();
            String path = component.getPath();
            String key = component.getRepository() + path;
            if (componentInfoMap.get(key) == null) {
                NexusServerComponentInfo componentInfo = new NexusServerComponentInfo();
                BeanUtils.copyProperties(component, componentInfo, NexusServerComponentInfo.FIELD_CREATE_BY,
                        NexusServerComponentInfo.FIELD_CREATION_DATE, NexusServerComponentInfo.FIELD_LAST_DOWNLOAD_DATE);
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

            // 更新时间
            List<NexusServerComponent> dateList = components.stream().sorted(Comparator.comparing(NexusServerComponent::getLastUpdateDate).reversed()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dateList)) {
                componentInfo.setLastUpdateDate(dateList.get(0).getLastUpdateDate());
            }

            if (components.size() == 1) {
                NexusServerComponent nexusServerComponent = components.get(0);
                if (nexusServerComponent.getVersion().equals(nexusServerComponent.getUseVersion())) {
                    // 版本相同时，不用再有下级； 如 RELEASE 版本的jar包
                    componentInfo.setComponents(new ArrayList<>());
                    // RELEASE包  外层需要以下字段
                    componentInfo.setCreatedBy(nexusServerComponent.getCreatedBy());
                    componentInfo.setLastDownloadDate(nexusServerComponent.getLastDownloadDate());
                    componentInfo.setCreationDate(nexusServerComponent.getCreationDate());
                }
            }
            // 设置Id
            componentInfo.setComponentIds(components.stream().map(NexusServerComponent::getId).collect(Collectors.toList()));

        });
        componentInfoList = componentInfoList.stream().sorted(Comparator.comparing(NexusServerComponentInfo::getGroup).thenComparing(NexusServerComponentInfo::getName).thenComparing(NexusServerComponentInfo::getVersion).reversed()).collect(Collectors.toList());
        return componentInfoList;
    }

    /**
     * npm包组件分组处理
     *
     * @param componentList 组件（包）信息
     * @return List<NexusServerComponentInfo>
     */
    private List<NexusServerComponentInfo> npmComponentGroup(List<NexusServerComponent> componentList) {
        componentList = componentList.stream().filter(component -> CollectionUtils.isNotEmpty(component.getAssets())).collect(Collectors.toList());

        //List<NexusServerRepository> nexusServerRepositories = nexusRepositoryHttpApi.getRepository(null);
        //Map<String, NexusServerRepository> nexusServerRepositoryMap = nexusServerRepositories.stream().collect(Collectors.toMap(NexusServerRepository::getName, k -> k));


        Map<String, NexusServerComponentInfo> componentInfoMap = new HashMap<>(16);
        for (NexusServerComponent component : componentList) {
            if (component.getRepositoryUrl() != null) {
                component.setDownloadUrl(component.getRepositoryUrl() + "/" + component.getAssets().get(0).getPath());
            }


            component.setComponentIds(Collections.singletonList(component.getId()));


            String key = component.getName();
            if (componentInfoMap.get(key) == null) {
                NexusServerComponentInfo componentInfo = new NexusServerComponentInfo();
                BeanUtils.copyProperties(component, componentInfo, NexusServerComponentInfo.FIELD_CREATE_BY,
                        NexusServerComponentInfo.FIELD_CREATION_DATE, NexusServerComponentInfo.FIELD_LAST_DOWNLOAD_DATE);

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
            componentInfo.setVersion(null);
            List<NexusServerComponent> components = componentInfo.getComponents();

            // 更新时间
            List<NexusServerComponent> dateList = components.stream().sorted(Comparator.comparing(NexusServerComponent::getLastUpdateDate).reversed()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dateList)) {
                componentInfo.setLastUpdateDate(dateList.get(0).getLastUpdateDate());
            }


            componentInfo.setVersionCount(components.size());
            // 设置Id
            componentInfo.setComponentIds(components.stream().map(NexusServerComponent::getId).collect(Collectors.toList()));
            // 版本
            components = components.stream().sorted(Comparator.comparing(NexusServerComponent::getVersion).reversed()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(components)) {
                componentInfo.setNewestVersion(components.get(0).getVersion());
            }
        });

        return componentInfoList;
    }

    /**
     * 版本处理
     *
     * @param componentList 组件（包）信息
     */
    private void handleVersion(List<NexusServerComponent> componentList) {
        componentList = componentList.stream().filter(nexusServerComponent -> CollectionUtils.isNotEmpty(nexusServerComponent.getAssets())).collect(Collectors.toList());
        componentList.forEach(nexusComponent -> {
            List<NexusServerAsset> assetList = nexusComponent.getAssets();
            if (CollectionUtils.isNotEmpty(assetList)) {
                NexusServerAsset asset = assetList.get(0);
                nexusComponent.setUseVersion(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(asset.getPath(), "/"), "/"));

                // extension处理
                List<String> extensionList = new ArrayList<>();
                assetList.forEach(nexusServerAsset -> {
                    String lastPath = StringUtils.substringAfterLast(nexusServerAsset.getPath(), "/");
                    String prePath = nexusComponent.getName() + "-" + nexusComponent.getVersion() + ".";
                    String extension = StringUtils.substringAfterLast(lastPath, prePath);
                    nexusServerAsset.setExtension(extension);
                    extensionList.add(extension);
                });
                nexusComponent.setExtension(this.handleExtension(extensionList));

                nexusComponent.setPath(StringUtils.substringBeforeLast(asset.getPath(), "/"));
            }
            nexusComponent.setComponentIds(Collections.singletonList(nexusComponent.getId()));
            // assets 前端不需要，数据量太大
            nexusComponent.setAssets(new ArrayList<>());
        });
    }

    private String handleExtension(List<String> extensionList) {
        List<String> extensionLowerList = extensionList.stream().map(String::toLowerCase).collect(Collectors.toList());
        if (extensionLowerList.contains(NexusApiConstants.packageType.JAR)) {
            return NexusApiConstants.packageType.JAR;
        } else if (extensionLowerList.contains(NexusApiConstants.packageType.WAR)) {
            return NexusApiConstants.packageType.WAR;
        } else if (extensionLowerList.contains(NexusApiConstants.packageType.POM)) {
            return NexusApiConstants.packageType.POM;
        } else {
            return null;
        }
    }
}
