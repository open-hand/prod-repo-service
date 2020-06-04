package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
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

	@Autowired
	private C7nBaseService c7nBaseService;

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
		checkProjectAdmin(projectId);
		if(CollectionUtils.isEmpty(dtoList)){
			throw new CommonException("error.harbor.auth.param.empty");
		}
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}

		//校验是否已分配权限
		List<HarborAuth> existList = repository.select(HarborAuth.FIELD_PROJECT_ID,projectId);
		Map<Long,HarborAuth> harborAuthMap = CollectionUtils.isEmpty(existList) ? new HashMap<>(1) : existList.stream().collect(Collectors.toMap(HarborAuth::getUserId,dto->dto));

		Set<Long> userIdSet = dtoList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		Map<Long,UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
		dtoList.forEach(dto->{
			UserDTO userDTO = userDtoMap.get(dto.getUserId());
			dto.setLoginName(userDTO == null ? null : userDTO.getLoginName());
			dto.setRealName(userDTO == null ? null : userDTO.getRealName());

			if(harborAuthMap.get(dto.getUserId()) != null){
				throw new CommonException("error.harbor.auth.already.exist",dto.getRealName());
			}

			dto.setProjectId(projectId);
			dto.setOrganizationId(harborRepository.getOrganizationId());
			dto.setHarborId(harborRepository.getHarborId());
			dto.setHarborRoleValue(dto.getHarborRoleValue());
			dto.setHarborAuthId(-1L);
		});

		transactionalProducer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(HarborConstants.HarborSagaCode.CREATE_AUTH)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("dockerRepo")
						.withSourceId(projectId),
				startSagaBuilder -> {

				//保存到数据库
				Long harborId = dtoList.get(0).getHarborId();
				ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,false,harborId);
				List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
				Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
				dtoList.stream().forEach(dto->{
					if(harborAuthVoMap.get(dto.getLoginName()) != null){
						throw new CommonException("error.harbor.auth.find.harborAuthId");
					}
				});
				repository.batchInsert(dtoList);

				startSagaBuilder.withPayloadAndSerialize(dtoList).withSourceId(projectId);
		});
	}

	@Override
	@OperateLog(operateType = HarborConstants.UPDATE_AUTH,content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】")
	@Transactional(rollbackFor = Exception.class)
	public void update(HarborAuth harborAuth) {
		checkProjectAdmin(harborAuth.getProjectId());
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		if(HarborConstants.Y.equals(harborAuth.getLocked())){
			throw new CommonException("error.harbor.auth.owner.not.update");
		}
		processHarborAuthId(harborAuth);
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
		for(Map.Entry<Long,List<HarborAuth>> entry : dataListMap.entrySet()){
			Long projectId = entry.getKey();
			List<HarborAuth> list = entry.getValue();
			Set<Long> userIdSet = list.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
			Map<Long,UserWithGitlabIdDTO> map = c7nBaseService.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
			userDtoMap.putAll(map);
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
		checkProjectAdmin(harborAuth.getProjectId());
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		if(harborRepository.getCreatedBy().equals(harborAuth.getUserId())){
			throw new CommonException("error.harbor.auth.owner.not.delete");
		}
		processHarborAuthId(harborAuth);
		Long harborId = harborRepository.getHarborId();
		repository.deleteByPrimaryKey(harborAuth);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_ONE_AUTH,null,null,false,harborId,harborAuth.getHarborAuthId());
	}

	private void processHarborAuthId(HarborAuth harborAuth){
		if(harborAuth.getHarborAuthId() == null || harborAuth.getHarborAuthId().intValue() == -1){
			HarborAuth dbAuth = harborAuthMapper.selectByPrimaryKey(harborAuth.getAuthId());
			harborAuth.setObjectVersionNumber(dbAuth.getObjectVersionNumber());
			harborAuth.setHarborAuthId(dbAuth.getHarborAuthId());
		}
	}

	@Override
	@ExcelExport(HarborAuth.class)
	public Page<HarborAuth> export(PageRequest pageRequest, HarborAuth harborAuth, ExportParam exportParam, HttpServletResponse response) {
		Page<HarborAuth> page = this.pageList(pageRequest,harborAuth);
		return page;
	}

	@Override
	@OperateLog(operateType = HarborConstants.ASSIGN_AUTH,content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
	public void saveOwnerAuth(Long projectId, Long organizationId, Integer harborId, List<HarborAuth> dtoList) {
		dtoList.forEach(dto->{
			//获取用户详情
			UserDTO userDTO = c7nBaseService.listUserById(dto.getUserId());
			dto.setLoginName(userDTO == null ? null : userDTO.getLoginName());
			dto.setRealName(userDTO == null ? null : userDTO.getRealName());
			dto.setUserId(userDTO == null ? null : userDTO.getId());
			dto.setProjectId(projectId);
			dto.setOrganizationId(organizationId);
			dto.setHarborRoleValue(dto.getHarborRoleValue());
			dto.setLocked(HarborConstants.Y);

			//获取harborAuthId，然后保存用户权限到数据库
			ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,false,harborId);
			List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
			Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,entity->entity));
			if(harborAuthVoMap.get(dto.getLoginName()) != null){
				dto.setHarborAuthId(harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId());
			}
			repository.insertSelective(dto);
		});
	}

	/***
	 * 检查当前用户是否为项目管理员
	 */
	@Override
	public void checkProjectAdmin(Long projectId){
		Long userId = DetailsHelper.getUserDetails().getUserId();
		HarborAuth harborAuth = new HarborAuth();
		harborAuth.setProjectId(projectId);
		harborAuth.setUserId(userId);
		HarborAuth dto = repository.select(harborAuth).stream().findFirst().orElse(null);
		if(dto == null){
			throw new CommonException("error.harbor.auth.null");
		}
		if(!dto.getHarborRoleId().equals(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId())){
			throw new CommonException("error.harbor.auth.not.projectAdmin");
		}
	}
}
