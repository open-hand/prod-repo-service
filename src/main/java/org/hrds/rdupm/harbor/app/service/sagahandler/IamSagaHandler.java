package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborAuthMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author chenxiuhong 2020/06/08 5:13 下午
 */
@Component
public class IamSagaHandler {
	/**
	 * IAM删除角色
	 */
	public static final String IAM_DELETE_MEMBER_ROLE = "iam-delete-memberRole";

	public static final String DOCKER_DELETE_AUTH = "rdupm-docker-delete-auth";

	private String project = "project";

	@Autowired
	private HarborAuthService harborAuthService;

	@Resource
	private HarborAuthMapper harborAuthMapper;

	@Autowired
	private HarborAuthRepository harborAuthRepository;
	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;
	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private C7nBaseService c7nBaseService;

	/**
	 * 删除角色同步事件
	 */
	@SagaTask(code = DOCKER_DELETE_AUTH, description = " 制品库删除权限同步事件", sagaCode = IAM_DELETE_MEMBER_ROLE, maxRetryCount = 3, seq = 1)
	public String deleteHarborAuth(String payload) {
		List<IamGroupMemberVO> iamGroupMemberVOList = new Gson().fromJson(payload, new TypeToken<List<IamGroupMemberVO>>() {}.getType());
		iamGroupMemberVOList.forEach(dto->{
			if(project.equals(dto.getResourceType())){
				//删除权限角色
				HarborAuth dbAuth = harborAuthMapper.selectByCondition(Condition.builder(HarborAuth.class)
						.where(Sqls.custom()
								.andEqualTo(HarborAuth.FIELD_PROJECT_ID,dto.getResourceId())
								.andEqualTo(HarborAuth.FIELD_USER_ID,dto.getUserId())
						).build()).stream().findFirst().orElse(null);
				if(dbAuth != null){
					deleteHarborAuth(dbAuth);
				}

				//选举新的项目管理员角色
				createNewOwner(dto.getResourceId());
			}
		});
		return payload;
	}

	@OperateLog(operateType = HarborConstants.REVOKE_AUTH,content = "团队成员删除：%s 删除 %s 的权限角色 【%s】")
	@Transactional(rollbackFor = Exception.class)
	public void deleteHarborAuth(HarborAuth harborAuth) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		harborAuthRepository.deleteByPrimaryKey(harborAuth);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_ONE_AUTH,null,null,true,harborRepository.getHarborId(),harborAuth.getHarborAuthId());
	}

	public void createNewOwner(Long projectId){
		//若不存在"仓库管理员同时是项目所有者"，则新建一个
		List<HarborAuth> dbAuthList = harborAuthMapper.selectByCondition(Condition.builder(HarborAuth.class)
				.where(Sqls.custom()
						.andEqualTo(HarborAuth.FIELD_PROJECT_ID,projectId)
						.andEqualTo(HarborAuth.FIELD_HARBOR_ROLE_ID,HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId())
				).build());

		UserDTO userDTO = c7nBaseService.getProjectOwnerById(projectId);
		if(CollectionUtils.isEmpty(dbAuthList)){
			saveAuth(projectId,userDTO);
		}else {
			Map<Long,UserDTO> userDTOMap = c7nBaseService.listProjectOwnerById(projectId);
			for(HarborAuth harborAuth : dbAuthList){
				if(userDTOMap.containsKey(harborAuth.getUserId())){
					return;
				}
			}
			saveAuth(projectId,userDTO);
		}

	}

	public void saveAuth(Long projectId,UserDTO userDTO){
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}

		List<HarborAuth> authList = new ArrayList<>();
		HarborAuth harborAuth = new HarborAuth();
		harborAuth.setUserId(userDTO.getId());
		harborAuth.setLoginName(userDTO.getLoginName());
		harborAuth.setRealName(userDTO.getRealName());
		harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
		try {
			harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		authList.add(harborAuth);
		harborAuthService.saveOwnerAuth(harborRepository.getProjectId(),harborRepository.getOrganizationId(),Integer.parseInt(harborRepository.getHarborId().toString()),authList);
	}

}
