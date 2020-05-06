package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborCountVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 3:07 下午
 */
@Service
public class HarborImageServiceImpl implements HarborImageService {

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Resource
	private BaseFeignClient baseFeignClient;

	@Override
	public PageInfo<HarborImageVo> getByProject(Long harborId, String imageName, PageRequest pageRequest) {
		Gson gson = new Gson();

		//获得镜像数
		ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT,null,null,false,harborId);
		HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
		Integer totalSize = harborProjectDTO.getRepoCount();
		String repoName = harborProjectDTO.getName();
		if(totalSize <= 0){
			return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), new ArrayList<>());
		}

		List<HarborImageVo> harborImageVoList = getImageList(harborId,imageName,pageRequest,repoName);
		PageInfo<HarborImageVo> pageInfo = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), harborImageVoList);
		return pageInfo;
	}

	private List<HarborImageVo> getImageList(Long harborId, String imageName, PageRequest pageRequest,String projectCode){
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("project_id",harborId);
		paramMap.put("q",imageName);
		paramMap.put("page",pageRequest.getPage()==0?1:pageRequest.getPage());
		paramMap.put("page_size",pageRequest.getSize());
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE,paramMap,null,true);
		List<HarborImageVo> harborImageVoList = new ArrayList<>();
		if(responseEntity != null && !StringUtils.isEmpty(responseEntity.getBody())){
			harborImageVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageVo>>(){}.getType());
		}
		harborImageVoList.forEach(dto->dto.setImageName(dto.getRepoName().substring(projectCode.length()+1)));
		return harborImageVoList;
	}

	@Override
	public PageInfo<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest) {
		Sqls sql = Sqls.custom().andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID,organizationId);
		if(!StringUtils.isEmpty(projectCode)){
			sql.andEqualTo(HarborRepository.FIELD_CODE,projectCode);
		}
		if(!StringUtils.isEmpty(projectName)){
			sql.andLike(HarborRepository.FIELD_NAME,projectName);
		}
		Condition condition = Condition.builder(HarborRepository.class).where(sql).build();
		Page<HarborRepository> page = PageHelper.doPageAndSort(pageRequest, () -> harborRepositoryRepository.selectByCondition(condition));
		List<HarborRepository> projectList = page.getContent();
		if(CollectionUtils.isEmpty(projectList)){
			return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), new ArrayList<>());
		}
		//获得镜像头像
		Set<Long> projectIdSet = projectList.stream().map(dto->dto.getProjectId()).collect(Collectors.toSet());
		ResponseEntity<List<ProjectDTO>> projectResponseEntity = baseFeignClient.queryByIds(projectIdSet);
		Map<Long,ProjectDTO> projectDtoMap = projectResponseEntity == null ? new HashMap<>(1) : projectResponseEntity.getBody().stream().collect(Collectors.toMap(ProjectDTO::getId,dto->dto));

		//查询镜像列表
		List<HarborImageVo> harborImageVoList = new ArrayList<>();
		for(HarborRepository harborRepository : projectList){
			List<HarborImageVo> dtoList = getImageList(harborRepository.getHarborId(),imageName,pageRequest,harborRepository.getCode());
			dtoList.forEach(dto->{
				ProjectDTO projectDTO = projectDtoMap.get(harborRepository.getProjectId());
				if(projectDTO == null){
					dto.setProjectName(projectDTO.getName());
					dto.setProjectImageUrl(projectDTO.getImageUrl());
				}
			});
			harborImageVoList.addAll(dtoList);
		}
		PageInfo<HarborImageVo> pageInfo = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), harborImageVoList);
		return pageInfo;
	}

	@Override
	public void delete(HarborImageVo harborImageVo) {
		String repoName = harborImageVo.getRepoName();
		if(StringUtils.isEmpty(repoName)){
			throw new CommonException("error.harbor.image.repoName.empty");
		}
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE,null,null,false,repoName);
	}

	@Override
	public void updateDesc(HarborImageVo harborImageVo) {
		String repoName = harborImageVo.getRepoName();
		if(StringUtils.isEmpty(repoName)){
			throw new CommonException("error.harbor.image.repoName.empty");
		}
		Map<String,String> bodyMap = new HashMap<>();
		bodyMap.put("description",harborImageVo.getDescription());
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_IMAGE_DESC,null,bodyMap,false,repoName);
	}

	/***
	 * 获取镜像仓库名+"/"+镜像名
	 * @param harborImageVo
	 * @return
	 */
	private String getRepoName(HarborImageVo harborImageVo){
		if(harborImageVo.getHarborId() == null || StringUtils.isEmpty(harborImageVo.getImageName())){
			throw new CommonException("error.harbor.image.param.empty");
		}
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_HARBOR_ID,harborImageVo.getHarborId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		String repoName = harborRepository.getCode()+ BaseConstants.Symbol.SLASH +harborImageVo.getImageName();
		return repoName;
	}

}
