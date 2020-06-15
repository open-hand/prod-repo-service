package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	 * 1.选举新的仓库管理员
	 * 若权限列表存在用户为"项目管理员&&仓库管理员"，则不创建新的仓管
	 * 若权限列表存在用户为"项目管理员&&访客/开发人员"，则更新为仓管
	 * 若权限列表不存在"项目管理员&&仓库管理员"，则随机选择一个项目所有者，1）创建Harbor账号 2)分配Harbor仓库管理员权限
	 * 备注：使用原来的仓管账号执行上一步操作
	 * 2.删除原来仓管权限，同时删除数据库中权限
	 */
	@SagaTask(code = DOCKER_DELETE_AUTH, description = " 制品库删除权限同步事件", sagaCode = IAM_DELETE_MEMBER_ROLE, maxRetryCount = 3, seq = 1)
	@Transactional
	public String delete(String payload) {
		List<IamGroupMemberVO> iamGroupMemberVOList = new Gson().fromJson(payload, new TypeToken<List<IamGroupMemberVO>>() {}.getType());
		iamGroupMemberVOList.forEach(dto->{
			if(project.equals(dto.getResourceType())){
				HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,dto.getResourceId()).stream().findFirst().orElse(null);
				if(harborRepository == null){
					return;
				}

				//选举新的项目管理员角色
				createNewOwner(harborRepository,dto.getUserId());

				//删除权限角色
				HarborAuth dbAuth = harborAuthMapper.selectByCondition(Condition.builder(HarborAuth.class)
						.where(Sqls.custom()
								.andEqualTo(HarborAuth.FIELD_PROJECT_ID,harborRepository.getProjectId())
								.andEqualTo(HarborAuth.FIELD_USER_ID,dto.getUserId())
						).build()).stream().findFirst().orElse(null);
				if(dbAuth != null){
					deleteHarborAuth(dbAuth);
				}
			}
		});
		return payload;
	}

	public void createNewOwner(HarborRepository harborRepository,Long userId){
		Long projectId = harborRepository.getProjectId();
		Map<Long,UserDTO> userDTOMap = c7nBaseService.listProjectOwnerById(projectId);

		//查询权限列表中属于项目所有者的信息
		List<HarborAuth> dbAuthList = harborAuthMapper.selectByCondition(Condition.builder(HarborAuth.class)
				.where(Sqls.custom()
						.andEqualTo(HarborAuth.FIELD_PROJECT_ID,projectId)
						.andIn(HarborAuth.FIELD_USER_ID,userDTOMap.keySet())
						.andNotEqualTo(HarborAuth.FIELD_USER_ID,userId)
				).build());
		//无项目所有者权限，则创建
		UserDTO userDTO = c7nBaseService.getProjectOwnerById(projectId);
		if(CollectionUtils.isEmpty(dbAuthList)){
			saveAuth(harborRepository,userDTO);
		}
		//有项目所有者权限，但没有仓库管理员，则选择其中一个所有者进行更新
		else {
			List<HarborAuth> filterList = dbAuthList.stream().filter(dto->HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId().equals(dto.getHarborRoleId()) && !dto.getUserId().equals(userId)).collect(Collectors.toList());
			if(CollectionUtils.isEmpty(filterList)){
				updateAuth(harborRepository,dbAuthList.get(0));
			}
		}
	}

	public void saveAuth(HarborRepository harborRepository,UserDTO userDTO){
		//设置权限信息
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

		//创建账号
		harborAuthService.saveHarborUser(userDTO);

		//Harbor中创建权限
		Map<String,Object> bodyMap = new HashMap<>(2);
		Map<String,Object> memberMap = new HashMap<>(1);
		memberMap.put("username",userDTO.getLoginName());
		bodyMap.put("role_id",harborAuth.getHarborRoleId());
		bodyMap.put("member_user",memberMap);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH,null,bodyMap,true,harborRepository.getHarborId());

		//权限保存到数据库
		harborAuthService.saveOwnerAuth(harborRepository.getProjectId(),harborRepository.getOrganizationId(),Integer.parseInt(harborRepository.getHarborId().toString()),authList);
	}

	@OperateLog(operateType = HarborConstants.UPDATE_AUTH,content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】(团队成员删除)")
	private void updateAuth(HarborRepository harborRepository, HarborAuth harborAuth) {
		harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
		harborAuthRepository.updateByPrimaryKey(harborAuth);

		Map<String,Object> bodyMap = new HashMap<>(2);
		bodyMap.put("role_id",harborAuth.getHarborRoleId());
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_ONE_AUTH,null,bodyMap,true,harborRepository.getHarborId(),harborAuth.getHarborAuthId());
	}

	@OperateLog(operateType = HarborConstants.REVOKE_AUTH,content = "%s 删除 %s 的权限角色 【%s】(团队成员删除)")
	private void deleteHarborAuth(HarborAuth harborAuth) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,harborAuth.getProjectId()).stream().findFirst().orElse(null);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		harborAuthRepository.deleteByPrimaryKey(harborAuth);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_ONE_AUTH,null,null,true,harborRepository.getHarborId(),harborAuth.getHarborAuthId());
	}

}
