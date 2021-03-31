package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborImageTagService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborBuildLogDTO;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.operator.HarborClientOperator;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 1:44 下午
 */
@Service
public class HarborImageTagServiceImpl implements HarborImageTagService {

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborClientOperator harborClientOperator;

	@Override
	public Page<HarborImageTagVo> list(Long projectId, String repoName, String tagName, PageRequest pageRequest) {
		List<HarborImageTagVo> harborImageTagVoList = harborClientOperator.listImageTags(repoName);
		Page<HarborImageTagVo> pageInfo;
		if (StringUtils.isNotEmpty(tagName)) {
			harborImageTagVoList = harborImageTagVoList.stream().filter(dto -> dto.getTagName().contains(tagName)).collect(Collectors.toList());
		}
		if (CollectionUtils.isEmpty(harborImageTagVoList)) {
			return new Page<>();
		}
		harborImageTagVoList.forEach(t -> setTagAuthor(projectId, repoName, tagName, t));
		setAuthorWithIam(harborImageTagVoList);
		pageInfo = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), harborImageTagVoList);
		return pageInfo;
	}

	/***
	 * 获取镜像推送者
	 * @param projectId
	 * @param repoName
	 * @param tagName
	 * @param tagVo
	 */
	public void setTagAuthor(Long projectId, String repoName, String tagName, HarborImageTagVo tagVo) {
		HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
		if (harborRepository == null) {
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		String harborProjectName = harborRepository.getCode();
		Map<String, Object> param = new HashMap<>(16);
		param.put("project_id", harborId);
		param.put("project_name", harborProjectName);
		param.put("repository", repoName);
		param.put("operation", "push");
		param.put("q", "operation=create");
		if (StringUtils.isNotEmpty(tagName)) {
			param.put("tag", tagName);
		}
		List<HarborImageLog> logListResult = harborClientOperator.listImageLogs(param, harborRepository);
		Map<String, List<HarborImageLog>> logListMap = logListResult.stream().collect(Collectors.groupingBy(dto -> dto.getRepoName() + dto.getTagName()));
		tagVo.getTags().forEach(t -> {
			List<HarborImageLog> logList = logListMap.get(repoName + t.getName());
			if (CollectionUtils.isNotEmpty(logList)) {
				t.setAuthor(logList.get(0).getLoginName());
			}
		});
	}

	public void setAuthorWithIam(List<HarborImageTagVo> harborImageTagVoList) {
		Set<String> userNameSet = new HashSet<>();
		harborImageTagVoList.forEach(dto -> dto.getTags().forEach(tag -> userNameSet.add(tag.getAuthor())));
		Map<String,UserDTO> userDtoMap = c7nBaseService.listUsersByLoginNames(userNameSet);
		harborImageTagVoList.forEach(dto->{
			dto.getTags().forEach(tag -> {
				String loginName = tag.getAuthor();
				UserDTO userDTO = userDtoMap.get(loginName);
				String realName = userDTO == null ? loginName : userDTO.getRealName();
				String userImageUrl = userDTO == null ? null : userDTO.getImageUrl();
				tag.setLoginName(loginName);
				tag.setRealName(realName);
				tag.setUserImageUrl(userImageUrl);
			});
		});
	}

	@Override
	public String buildLog(String repoName, String tagName, String digest) {
		StringBuffer sb = new StringBuffer();
		List<HarborBuildLogDTO> buildLogDTOList = harborClientOperator.listBuildLogs(repoName, tagName, digest);
		buildLogDTOList.forEach(t -> {
			sb.append(t.getCreated()).append("  ").append(t.getCreatedBy()).append("\n");
		});
		return sb.toString();
	}

	@Override
	public void delete(String repoName, String tagName) {
		harborClientOperator.deleteImageByTag(repoName, tagName);
	}

	@Override
	public void copyTag(HarborImageReTag harborImageReTag) {
		harborClientOperator.copyTag(harborImageReTag);
	}
}
