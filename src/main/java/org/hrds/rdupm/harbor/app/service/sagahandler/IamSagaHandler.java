package org.hrds.rdupm.harbor.app.service.sagahandler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.api.vo.QuotasVO;
import org.hrds.rdupm.harbor.api.vo.RegisterSaasOrderAttrVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborAuthMapper;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.JsonHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/06/08 5:13 下午
 */
@Component
public class IamSagaHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(IamSagaHandler.class);

	/**
	 * IAM删除角色
	 */
	public static final String IAM_DELETE_MEMBER_ROLE = "iam-delete-memberRole";

	public static final String DOCKER_DELETE_AUTH = "rdupm-docker-delete-auth";

	public static final String SAAS_TENANT_UPGRADE = "saas-tenant-upgrade";

	public static final String SAAS_VERSION_UPGRADE = "saas-version-upgrade";

	private String project = "project";

	@Value("${harbor.choerodon.capacity.limit.base: 20}")
	private Integer harborBaseCapacityLimit;

	/**
	 * 企业版 一个项目限制50G
	 */
	@Value("${harbor.choerodon.capacity.limit.business: 50}")
	private Integer harborBusinessCapacityLimit;

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

	@Autowired
	private HarborQuotaService harborQuotaService;

	@Autowired
	private HarborRepositoryMapper harborRepositoryMapper;

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


	@SagaTask(code = SAAS_TENANT_UPGRADE, description = "Saas组织升级，修改Harbor仓库容量", sagaCode = SAAS_VERSION_UPGRADE, maxRetryCount = 3, seq = 1)
	public void changeHarborCapacity(String payload) {
		RegisterSaasOrderAttrVO registerSaasOrderAttrVO = null;
		try {
			registerSaasOrderAttrVO = JsonHelper.unmarshalByJackson(payload, RegisterSaasOrderAttrVO.class);
		} catch (Exception e) {
			throw new CommonException(e);
		}
		List<String> saasLevels = Arrays.asList(SaasLevelEnum.FREE.name(), SaasLevelEnum.STANDARD.name(), SaasLevelEnum.SENIOR.name());
		if (registerSaasOrderAttrVO == null || registerSaasOrderAttrVO.getVersion() == null || !saasLevels.contains(registerSaasOrderAttrVO.getVersion())) {
			LOGGER.warn(">>Saas tenant not exist ");
		}
		 Integer harborCapacityLimit = null;
		if (StringUtils.equalsIgnoreCase(SaasLevelEnum.FREE.name(), registerSaasOrderAttrVO.getVersion())
				|| StringUtils.equalsIgnoreCase(SaasLevelEnum.STANDARD.name(), registerSaasOrderAttrVO.getVersion())) {
			harborCapacityLimit = harborBaseCapacityLimit;
		}
		else if (StringUtils.equalsIgnoreCase(SaasLevelEnum.SENIOR.name(), registerSaasOrderAttrVO.getVersion())) {
			harborCapacityLimit=harborBusinessCapacityLimit;
		}

		//查询组织
		List<ProjectDTO> projectDTOS = c7nBaseService.queryProjectByOrgId(registerSaasOrderAttrVO.getTenantId());
		if (CollectionUtils.isEmpty(projectDTOS)) {
			return;
		}

		List<QuotasVO> allHarborQuotas = harborQuotaService.getAllHarborQuotas();
		Integer finalHarborCapacityLimit = harborCapacityLimit;
		projectDTOS.forEach(projectDTO -> {
			//查询该项目下是否有默认的docker仓库
			HarborRepository harborRepository = new HarborRepository();
			harborRepository.setOrganizationId(projectDTO.getOrganizationId());
			harborRepository.setProjectId(projectDTO.getId());
			HarborRepository repository = harborRepositoryMapper.selectOne(harborRepository);
			if (repository == null) {
				return;
			}
			//如果存在harbor仓库，则容量限制
			//获取quotas id
			Integer projectQuotasId = getProjectQuotasId(repository.getCode(), allHarborQuotas);
			if (projectQuotasId == null) {
				LOGGER.error("{} Quotas Id is null", repository.getCode());
				return;
			}
			Map<String, Object> hard = new HashMap<>(1);
			Map<String, Object> storage = new HashMap<>(1);
			storage.put("storage", HarborUtil.getStorageLimit(finalHarborCapacityLimit, HarborConstants.GB));
			hard.put("hard", storage);
			ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_QUOTAS, null, hard, true, projectQuotasId);
		});



		return;
	}

	private Integer getProjectQuotasId(String code, List<QuotasVO> allHarborQuotas) {
		List<QuotasVO> quotasVOS = allHarborQuotas.stream().filter(quotasVO -> quotasVO.getRef() != null
				&& StringUtils.equalsIgnoreCase(quotasVO.getRef().getName(), code)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(quotasVOS)) {
			return quotasVOS.get(0).getId();
		} else {
			return null;
		}
	}

	public void createNewOwner(HarborRepository harborRepository,Long userId){
		//选举新仓库管理员得逻辑
//		AB都是项目所有者，A是仓库管理员，B是开发人员。此时删除A的仓管权限
//		1）若制品库权限数据中存在项目所有者，即B有权限，则选举B为仓库管理员。
//		2）若没有制品库权限数据，即AB都没有权限，才选举任意一个项目所有者作为仓管
//		3）若不存在项目所有者，应该找个项目成员选举为仓管。
//		4）若没有项目成员即制品库没有任何权限，后续需要分配权限的话，可以使用管理员账号操作
		Long projectId = harborRepository.getProjectId();
		// 这里查询iam获取项目下的项目管理员可能为null,所以要做判断
		Map<Long, UserDTO> userDTOMap = c7nBaseService.listProjectOwnerById(projectId);
		List<HarborAuth> dbAuthList = new ArrayList<>();
		if (!MapUtils.isEmpty(userDTOMap)) {
			//查询权限列表中属于项目所有者的信息
			dbAuthList = harborAuthMapper.selectByCondition(Condition.builder(HarborAuth.class)
					.where(Sqls.custom()
							.andEqualTo(HarborAuth.FIELD_PROJECT_ID, projectId)
							.andIn(HarborAuth.FIELD_USER_ID, userDTOMap.keySet())
							.andNotEqualTo(HarborAuth.FIELD_USER_ID, userId)
					).build());
		}
		//无项目所有者权限，则创建
		UserDTO userDTO = null;
		userDTO = c7nBaseService.getProjectOwnerById(projectId);
		if (Objects.isNull(userDTO)) {
			List<UserDTO> userDTOS = c7nBaseService.listProjectUsersByIdName(projectId, null);
			if (!CollectionUtils.isEmpty(userDTOS)) {
				userDTO = userDTOS.get(0);
			}
		}
		if (CollectionUtils.isEmpty(dbAuthList)) {
			//项目下没有一个成员
			if (Objects.isNull(userDTO)) {
				return;
			}
			saveAuth(harborRepository, userDTO);
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
//		harborAuth.setLoginName(userDTO.getLoginName());
		harborAuth.setRealName(userDTO.getRealName());
		harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
		try {
			harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
		} catch (ParseException e) {
			LOGGER.error("error.format.date", e);
		}


		//创建账号
		harborAuthService.saveHarborUser(userDTO);
		harborAuth.setLoginName(userDTO.getLoginName());
		authList.add(harborAuth);
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
