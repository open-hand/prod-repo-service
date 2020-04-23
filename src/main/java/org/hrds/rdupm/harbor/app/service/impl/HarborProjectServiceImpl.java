package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.dto.User;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hzero.core.util.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 10:54 上午
 */
@Service
public class HarborProjectServiceImpl implements HarborProjectService {

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Override
	public void create(Long projectId, HarborProjectVo harborProjectVo) {
		/*
		* 1.判断Harbor中是否存在当前用户
		* 2.获取当前用户登录名，调用猪齿鱼接口获取用户基本信息，新增用户到harbor
		* 3.根据projectId获取猪齿鱼项目信息，得到项目编码、组织ID
		* 4.创建harbor项目，存储容量、安全级别、其他配置等
		* 5.数据库保存harbor项目，并关联猪齿鱼ID
		* */
		//TODO 当前用户登录名
		String userName = "15367";

		//校验项目是否已经存在、校验数据正确性
		if(CollectionUtils.isNotEmpty(harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId))){
			throw new CommonException("error.harbor.project.exist");
		}
		check(harborProjectVo);

		//判断是否存在当前用户
		Map<String,Object> paramMap = new HashMap<>(1);
		paramMap.put("username",userName);
		ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
		List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);

		//新增用户到Harbor
		if(CollectionUtils.isEmpty(userList)){
			ResponseEntity<UserDTO> userDTOResponseEntity = baseFeignClient.query(userName);
			UserDTO userDTO = userDTOResponseEntity.getBody();
			User user = new User(userDTO.getLoginName(),userDTO.getEmail(),HarborConstants.DEFAULT_PASSWORD,userDTO.getRealName());
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER,null,user,true);
		}

		//获取猪齿鱼项目信息
		ResponseEntity<ProjectDTO> projectDTOResponseEntity = baseFeignClient.query(projectId);
		ProjectDTO projectDTO = projectDTOResponseEntity.getBody();
		String code = projectDTO.getCode();

		//创建Harbor项目
		HarborProjectDTO harborProjectDTO = new HarborProjectDTO(harborProjectVo);
		harborProjectDTO.setName(code);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_PROJECT,null,harborProjectDTO,false);

		//查询harbor-id
		Integer harborId = null;
		Map<String,Object> paramMap2 = new HashMap<>(3);
		paramMap2.put("name",code);
		paramMap2.put("public",harborProjectVo.getPublicFlag());
		paramMap2.put("owner",userName);
		ResponseEntity<String> projectResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_PROJECT,paramMap,null,false);
		List<String> projectList= JSONObject.parseArray(projectResponse.getBody(),String.class);
		Gson gson = new Gson();
		for(String object : projectList){
			HarborProjectDTO projectResponseDto = gson.fromJson(object, HarborProjectDTO.class);
			if(code.equals(projectResponseDto.getName())){
				harborId = projectResponseDto.getProjectId();
				break;
			}
		}
		if(harborId == null){
			throw new CommonException("error.harbor.get.harborId");
		}
		saveWhiteList(harborProjectVo,harborProjectDTO,harborId);

		//保存数据库
		HarborRepository harborRepository = new HarborRepository(projectDTO.getId(),projectDTO.getCode(),projectDTO.getName(),harborProjectVo.getPublicFlag(),new Long(harborId),projectDTO.getOrganizationId());
		harborRepositoryRepository.insertSelective(harborRepository);
	}

	@Override
	public HarborProjectVo detail(Long harborId) {
		Gson gson = new Gson();
		ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT,null,null,false,harborId);
		HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
		HarborProjectVo harborProjectVo = new HarborProjectVo(harborProjectDTO);

		//获取存储容量
		ResponseEntity<String> summaryResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_SUMMARY,null,null,false,harborId);
		Map<String,Object> summaryMap = gson.fromJson(summaryResponseEntity.getBody(),Map.class);
		Map<String,Object> quotaMap = (Map<String, Object>) summaryMap.get("quota");
		Map<String,Object> hardMap = (Map<String, Object>) quotaMap.get("hard");
		Map<String,Object> usedMap = (Map<String, Object>) quotaMap.get("used");
		Double hardCount = (Double) hardMap.get("count");
		Double hardStorage = (Double) hardMap.get("storage");
		Double usedCount = (Double) usedMap.get("count");
		Double usedStorage = (Double) usedMap.get("storage");

		harborProjectVo.setCountLimit(Double.valueOf(hardCount).intValue());
		harborProjectVo.setUsedCount(Double.valueOf(usedCount).intValue());
		harborProjectVo.setStorageLimit(Double.valueOf(hardStorage).intValue());
		harborProjectVo.setUsedStorage(Double.valueOf(usedStorage).intValue());

		Map<String,Object> storageLimitMap = HarborUtil.getStorageNumUnit(Double.valueOf(hardStorage).intValue());
		harborProjectVo.setStorageNum((Integer) storageLimitMap.get("storageNum"));
		harborProjectVo.setStorageUnit((String) storageLimitMap.get("storageUnit"));
		Map<String,Object> usedStorageMap = HarborUtil.getStorageNumUnit(Double.valueOf(usedStorage).intValue());
		harborProjectVo.setUsedStorageNum((Integer) usedStorageMap.get("storageNum"));
		harborProjectVo.setUsedStorageUnit((String) usedStorageMap.get("storageUnit"));

		//获取镜像仓库名称
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_HARBOR_ID,harborId).stream().findFirst().orElse(null);
		harborProjectVo.setName(harborRepository == null ? null : harborRepository.getName());

		return harborProjectVo;
	}

	@Override
	public void update(Long projectId, HarborProjectVo harborProjectVo) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.notexist");
		}
		Long harborId = harborRepository.getHarborId();

		/**
		* 1.校验数据必输性
		* 2.更新harbor项目元数据
	    * 3.更新项目资源配额
	    * 4.更新项目白名单
	    * 5.更新数据库项目
		* */
		check(harborProjectVo);

		HarborProjectDTO harborProjectDTO = new HarborProjectDTO(harborProjectVo);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT,null,harborProjectDTO,false,harborId);

		Integer storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(),harborProjectVo.getStorageUnit());
		Map<String,Object> qutoaObject = new HashMap<>(1);
		Map<String,Object> hardObject = new HashMap<>(2);
		hardObject.put("count",harborProjectVo.getCountLimit());
		hardObject.put("storage",storageLimit);
		qutoaObject.put("hard",hardObject);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT_QUOTA,null,qutoaObject,false,harborId);

		saveWhiteList(harborProjectVo,harborProjectDTO,harborId.intValue());

		if(!harborRepository.getPublicFlag().equals(harborProjectVo.getPublicFlag())){
			harborRepository.setPublicFlag(harborProjectVo.getPublicFlag());
			harborRepositoryRepository.updateByPrimaryKeySelective(harborRepository);
		}
	}

	@Override
	public List<HarborRepository> listByProject(Long projectId) {
		List<HarborRepository> harborRepositoryList = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId);
		processHarborRepositoryList(harborRepositoryList);
		return harborRepositoryList;
	}

	@Override
	public List<HarborRepository> listByOrg(Long organizationId) {
		List<HarborRepository> harborRepositoryList = harborRepositoryRepository.select(HarborRepository.FIELD_ORGANIZATION_ID,organizationId);
		processHarborRepositoryList(harborRepositoryList);
		return harborRepositoryList;
	}

	/***
	 * 处理镜像仓库列表：查询镜像数、获得创建人登录名、真实名称、创建人头像
	 * @param harborRepositoryList
	 */
	private void processHarborRepositoryList(List<HarborRepository> harborRepositoryList){
		if(CollectionUtils.isEmpty(harborRepositoryList)){
			return;
		}

		//创建人ID去重，并获得创建人详细信息
		Set<Long> userIdSet = harborRepositoryList.stream().map(dto->dto.getCreatedBy()).collect(Collectors.toSet());
		ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]),true);
		if(responseEntity == null){
			throw new CommonException("error.feign.user.select.empty");
		}
		Map<Long,UserDTO> userDtoMap = responseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getId,dto->dto));

		harborRepositoryList.forEach(dto->{
			//获得镜像数
			ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT,null,null,false,dto.getHarborId());
			HarborProjectDTO harborProjectDTO = new Gson().fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
			dto.setRepoCount(harborProjectDTO.getRepoCount());

			//设置创建人登录名、真实名称、创建人头像
			UserDTO userDTO = userDtoMap.get(dto.getCreatedBy());
			if(userDTO != null){
				dto.setCreatorImageUrl(userDTO.getImageUrl());
				dto.setCreatorLoginName(userDTO.getLoginName());
				dto.setCreatorRealName(userDTO.getRealName());
			}
		});
	}

	/***
	 * 保存cve白名单
	 * @param harborProjectVo
	 * @param harborProjectDTO
	 * @param harborId
	 */
	private void saveWhiteList(HarborProjectVo harborProjectVo,HarborProjectDTO harborProjectDTO,Integer harborId){
		if(HarborConstants.TRUE.equals(harborProjectVo.getUseProjectCveFlag())){
			Map<String,Object> map = new HashMap<>(4);
			List<Map<String,String >> cveMapList = new ArrayList<>();
			for(String cve : harborProjectVo.getCveNoList()){
				Map<String,String> cveMap = new HashMap<>(2);
				cveMap.put("cve_id",cve);
				cveMapList.add(cveMap);
			}
			map.put("items",cveMapList);
			map.put("expires_at",HarborUtil.dateToTimestamp(harborProjectVo.getEndDate()));
			map.put("project_id",harborId);
			map.put("id",1);
			harborProjectDTO.setCveWhiteList(map);
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT,null,harborProjectDTO,false,harborId);
		}
	}

	private void check(HarborProjectVo harborProjectVo){
		AssertUtils.notNull(harborProjectVo.getPublicFlag(),"error.harbor.publicFlag.empty");
		AssertUtils.notNull(harborProjectVo.getCountLimit(),"error.harbor.CountLimit.empty");
		AssertUtils.notNull(harborProjectVo.getStorageNum(),"error.harbor.StorageNum.empty");
		AssertUtils.notNull(harborProjectVo.getStorageUnit(),"error.harbor.StorageUnit.empty");
		AssertUtils.notNull(harborProjectVo.getContentTrustFlag(),"error.harbor.ContentTrustFlag.empty");
		AssertUtils.notNull(harborProjectVo.getAutoScanFlag(),"error.harbor.AutoScanFlag.empty");
		AssertUtils.notNull(harborProjectVo.getPreventVulnerableFlag(),"error.harbor.PreventVulnerableFlag.empty");
		AssertUtils.notNull(harborProjectVo.getSeverity(),"error.harbor.Severity.empty");
	}

}
