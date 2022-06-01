package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.infra.mapper.ProdUserMapper;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.User;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:16 下午
 */
@Component
public class HarborAuthCreateHandler {
	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborAuthRepository repository;

	@Autowired
	private HarborAuthService harborAuthService;

	@Autowired
	private ProdUserMapper prodUserMapper;

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_USER,description = "分配权限：插入用户",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	public String insertUser(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		Set<Long> userIdSet = dtoList.stream().map(dto->dto.getUserId()).collect(Collectors.toSet());
		Map<Long,UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);

		//新增用户到数据库、新增用户到Harbor
		for(HarborAuth harborAuth : dtoList){
			UserDTO userDTO = userDtoMap.get(harborAuth.getUserId());
			harborAuthService.saveHarborUser(userDTO);
		}
		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_AUTH,description = "分配权限：保存权限到Harbor",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 2,maxRetryCount = 3,outputSchemaClass = List.class)
	public List<HarborAuth> insertToHarbor(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		for(HarborAuth dto : dtoList){
			ProdUser prodUser = prodUserMapper.selectByPrimaryKey(dto.getUserId());
			Map<String,Object> bodyMap = new HashMap<>(2);
			Map<String,Object> memberMap = new HashMap<>(1);
			memberMap.put("username",prodUser.getLoginName());
			bodyMap.put("role_id",dto.getHarborRoleId());
			bodyMap.put("member_user",memberMap);
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH,null,bodyMap,false,dto.getHarborId());
		}
		return dtoList;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_DB,description = "分配权限：更新harbor_auth_id到数据库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 3,maxRetryCount = 3)
	public void updateToDb(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		Long harborId = dtoList.get(0).getHarborId();

		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,false,harborId);
		List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
		Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
		dtoList.stream().forEach(dto->{
			if(harborAuthVoMap.get(dto.getLoginName()) != null){
				dto.setHarborAuthId(harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId());
			}
			HarborAuth harborAuth = repository.selectByPrimaryKey(dto.getAuthId());
			if (harborAuth != null) {
				dto.setObjectVersionNumber(harborAuth.getObjectVersionNumber());
			}
		});
		repository.batchUpdateOptional(dtoList,HarborAuth.FIELD_HARBOR_AUTH_ID);
	}

}
