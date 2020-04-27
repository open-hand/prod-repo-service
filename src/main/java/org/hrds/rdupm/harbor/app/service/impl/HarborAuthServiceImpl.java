package org.hrds.rdupm.harbor.app.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.dto.User;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborAuthMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Long projectId,List<HarborAuth> dtoList) {
		if(CollectionUtils.isEmpty(dtoList)){
			throw new CommonException("error.harbor.auth.param.empty");
		}
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		Long harborId = harborRepository.getHarborId();
		dtoList.forEach(dto->{
			dto.setProjectId(projectId);
			dto.setOrganizationId(harborRepository.getOrganizationId());
			dto.setHarborId(harborId);
		});
		insertUser(dtoList);
		insertToHarbor(dtoList);
		insertToDb(dtoList);
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
	public PageInfo<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth) {
		Page<HarborAuth> page = PageHelper.doPageAndSort(pageRequest,()->harborAuthMapper.list(harborAuth));
		List<HarborAuth> dataList = page.getContent();
		if(CollectionUtils.isEmpty(dataList)){
			return PageConvertUtils.convert(page);
		}

		Set<Long> userIdSet = dataList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(harborAuth.getProjectId(),userIdSet);
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

		return PageConvertUtils.convert(page);
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

	private void insertUser(List<HarborAuth> dtoList){
		for(HarborAuth harborAuth : dtoList){
			Map<String,Object> paramMap = new HashMap<>(1);
			paramMap.put("username",harborAuth.getLoginName());
			ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
			List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);

			if(CollectionUtils.isEmpty(userList)){
				ResponseEntity<UserDTO> userDTOResponseEntity = baseFeignClient.query(harborAuth.getLoginName());
				UserDTO userDTO = userDTOResponseEntity.getBody();
				User user = new User(userDTO.getLoginName(),userDTO.getEmail(),HarborConstants.DEFAULT_PASSWORD,userDTO.getRealName());
				harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER,null,user,true);
			}
		}
	}

	private void insertToHarbor(List<HarborAuth> dtoList){
		for(HarborAuth dto : dtoList){
			Map<String,Object> bodyMap = new HashMap<>(2);
			Map<String,Object> memberMap = new HashMap<>(1);
			memberMap.put("username",dto.getLoginName());
			bodyMap.put("role_id",dto.getHarborRoleId());
			bodyMap.put("member_user",memberMap);
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH,null,bodyMap,false,dto.getHarborId());
		}
	}

	private void insertToDb(List<HarborAuth> dtoList){
		Long harborId = dtoList.get(0).getHarborId();
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,false,harborId);
		List<HarborAuthVo> harborImageTagVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
		Map<String,HarborAuthVo> harborAuthVoMap = harborImageTagVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
		dtoList.stream().forEach(dto->{
			if(harborAuthVoMap.get(dto.getLoginName()) != null){
				dto.setHarborAuthId(harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId());
			}else {
				throw new CommonException("error.harbor.auth.find.harborAuthId");
			}
		});
		repository.batchInsert(dtoList);
	}
}
