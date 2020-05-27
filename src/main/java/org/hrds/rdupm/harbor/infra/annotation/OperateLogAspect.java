package org.hrds.rdupm.harbor.infra.annotation;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborLogRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author chenxiuhong 2020/04/29 3:00 下午
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class OperateLogAspect {

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Autowired
	private HarborLogRepository harborLogRepository;

	private String PROJECT_ID = "projectId";

	private String DTOLIST = "dtoList";

	private String HARBOR_AUTH = "harborAuth";

	@Pointcut("@annotation(org.hrds.rdupm.harbor.infra.annotation.OperateLog)")
	public void logPointCut(){ }

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point){
		Long operateId = DetailsHelper.getUserDetails().getUserId();
		String operatorInfo = getUserParms(operateId);
		SimpleDateFormat sdf = new SimpleDateFormat(BaseConstants.Pattern.DATE);

		//解析方法请求参数，并放入map中
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		String[] parameterNames = signature.getParameterNames();

		Object[] args = point.getArgs();
		Map<Object, Object> parmMap = processParameters(parameterNames, args);

		//解析注解参数
		OperateLog operateLog = method.getAnnotation(OperateLog.class);
		String operateType = operateLog.operateType();
		String content = operateLog.content();

		HarborLog harborLog = new HarborLog();
		harborLog.setOperatorId(operateId);
		harborLog.setOperateType(operateType);
		harborLog.setOperateTime(new Date());

		List<String> contentList = new ArrayList<>();
		if (operateLog != null && method != null && StringUtils.isNotEmpty(operateType)) {
			switch (operateType) {
				case HarborConstants.ASSIGN_AUTH:
					List<HarborAuth> dtoList = (List<HarborAuth>) parmMap.get(DTOLIST);
					for(HarborAuth harborAuth : dtoList){
						contentList.add(String.format(content, operatorInfo, getUserParms(harborAuth.getUserId()),HarborConstants.HarborRoleEnum.getNameById(harborAuth.getHarborRoleId()),sdf.format(harborAuth.getEndDate())));
					}
					harborLog.setProjectId((Long) parmMap.get(PROJECT_ID));
					harborLog.setOrganizationId(getOrganizationId((Long) parmMap.get(PROJECT_ID)));
					break;
				case HarborConstants.UPDATE_AUTH:
					HarborAuth harborAuth = (HarborAuth) parmMap.get(HARBOR_AUTH);
					contentList.add(String.format(content, operatorInfo, getUserParms(harborAuth.getUserId()),HarborConstants.HarborRoleEnum.getNameById(harborAuth.getHarborRoleId()),sdf.format(harborAuth.getEndDate())));
					harborLog.setProjectId(harborAuth.getProjectId());
					harborLog.setOrganizationId(harborAuth.getOrganizationId());
					break;
				case HarborConstants.REVOKE_AUTH:
					HarborAuth harborAuth2 = (HarborAuth) parmMap.get(HARBOR_AUTH);
					contentList.add(String.format(content, operatorInfo, getUserParms(harborAuth2.getUserId()),HarborConstants.HarborRoleEnum.getNameById(harborAuth2.getHarborRoleId())));
					harborLog.setProjectId(harborAuth2.getProjectId());
					harborLog.setOrganizationId(harborAuth2.getOrganizationId());
					break;
				default:break;
			}
		}

		//执行业务逻辑
		Object object = null;
		try {
			object = point.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CommonException(e.getMessage());
		}

		//保存日志记录
		contentList.forEach(str->{
			harborLog.setLogId(null);
			harborLog.setContent(str);
			harborLogRepository.insertSelective(harborLog);
		});

		return object;
	}

	/***
	 * 根据用户ID获取用户信息
	 * @param userId
	 * @return
	 */
	private String getUserParms(Long userId) {
		UserDTO userDTO = c7nBaseService.listUserById(userId);
		return userDTO.getRealName() + "(" + userDTO.getLoginName() + ")";
	}

	/***
	 * 根据项目ID获取组织ID
	 * @param projectId
	 * @return
	 */
	private Long getOrganizationId(Long projectId) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID,projectId).stream().findFirst().orElse(null);
		return harborRepository == null ? null : harborRepository.getOrganizationId();
	}

	private Map<Object, Object> processParameters(String[] parameterNames, Object[] args) {
		Map<Object, Object> objectMap = new HashMap<>(16);
		for (int i = 0; i < parameterNames.length; i++) {
			objectMap.put(parameterNames[i], args[i]);
		}
		return objectMap;
	}
}
