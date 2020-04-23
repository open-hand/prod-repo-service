package org.hrds.rdupm.harbor.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborCountVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
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

	@Override
	public PageInfo<HarborImageVo> getByProject(Long harborId, String imageName, PageRequest pageRequest) {
		Gson gson = new Gson();

		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("project_id",harborId);
		paramMap.put("q",imageName);
		paramMap.put("page",pageRequest.getPage()==0?1:pageRequest.getPage());
		paramMap.put("page_size",pageRequest.getSize());
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE,paramMap,null,false);
		List<HarborImageVo> harborImageVoList = new ArrayList<>();
		if(responseEntity != null && !StringUtils.isEmpty(responseEntity.getBody())){
			harborImageVoList = gson.fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageVo>>(){}.getType());
		}

		//获得镜像数
		ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT,null,null,false,harborId);
		HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
		Integer totalSize = harborProjectDTO.getRepoCount();

		return new PageInfo(harborImageVoList,totalSize);
	}

	/**
	* 组织层--镜像分页，无法获得组织下所有镜像数量
	* 组织层--组织管理员没有权限查看镜像，怎么分配权限，组织管理员角色中人员也是变动的
	* 组织层--
	* */
	@Override
	public PageInfo<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest) {
		Gson gson = new Gson();

		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.COUNT,null,null,true);
		HarborCountVo harborCountVo = gson.fromJson(responseEntity.getBody(), HarborCountVo.class);
		Integer totalSize = harborCountVo.getPublicRepoCount() + harborCountVo.getPrivateRepoCount();

		Map<String,Object> paramMap = new HashMap<>();
		//paramMap.put("project_id",harborId);
		paramMap.put("q",imageName);
		paramMap.put("page",pageRequest.getPage()==0?1:pageRequest.getPage());
		paramMap.put("page_size",pageRequest.getSize());
		ResponseEntity<String> dataResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE,paramMap,null,false);
		List<HarborImageVo> harborImageVoList = new ArrayList<>();
		if(dataResponseEntity != null && !StringUtils.isEmpty(dataResponseEntity.getBody())){
			harborImageVoList = gson.fromJson(dataResponseEntity.getBody(),new TypeToken<List<HarborImageVo>>(){}.getType());
		}

		return new PageInfo(harborImageVoList,totalSize);
	}
}
