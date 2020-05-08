package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborAuthMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 制品库-harbor权限表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
@Service
public class HarborAuthServiceImpl implements HarborAuthService {

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborAuthRepository repository;

	@Resource
	private HarborAuthMapper harborAuthMapper;

	@Resource
	private TransactionalProducer transactionalProducer;

	@Override
	@OperateLog(operateType = HarborConstants.ASSIGN_AUTH,content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
	@Saga(code = HarborConstants.HarborSagaCode.CREATE_AUTH,description = "分配权限",inputSchemaClass = List.class)
	public void save(Long projectId,List<HarborAuth> dtoList) {
		if(CollectionUtils.isEmpty(dtoList)){
			throw new CommonException("error.harbor.auth.param.empty");
		}
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}

		//校验是否已分配权限
		List<HarborAuth> existList = repository.select(HarborAuth.FIELD_PROJECT_ID,dtoList.get(0).getProjectId());
		Map<Long,HarborAuth> harborAuthMap = CollectionUtils.isEmpty(existList) ? new HashMap<>(1) : existList.stream().collect(Collectors.toMap(HarborAuth::getUserId,dto->dto));

		//设置loginName、realName
		Set<Long> userIdSet = dtoList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]),true);
		Map<Long,UserDTO> userDtoMap = userDtoResponseEntity == null ? new HashMap<>(1) : userDtoResponseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getId,dto->dto));

		dtoList.forEach(dto->{
			UserDTO userDTO = userDtoMap.get(dto.getUserId());
			dto.setLoginName(userDTO == null ? null : userDTO.getLoginName());
			dto.setRealName(userDTO == null ? null : userDTO.getRealName());

			if(harborAuthMap.get(dto.getUserId()) != null){
				throw new CommonException("error.harbor.auth.already.exist",dto.getLoginName(),dto.getRealName());
			}

			dto.setProjectId(projectId);
			dto.setOrganizationId(harborRepository.getOrganizationId());
			dto.setHarborId(harborRepository.getHarborId());
			dto.setHarborRoleValue(dto.getHarborRoleValue());
		});

		transactionalProducer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(HarborConstants.HarborSagaCode.CREATE_AUTH)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("dockerRepo")
						.withSourceId(projectId),
				startSagaBuilder -> startSagaBuilder.withPayloadAndSerialize(dtoList).withSourceId(projectId)
		);
	}

	@Override
	@OperateLog(operateType = HarborConstants.UPDATE_AUTH,content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】")
	@Transactional(rollbackFor = Exception.class)
	public void update(HarborAuth harborAuth) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		harborAuth.setHarborRoleId(HarborConstants.HarborRoleEnum.getIdByValue(harborAuth.getHarborRoleValue()));
		repository.updateByPrimaryKey(harborAuth);

		Map<String,Object> bodyMap = new HashMap<>(2);
		bodyMap.put("role_id",harborAuth.getHarborRoleId());
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_ONE_AUTH,null,bodyMap,true,harborId,harborAuth.getHarborAuthId());
	}

	@Override
	public Page<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth) {
		Page<HarborAuth> page = PageHelper.doPageAndSort(pageRequest,()->harborAuthMapper.list(harborAuth));
		List<HarborAuth> dataList = page.getContent();
		if(CollectionUtils.isEmpty(dataList)){
			return page;
		}

		//分项目查询成员角色
		Map<Long,List<HarborAuth>> dataListMap = dataList.stream().collect(Collectors.groupingBy(HarborAuth::getProjectId));
		Map<Long,UserWithGitlabIdDTO> userDtoMap = new HashMap<>(16);
		for(Long projectId : dataListMap.keySet()){
			Set<Long> userIdSet = dataListMap.get(projectId).stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
			ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
			userDtoMap.putAll(responseEntity.getBody().stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId,dto->dto)));
		}

		dataList.forEach(dto->{
			dto.setHarborRoleValueById(dto.getHarborRoleId());
			UserWithGitlabIdDTO userDto = userDtoMap.get(dto.getUserId());
			if(userDto != null){
				dto.setRealName(userDto.getRealName());
				dto.setUserImageUrl(userDto.getImageUrl());

				List<RoleDTO> roleDTOList = userDto.getRoles();
				if(CollectionUtils.isNotEmpty(roleDTOList)){
					StringBuffer memberRole = new StringBuffer();
					for(RoleDTO roleDTO : roleDTOList){
						memberRole.append(roleDTO.getName()).append(" ");
					}
					dto.setMemberRole(memberRole.toString());
				}
			}
		});

		return page;
	}

	@Override
	@OperateLog(operateType = HarborConstants.REVOKE_AUTH,content = "%s 删除 %s 的权限角色 【%s】")
	@Transactional(rollbackFor = Exception.class)
	public void delete(HarborAuth harborAuth) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		repository.deleteByPrimaryKey(harborAuth);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_ONE_AUTH,null,null,false,harborId,harborAuth.getHarborAuthId());
	}

	@Override
	@ExcelExport(HarborAuth.class)
	public Page<HarborAuth> export(PageRequest pageRequest, HarborAuth harborAuth, ExportParam exportParam, HttpServletResponse response) {
		Page<HarborAuth> page = this.pageList(pageRequest,harborAuth);
		return page;
	}
}
