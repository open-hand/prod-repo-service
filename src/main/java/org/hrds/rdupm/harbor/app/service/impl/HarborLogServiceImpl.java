package org.hrds.rdupm.harbor.app.service.impl;


import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.choerodon.core.domain.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborLogService;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborLogRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
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

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Override
	public Page<HarborLog> listAuthLog(PageRequest pageRequest, HarborLog harborLog) {
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
			Map<Long,ProjectDTO> projectDtoMap = c7nBaseService.queryProjectByIds(projectIdSet);
			page.getContent().forEach(dto->{
				ProjectDTO projectDTO = projectDtoMap.get(dto.getProjectId());
				if(projectDTO != null){
					dto.setProjectCode(projectDTO.getCode());
					dto.setProjectName(projectDTO.getName());
					dto.setProjectImageUrl(projectDTO.getImageUrl());
				}
			});
		}

		return page;
	}

	@Override
	public Page<HarborImageLog> listImageLogByProject(PageRequest pageRequest, Long projectId, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Map<String,Object> paramMap = getParamMap(pageRequest,imageName,loginName,tagName,operateType,startDate,endDate);
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT,paramMap,null,true,harborRepository.getHarborId());
		List<HarborImageLog> dataList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageLog>>(){}.getType());
		List<HarborImageLog> harborImageLogList = dataList.stream().filter(dto->!HarborConstants.LOWER_CREATE.equals(dto.getOperateType()) ).collect(Collectors.toList());

		processImageLogList(harborImageLogList);
		return PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),harborImageLogList);
	}

	@Override
	public Page<HarborImageLog> listImageLogByOrg(PageRequest pageRequest, Long organizationId, String code, String name, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate) {
		Sqls sql = Sqls.custom().andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID,organizationId);
		if(!StringUtils.isEmpty(code)){
			sql.andEqualTo(HarborRepository.FIELD_CODE,code);
		}
		if(!StringUtils.isEmpty(name)){
			sql.andEqualTo(HarborRepository.FIELD_NAME,name);
		}
		Condition condition = Condition.builder(HarborRepository.class).where(sql).build();
		List<HarborRepository> harborRepositoryList = harborRepositoryRepository.selectByCondition(condition);
		if(CollectionUtils.isEmpty(harborRepositoryList)){
			PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),new ArrayList<>());
		}

		List<HarborImageLog> harborImageLogList = new ArrayList<>();
		for(HarborRepository harborRepository : harborRepositoryList){
			Map<String,Object> paramMap = getParamMap(pageRequest,imageName,loginName,tagName,operateType,startDate,endDate);
			ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT,paramMap,null,true,harborRepository.getHarborId());
			List<HarborImageLog> dataList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborImageLog>>(){}.getType());
			harborImageLogList.addAll(dataList.stream().filter(dto->!HarborConstants.LOWER_CREATE.equals(dto.getOperateType()) ).collect(Collectors.toList()));
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
		Set<String> userNameSet = harborImageLogList.stream().map(dto->dto.getLoginName()).collect(Collectors.toSet());
		Map<String,UserDTO> userDtoMap = c7nBaseService.listUsersByLoginNames(userNameSet);
		harborImageLogList.stream().forEach(dto->{
			String loginName = dto.getLoginName();
			UserDTO userDTO = userDtoMap.get(loginName);
			String realName = userDTO == null ? loginName : userDTO.getRealName();
			String operateTypeName = HarborConstants.HarborImageOperateEnum.getNameByValue(dto.getOperateType());
			String userImageUrl = userDTO == null ? null : userDTO.getImageUrl();

			dto.setUserImageUrl(userImageUrl);
			dto.setContent(String.format("%s(%s) %s 镜像【%s:%s】",realName,loginName,operateTypeName,dto.getRepoName(),dto.getTagName()));
		});
	}
}
