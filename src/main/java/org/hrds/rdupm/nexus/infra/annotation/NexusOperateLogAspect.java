package org.hrds.rdupm.nexus.infra.annotation;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.app.service.impl.NexusComponentServiceImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusLogRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description
 *
 * @author chenxiuhong 2020/04/29 3:00 下午
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class NexusOperateLogAspect {
	private static final Logger logger = LoggerFactory.getLogger(NexusOperateLogAspect.class);

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private NexusRepositoryRepository nexusRepositoryRepository;

	@Autowired
	private NexusLogRepository nexusLogRepository;

	private static String PROJECT_ID = "projectId";

	private static String DATA_LIST = "nexusAuthList";

	private static String NEXUS_AUTH = "nexusAuth";

	@Pointcut("@annotation(org.hrds.rdupm.nexus.infra.annotation.NexusOperateLog)")
	public void logPointCut(){ }

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point){
		Long operateId = DetailsHelper.getUserDetails().getUserId();
		String operatorInfo = getUserParams(operateId);
		SimpleDateFormat sdf = new SimpleDateFormat(BaseConstants.Pattern.DATE);

		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();

		//解析注解参数
		NexusOperateLog operateLog = method.getAnnotation(NexusOperateLog.class);
		String operateType = operateLog.operateType();
		String content = operateLog.content();

		String[] parameterNames = signature.getParameterNames();
		Object[] args = point.getArgs();
		Map<Object, Object> parmMap = processParameters(parameterNames, args);


		NexusLog nexusLog = new NexusLog();
		nexusLog.setOperatorId(operateId);
		nexusLog.setOperateType(operateType);
		nexusLog.setOperateTime(new Date());

		List<String> contentList = new ArrayList<>();
		if (StringUtils.isNotEmpty(operateType)) {
			switch (operateType) {
				case NexusConstants.LogOperateType.AUTH_CREATE: {
					List<NexusAuth> nexusAuthList = (List<NexusAuth>) parmMap.get(DATA_LIST);

					NexusRepository nexusRepository = null;
					if (CollectionUtils.isNotEmpty(nexusAuthList)) {
						nexusRepository = this.getNexusRepository(nexusAuthList.get(0).getRepositoryId());
						if (nexusRepository == null) {
							logger.error("auth create error, repository not exist: {}", nexusAuthList.get(0).getRepositoryId());
							throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
						}
						for (NexusAuth nexusAuth : nexusAuthList) {
							contentList.add(String.format(content, operatorInfo, getUserParams(nexusAuth.getUserId()), nexusRepository.getNeRepositoryName(),
									NexusConstants.NexusRoleEnum.getNameByCode(nexusAuth.getRoleCode()), sdf.format(nexusAuth.getEndDate())));
						}
						nexusLog.setProjectId((Long) parmMap.get(PROJECT_ID));
						nexusLog.setOrganizationId(nexusRepository.getOrganizationId());
						nexusLog.setRepositoryId(nexusRepository.getRepositoryId());
					}
					break;
				}
				case NexusConstants.LogOperateType.AUTH_UPDATE:{
					NexusAuth nexusAuth = (NexusAuth) parmMap.get(NEXUS_AUTH);
					NexusRepository nexusRepository = this.getNexusRepository(nexusAuth.getRepositoryId());;

					contentList.add(String.format(content, operatorInfo, getUserParams(nexusAuth.getUserId()), nexusRepository.getNeRepositoryName(),
							NexusConstants.NexusRoleEnum.getNameByCode(nexusAuth.getRoleCode()), sdf.format(nexusAuth.getEndDate())));
					nexusLog.setProjectId(nexusAuth.getProjectId());
					nexusLog.setOrganizationId(nexusAuth.getOrganizationId());
					nexusLog.setRepositoryId(nexusAuth.getRepositoryId());
					break;
				}
				case NexusConstants.LogOperateType.AUTH_DELETE: {
					NexusAuth nexusAuth = (NexusAuth) parmMap.get(NEXUS_AUTH);
					NexusRepository nexusRepository = this.getNexusRepository(nexusAuth.getRepositoryId());;

					contentList.add(String.format(content, operatorInfo, getUserParams(nexusAuth.getUserId()), nexusRepository.getNeRepositoryName(),
							NexusConstants.NexusRoleEnum.getNameByCode(nexusAuth.getRoleCode())));
					nexusLog.setProjectId(nexusAuth.getProjectId());
					nexusLog.setOrganizationId(nexusAuth.getOrganizationId());
					nexusLog.setRepositoryId(nexusAuth.getRepositoryId());
					break;
				}
				default:break;
			}
		}

		//执行业务逻辑
		Object object = null;
		try {
			object = point.proceed();
		} catch (Throwable e) {
			logger.error("error ", e);
			throw new CommonException(e.getMessage());
		}

		//保存日志记录
		contentList.forEach(str->{
			nexusLog.setLogId(null);
			nexusLog.setContent(str);
			nexusLogRepository.insertSelective(nexusLog);
		});

		return object;
	}

	/***
	 * 根据用户ID获取用户信息
	 * @param userId
	 * @return
	 */
	private String getUserParams(Long userId) {
		UserDTO userDTO = c7nBaseService.listUserById(userId);
		return userDTO.getRealName() + "(" + userDTO.getLoginName() + ")";
	}

	/***
	 * 获取仓库信息
	 * @param repositoryId 仓库Id
	 * @return  NexusRepository
	 */
	private NexusRepository getNexusRepository(Long repositoryId) {
		return nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
	}

	private Map<Object, Object> processParameters(String[] parameterNames, Object[] args) {
		Map<Object, Object> objectMap = new HashMap<>(16);
		for (int i = 0; i < parameterNames.length; i++) {
			objectMap.put(parameterNames[i], args[i]);
		}
		return objectMap;
	}
}
