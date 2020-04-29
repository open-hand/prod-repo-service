package org.hrds.rdupm.harbor.app.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.app.service.HarborLogService;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;
import org.hrds.rdupm.harbor.domain.repository.HarborLogRepository;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
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

	@Resource
	private BaseFeignClient baseFeignClient;

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
}
