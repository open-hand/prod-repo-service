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
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
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

	@Autowired
	private TransactionalProducer transactionalProducer;

	@Override
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
		Map<String,HarborAuth> harborAuthMap = CollectionUtils.isEmpty(existList) ? new HashMap<>(1) : existList.stream().collect(Collectors.toMap(HarborAuth::getLoginName,dto->dto));

		Long harborId = harborRepository.getHarborId();
		dtoList.forEach(dto->{
			if(harborAuthMap.get(dto.getLoginName()) != null){
				throw new CommonException("error.harbor.auth.already.exist",dto.getLoginName(),dto.getRealName());
			}
			dto.setProjectId(projectId);
			dto.setOrganizationId(harborRepository.getOrganizationId());
			dto.setHarborId(harborId);
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
	@Transactional(rollbackFor = Exception.class)
	public void update(HarborAuth harborAuth) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		repository.updateByPrimaryKey(harborAuth);

		Map<String,Object> bodyMap = new HashMap<>(2);
		bodyMap.put("role_id",harborAuth.getHarborRoleId());
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_ONE_AUTH,null,bodyMap,false,harborId,harborAuth.getHarborAuthId());
	}

	@Override
	public Page<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth) {
		Page<HarborAuth> page = PageHelper.doPageAndSort(pageRequest,()->harborAuthMapper.list(harborAuth));
		List<HarborAuth> dataList = page.getContent();
		if(CollectionUtils.isEmpty(dataList)){
			return page;
		}
		Long projectId = dataList.get(0).getProjectId();

		Set<Long> userIdSet = dataList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
		if(responseEntity == null){
			throw new CommonException("error.feign.user.select.empty");
		}
		Map<Long,UserWithGitlabIdDTO> userDtoMap = responseEntity.getBody().stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId,dto->dto));
		dataList.forEach(dto->{
			UserWithGitlabIdDTO userDto = userDtoMap.get(dto.getUserId());
			if(userDto != null){
				dto.setRealName(userDto.getRealName());
				dto.setUserImageUrl(userDto.getImageUrl());

				List<RoleDTO> roleDTOList = userDto.getRoles();
				if(CollectionUtils.isNotEmpty(roleDTOList)){
					String memberRole = "";
					for(RoleDTO roleDTO : roleDTOList){
						memberRole = memberRole + roleDTO.getName() + "  ";
					}
					dto.setMemberRole(memberRole);
				}
			}
		});

		return page;
	}

	@Override
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
