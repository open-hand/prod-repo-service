package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
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
	private ProdUserService prodUserService;

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_USER,description = "分配权限：插入用户",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	private String insertUser(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		List<ProdUser> prodUserList = new ArrayList<>();
		for(HarborAuth harborAuth : dtoList){
			//数据库插入制品库用户
			String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
			ProdUser prodUser = new ProdUser(harborAuth.getUserId(),harborAuth.getLoginName(),password,0);
			prodUserList.add(prodUser);

			//校验Harbor中是否已存在用户
			Map<String,Object> paramMap = new HashMap<>(1);
			paramMap.put("username",harborAuth.getLoginName());
			ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME,paramMap,null,true);
			List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);
			Map<String,User> userMap = CollectionUtils.isEmpty(userList) ? new HashMap<>(1) : userList.stream().collect(Collectors.toMap(User::getUsername, dto->dto));

			//Harbor系统插入用户
			if(userMap.get(harborAuth.getLoginName()) == null){
				UserDTO userDTO = c7nBaseService.queryByLoginName(harborAuth.getLoginName());
				User user = new User(userDTO.getLoginName(),userDTO.getEmail(),password,userDTO.getRealName());
				harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER,null,user,true);
			}
		}
		prodUserService.saveMultiUser(prodUserList);
		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_AUTH,description = "分配权限：保存权限到Harbor",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 2,maxRetryCount = 3,outputSchemaClass = List.class)
	private List<HarborAuth> insertToHarbor(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		for(HarborAuth dto : dtoList){
			Map<String,Object> bodyMap = new HashMap<>(2);
			Map<String,Object> memberMap = new HashMap<>(1);
			memberMap.put("username",dto.getLoginName());
			bodyMap.put("role_id",dto.getHarborRoleId());
			bodyMap.put("member_user",memberMap);
			harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH,null,bodyMap,false,dto.getHarborId());
		}
		return dtoList;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_AUTH_DB,description = "分配权限：更新harbor_auth_id到数据库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_AUTH,seq = 3,maxRetryCount = 3)
	private void updateToDb(String message){
		List<HarborAuth> dtoList = JSONObject.parseArray(message,HarborAuth.class);
		Long harborId = dtoList.get(0).getHarborId();

		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH,null,null,false,harborId);
		List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(),new TypeToken<List<HarborAuthVo>>(){}.getType());
		Map<String,HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName,dto->dto));
		dtoList.stream().forEach(dto->{
			if(harborAuthVoMap.get(dto.getLoginName()) != null){
				dto.setHarborAuthId(harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId());
			}
		});
		repository.batchUpdateOptional(dtoList,HarborAuth.FIELD_HARBOR_AUTH_ID);
	}

}
