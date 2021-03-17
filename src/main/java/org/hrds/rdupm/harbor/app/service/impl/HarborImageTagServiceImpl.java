package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborImageTagService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborArtifactDTO;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborBuildLogDTO;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hrds.rdupm.util.ConvertUtil;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 1:44 下午
 */
@Service
public class HarborImageTagServiceImpl implements HarborImageTagService {

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Autowired
	private C7nBaseService c7nBaseService;

	@Override
	public Page<HarborImageTagVo> list(Long projectId,String repoName, String tagName, PageRequest pageRequest) {
		ResponseEntity<String> tagResponseEntity;
		List<HarborImageTagVo> harborImageTagVoList = new ArrayList<>();
		if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
			tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, null, null, true, repoName);
			harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(),new TypeToken<List<HarborImageTagVo>>(){}.getType());
		} else {
			String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
			tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, null, null, true, strArr[0], strArr[1]);
			harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(),new TypeToken<List<HarborImageTagVo>>(){}.getType());
		}

		if(StringUtils.isNotEmpty(tagName)){
			harborImageTagVoList = harborImageTagVoList.stream().filter(dto->dto.getTagName().equals(tagName)).collect(Collectors.toList());
		}

		harborImageTagVoList.stream().forEach(dto->{
			dto.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(dto.getSize())));
			dto.setPullTime(HarborConstants.DEFAULT_DATE.equals(dto.getPullTime()) ? null : dto.getPullTime());
			setTagAuthor(projectId,repoName,tagName,dto);
		});
		processImageLogList(harborImageTagVoList);
		Page<HarborImageTagVo> pageInfo = PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),harborImageTagVoList);
		return pageInfo;
	}

	/***
	 * 获取镜像推送者
	 * @param projectId
	 * @param repoName
	 * @param tagName
	 * @param tagVo
	 */
	public void setTagAuthor(Long projectId,String repoName, String tagName,HarborImageTagVo tagVo){
		HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		String harborProjectName = harborRepository.getCode();
		Map<String,Object> param = new HashMap<>(16);
		param.put("project_id",harborId);
		param.put("project_name",harborProjectName);
		param.put("repository",repoName);
		param.put("operation","push");
		if(StringUtils.isNotEmpty(tagName)){
			param.put("tag",tagName);
		}
		ResponseEntity<String> logsResponseEntity;
		if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
			logsResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, param, null, true, harborId);
		} else {
			logsResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, param, null, true, harborProjectName);
		}
		List<HarborImageLog> logListResult = new Gson().fromJson(logsResponseEntity.getBody(),new TypeToken<List<HarborImageLog>>(){}.getType());
		Map<String,List<HarborImageLog>> logListMap = logListResult.stream().collect(Collectors.groupingBy(dto->dto.getRepoName()+dto.getTagName()));

		List<HarborImageLog> logList = logListMap.get(repoName+tagVo.getTagName());
		if(CollectionUtils.isNotEmpty(logList)){
			tagVo.setAuthor(logList.get(0).getLoginName());
		}
	}

	public void processImageLogList(List<HarborImageTagVo> harborImageTagVoList){
		Set<String> userNameSet = harborImageTagVoList.stream().map(dto->dto.getAuthor()).collect(Collectors.toSet());
		Map<String,UserDTO> userDtoMap = c7nBaseService.listUsersByLoginNames(userNameSet);
		harborImageTagVoList.stream().forEach(dto->{
			String loginName = dto.getAuthor();
			UserDTO userDTO = userDtoMap.get(loginName);
			String realName = userDTO == null ? loginName : userDTO.getRealName();
			String userImageUrl = userDTO == null ? null : userDTO.getImageUrl();

			dto.setLoginName(loginName);
			dto.setRealName(realName);
			dto.setUserImageUrl(userImageUrl);
		});
	}

	@Override
	public String buildLog(String repoName, String tagName) {
		StringBuffer sb = new StringBuffer();

		Gson gson = new Gson();
		ResponseEntity<String> responseEntity;
		if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
			responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_IMAGE_BUILD_LOG, null, null, true, repoName, tagName);
			Map<String, Object> map = gson.fromJson(responseEntity.getBody(), Map.class);
			String config = (String) map.get("config");
			Map<String, Object> configMap = gson.fromJson(config, Map.class);
			List<Map<String, Object>> historyList = (List<Map<String, Object>>) configMap.get("history");
			for (Map<String, Object> history : historyList) {
				sb.append(history.get("created")).append("  ").append(history.get("created_by")).append("\n");
			}
		} else {
			responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_IMAGE_BUILD_LOG, null, null, true, repoName, tagName);
			List<HarborBuildLogDTO> buildLogDTOList = gson.fromJson(responseEntity.getBody(), List.class);
			buildLogDTOList.forEach(t -> {
				sb.append(t.getCreated()).append("  ").append(t.getCreatedBy()).append("\n");
			});
		}
		return sb.toString();
	}

	@Override
	public void delete(String repoName, String tagName) {
		if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE_TAG, null, null, true, repoName, tagName);
		} else {
			String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
			ResponseEntity<String> tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, null, null, true, repoName, strArr[0], strArr[1]);
			List<HarborArtifactDTO> artifactDTOList = new Gson().fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborArtifactDTO>>() {
			}.getType());
			artifactDTOList.stream().forEach(t -> {
				t.getTags().stream().forEach(tag -> {
					if (tag.getTagName().equals(tagName)) {
						harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE_TAG, null, null, true, strArr[0], strArr[1], t.getDigest(), tagName);
					}
				});
			});
		}
	}

	@Override
	public void copyTag(HarborImageReTag harborImageReTag) {
		if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
			String srcImage = harborImageReTag.getSrcRepoName() + BaseConstants.Symbol.COLON + harborImageReTag.getDigest();
			String destRepoName = harborImageReTag.getDestProjectCode() + BaseConstants.Symbol.SLASH + harborImageReTag.getDestImageName();
			Map<String, Object> bodyMap = new HashMap<>(3);
			bodyMap.put("override", true);
			bodyMap.put("tag", harborImageReTag.getDestImageTagName());
			bodyMap.put("src_image", srcImage);
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.COPY_IMAGE_TAG, null, bodyMap, true, destRepoName);
		} else {
			Map<String, Object> paramsMap = new HashMap<>(1);
			paramsMap.put("from", String.format("%s@%s", harborImageReTag.getSrcRepoName(), harborImageReTag.getDigest()));
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.COPY_IMAGE_TAG, paramsMap, null, true, harborImageReTag.getDestProjectCode(), harborImageReTag.getDestImageName());
		}
	}
}
