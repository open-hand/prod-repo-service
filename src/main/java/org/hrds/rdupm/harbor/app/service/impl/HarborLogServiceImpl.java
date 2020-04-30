package org.hrds.rdupm.harbor.app.service.impl;


import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.app.service.HarborLogService;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborLogRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
/**
 * 制品库-harbor日志表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-04-29 14:54:57
 */
@Service
public class HarborLogServiceImpl implements HarborLogService {
	@Autowired
	private HarborLogRepository repository;

	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Override
	public PageInfo<HarborLog> listAuthLog(PageRequest pageRequest, HarborLog harborLog) {
		Sqls sqls = Sqls.custom();
		if(harborLog.getProjectId() != null){
			sqls.andEqualTo(HarborLog.FIELD_PROJECT_ID,harborLog.getProjectId());
		}
		if(harborLog.getOrganizationId() != null){
			sqls.andEqualTo(HarborLog.FIELD_ORGANIZATION_ID,harborLog.getOrganizationId());
		}
		if(harborLog.getOperateType() != null){
			sqls.andEqualTo(HarborLog.FIELD_OPERATE_TYPE,harborLog.getOperateType());
		}
		if(harborLog.getContent() != null){
			sqls.andLike(HarborLog.FIELD_CONTENT,harborLog.getContent());
		}
		if(harborLog.getStartDate() != null){
			sqls.andGreaterThanOrEqualTo(HarborLog.FIELD_OPERATE_TIME,harborLog.getStartDate());
		}
		if(harborLog.getEndDate() != null){
			sqls.andLessThanOrEqualTo(HarborLog.FIELD_OPERATE_TIME,harborLog.getEndDate());
		}
		Condition condition = Condition.builder(HarborLog.class).where(sqls).build();
		Page<HarborLog> page = PageHelper.doPageAndSort(pageRequest,()->repository.selectByCondition(condition));

		if(harborLog.getOrganizationId() != null){
			Set<Long> projectIdSet = page.getContent().stream().map(dto->dto.getProjectId()).collect(Collectors.toSet());
			ResponseEntity<List<ProjectDTO>> projectResponseEntity = baseFeignClient.queryByIds(projectIdSet);
			Map<Long,ProjectDTO> projectDtoMap = projectResponseEntity == null ? new HashMap<>(1) : projectResponseEntity.getBody().stream().collect(Collectors.toMap(ProjectDTO::getId, dto->dto));
			page.getContent().forEach(dto->{
				ProjectDTO projectDTO = projectDtoMap.get(dto.getProjectId());
				if(projectDTO != null){
					dto.setProjectCode(projectDTO.getCode());
					dto.setProjectName(projectDTO.getName());
					dto.setProjectImageUrl(projectDTO.getImageUrl());
				}
			});
		}

		return PageConvertUtils.convert(page);
	}

	@Override
	public PageInfo<HarborImageLog> listImageLog(PageRequest pageRequest, Long projectId, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Map<String,Object> paramMap = getParamMap(pageRequest,imageName,loginName,tagName,operateType,startDate,endDate);
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT,paramMap,null,false,harborRepository.getHarborId());
		List<HarborImageLog> dataList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageLog>>(){}.getType());
		List<HarborImageLog> harborImageLogList = dataList.stream().filter(dto->!"create".equals(dto.getOperateType()) ).collect(Collectors.toList());

		processImageLogList(harborImageLogList);
		return PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),harborImageLogList);
	}

	@Override
	public PageInfo<HarborImageLog> listImageLogByOrg(PageRequest pageRequest, Long organizationId, String code, String name, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate) {
		List<HarborRepository> harborRepositoryList = harborRepositoryRepository.select(HarborRepository.FIELD_ORGANIZATION_ID,organizationId);
		if(CollectionUtils.isEmpty(harborRepositoryList)){
			throw new CommonException("error.harbor.project.not.exist");
		}
		List<HarborImageLog> harborImageLogList = new ArrayList<>();
		for(HarborRepository harborRepository : harborRepositoryList){
			Map<String,Object> paramMap = getParamMap(pageRequest,imageName,loginName,tagName,operateType,startDate,endDate);
			ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT,paramMap,null,false,harborRepository.getHarborId());
			List<HarborImageLog> dataList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageLog>>(){}.getType());
			harborImageLogList.addAll(dataList.stream().filter(dto->!"create".equals(dto.getOperateType()) ).collect(Collectors.toList()));

		}
		processImageLogList(harborImageLogList);

		return PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),harborImageLogList);
	}

	public Map<String,Object> getParamMap(PageRequest pageRequest, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate){
		Map<String,Object> paramMap = new HashMap<>(8);
		if(StringUtils.isNotEmpty(loginName)){
			paramMap.put("username",loginName);
		}
		if(StringUtils.isNotEmpty(imageName)){
			paramMap.put("repository",imageName);
		}
		if(StringUtils.isNotEmpty(tagName)){
			paramMap.put("tag",tagName);
		}
		if(StringUtils.isNotEmpty(operateType)){
			paramMap.put("operation",operateType);
		}
		if(startDate != null){
			paramMap.put("begin_timestamp", HarborUtil.dateToTimestamp(startDate));
		}
		if(endDate != null){
			paramMap.put("end_timestamp",HarborUtil.dateToTimestamp(endDate));
		}
		paramMap.put("page",pageRequest.getPage() == 0 ? 1 : pageRequest.getPage());
		paramMap.put("page_size",pageRequest.getSize());
		return paramMap;
	}

	public void processImageLogList(List<HarborImageLog> harborImageLogList){
		//创建人ID去重，并获得创建人详细信息
		Set<String> userNameSet = harborImageLogList.stream().map(dto->dto.getLoginName()).collect(Collectors.toSet());
		ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByLoginNames(userNameSet.toArray(new String[userNameSet.size()]),true);
		Map<String,UserDTO> userDtoMap = userDtoResponseEntity == null ? new HashMap<>(1) : userDtoResponseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getLoginName,dto->dto));
		harborImageLogList.stream().forEach(dto->{
			UserDTO userDTO = userDtoMap.get(dto.getLoginName());
			if(userDTO != null){
				dto.setUserImageUrl(userDTO.getImageUrl());
				dto.setContent(String.format("%s(%s) %s 镜像【%s:%s】",userDTO.getRealName(),userDTO.getLoginName(),HarborConstants.HarborImageOperateEnum.getNameByValue(dto.getOperateType()),dto.getRepoName(),dto.getTagName()));
			}
		});
	}
}
