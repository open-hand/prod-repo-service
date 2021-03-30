package org.hrds.rdupm.harbor.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageScanResultVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageScanVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.operator.HarborClientOperator;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborAuthService harborAuthService;
	@Autowired
	private HarborClientOperator harborClientOperator;

	@Override
	public Page<HarborImageVo> getByProject(Long projectId, String imageName, PageRequest pageRequest) {
		Gson gson = new Gson();
		HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();

		//获得镜像数
		Integer totalSize = harborClientOperator.getRepoCountByHarborId(harborId);
		ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT, null, null, true, harborId);
		HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
		String repoName = harborProjectDTO == null ? null : harborProjectDTO.getName();
		if(totalSize <= 0){
			return PageConvertUtils.convert(pageRequest.getPage()+1, pageRequest.getSize(), new ArrayList<>());
		}

		List<HarborImageVo> harborImageVoList = getImageList(harborId,imageName,pageRequest,repoName);
		return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(),totalSize, harborImageVoList);
	}

	private List<HarborImageVo> getImageList(Long harborId, String imageName, PageRequest pageRequest, String projectCode) {
		Integer page = pageRequest.getPage() == 0 ? 1 : pageRequest.getPage() + 1;
		Integer pageSize = pageRequest.getSize();
		List<HarborImageVo> harborImageVoList = harborClientOperator.listImages(harborId, page, pageSize, imageName);
		harborImageVoList.forEach(dto -> {
			dto.setImageName(dto.getRepoName().substring(projectCode.length() + 1));
		});
		return harborImageVoList;
	}

	@Override
	public Page<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest) {
		Sqls sql = Sqls.custom().andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID,organizationId);
		if(!StringUtils.isEmpty(projectCode)){
			sql.andEqualTo(HarborRepository.FIELD_CODE,projectCode);
		}
		/*if(!StringUtils.isEmpty(projectName)){
			sql.andEqualTo(HarborRepository.FIELD_NAME,projectName);
		}*/
		Condition condition = Condition.builder(HarborRepository.class).where(sql).build();
		List<HarborRepository> projectList = harborRepositoryRepository.selectByCondition(condition);
		if(CollectionUtils.isEmpty(projectList)){
			return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), new ArrayList<>());
		}

		Set<Long> projectIdSet = projectList.stream().map(dto->dto.getProjectId()).collect(Collectors.toSet());
		Map<Long,ProjectDTO> projectDtoMap = c7nBaseService.queryProjectByIds(projectIdSet);

		//查询镜像列表
		Integer totalSize = 0;
		List<HarborImageVo> harborImageVoList = new ArrayList<>();
		for(HarborRepository harborRepository : projectList){
			List<HarborImageVo> dtoList = getImageList(harborRepository.getHarborId(),imageName,pageRequest,harborRepository.getCode());
			harborImageVoList.addAll(dtoList);
			dtoList.forEach(dto->{
				ProjectDTO projectDTO = projectDtoMap.get(harborRepository.getProjectId());
				dto.setProjectId(projectDTO.getId());
				dto.setProjectCode(projectDTO == null ? null : projectDTO.getCode());
				dto.setProjectName(projectDTO == null ? null : projectDTO.getName());
				dto.setProjectImageUrl(projectDTO == null ? null : projectDTO.getImageUrl());
			});
			totalSize += harborClientOperator.getRepoCountByHarborId(harborRepository.getHarborId());
		}
		Page<HarborImageVo> pageInfo = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), totalSize,harborImageVoList);
		return pageInfo;
	}

	@Override
	public void delete(HarborImageVo harborImageVo) {
		HarborRepository harborRepository = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class).where(Sqls.custom()
				.andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID, DetailsHelper.getUserDetails().getTenantId())
				.andEqualTo(HarborRepository.FIELD_HARBOR_ID,harborImageVo.getHarborId())
		).build()).stream().findFirst().orElse(null);
		if(harborRepository != null){
			harborAuthService.checkProjectAdmin(harborRepository.getProjectId());
		}
		String repoName = harborImageVo.getRepoName();
		if(StringUtils.isEmpty(repoName)){
			throw new CommonException("error.harbor.image.repoName.empty");
		}
		harborClientOperator.deleteImage(repoName);
	}

	@Override
	public void updateDesc(HarborImageVo harborImageVo) {
		String repoName = harborImageVo.getRepoName();
		if (StringUtils.isEmpty(repoName)) {
			throw new CommonException("error.harbor.image.repoName.empty");
		}
		harborClientOperator.updateImageDesc(harborImageVo);
	}

	@Override
	public void scanImages(List<HarborImageScanVO> imageScanVOList) {
		imageScanVOList.forEach(t -> harborClientOperator.scanImage(t));
	}

	@Override
	public List<HarborImageScanResultVO> queryImageScanDetail(HarborImageScanVO imageScanVO) {
		return  harborClientOperator.queryImageScanDetail(imageScanVO);
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
		HarborRepository harborRepository = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class).where(Sqls.custom()
				.andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID, DetailsHelper.getUserDetails().getTenantId())
				.andEqualTo(HarborRepository.FIELD_HARBOR_ID,harborImageVo.getHarborId())
		).build()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		String repoName = harborRepository.getCode()+ BaseConstants.Symbol.SLASH +harborImageVo.getImageName();
		return repoName;
	}

}
