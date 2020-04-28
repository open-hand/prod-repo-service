package org.hrds.rdupm.harbor.app.service.SagaHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.dto.User;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/26 5:09 下午
 */
@Component
public class HarborProjectCreateHandler {
	@Autowired
	private HarborHttpClient harborHttpClient;

	@Resource
	private BaseFeignClient baseFeignClient;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HarborQuotaService harborQuotaService;

	//TODO
	private String userName = "15367";

	@Autowired
	private HarborProjectService harborProjectService;

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_USER,description = "创建Docker镜像仓库：创建用户",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	private String createProjectUserSaga(String message){
		//判断是否存在当前用户
		Map<String,Object> paramMap = new HashMap<>(1);
		paramMap.put("username",userName);
		ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
		List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);
		Map<String,User> userMap = CollectionUtils.isEmpty(userList) ? new HashMap<>(16) : userList.stream().collect(Collectors.toMap(User::getUsername, dto->dto));

		if(userMap.get(userName) == null){
			ResponseEntity<UserDTO> userDTOResponseEntity = baseFeignClient.query(userName);
			UserDTO userDTO = userDTOResponseEntity.getBody();
			User user = new User(userDTO.getLoginName(),userDTO.getEmail(),HarborConstants.DEFAULT_PASSWORD,userDTO.getRealName());
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER,null,user,true);
		}

		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_REPO,description = "创建Docker镜像仓库：创建镜像仓库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 2,maxRetryCount = 3, outputSchemaClass = HarborProjectVo.class)
	private HarborProjectVo createProjectRepoSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}

		//创建Harbor项目
		HarborProjectDTO harborProjectDTO = new HarborProjectDTO(harborProjectVo);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_PROJECT,null,harborProjectDTO,false);

		//查询harbor-id
		Integer harborId = null;
		Map<String,Object> paramMap2 = new HashMap<>(3);
		paramMap2.put("name",harborProjectVo.getCode());
		paramMap2.put("public",harborProjectVo.getPublicFlag());
		paramMap2.put("owner",userName);
		ResponseEntity<String> projectResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_PROJECT,paramMap2,null,false);
		List<String> projectList= JSONObject.parseArray(projectResponse.getBody(),String.class);
		Gson gson = new Gson();
		for(String object : projectList){
			HarborProjectDTO projectResponseDto = gson.fromJson(object, HarborProjectDTO.class);
			if(harborProjectVo.getCode().equals(projectResponseDto.getName())){
				harborId = projectResponseDto.getHarborId();
				break;
			}
		}
		if(harborId == null){
			throw new CommonException("error.harbor.project.get.harborId");
		}
		harborProjectVo.setHarborId(harborId);
		return harborProjectVo;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_DB,description = "创建Docker镜像仓库：保存镜像仓库到数据库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 3,maxRetryCount = 3, outputSchemaClass = HarborProjectVo.class)
	private HarborProjectVo createProjectDbSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		ProjectDTO projectDTO = harborProjectVo.getProjectDTO();
		Integer harborId = harborProjectVo.getHarborId();
		HarborRepository harborRepository = new HarborRepository(projectDTO.getId(),projectDTO.getCode(),projectDTO.getName(),harborProjectVo.getPublicFlag(),new Long(harborId),projectDTO.getOrganizationId());
		harborRepositoryRepository.insertSelective(harborRepository);
		return harborProjectVo;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_QUOTA,description = "创建Docker镜像仓库：保存存储容量配置",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 4,maxRetryCount = 3)
	private void createProjectQuotaSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		harborQuotaService.saveQuota(harborProjectVo,harborProjectVo.getHarborId());
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_CVE,description = "创建Docker镜像仓库：保存cve白名单",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 4,maxRetryCount = 3)
	private void createProjectCveSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		harborProjectService.saveWhiteList(harborProjectVo,harborProjectVo.getHarborId());
	}
}
