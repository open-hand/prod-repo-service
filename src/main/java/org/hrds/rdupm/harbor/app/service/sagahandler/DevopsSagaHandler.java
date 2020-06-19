package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.DevopsProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
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
public class DevopsSagaHandler {

	public static final String IAM_CREATE_PROJECT = "iam-create-project";

	public static final String IAM_CREATE_PROJECT_DEFAULT_REPO = "iam-create-project.createDefaultRepo";

	@Autowired
	private C7nBaseService c7nBaseService;

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Resource
	private TransactionalProducer transactionalProducer;

	@Autowired
	private HarborQuotaService harborQuotaService;

	/**
	 * 监听IAM服务，创建项目，然后创建默认仓库
	 */
	@SagaTask(code = IAM_CREATE_PROJECT_DEFAULT_REPO, description = "docker-创建默认仓库", sagaCode = IAM_CREATE_PROJECT, maxRetryCount = 4, seq = 1)
	@Transactional
	public String createDefaultRepo(String payload) {
		DevopsProjectDTO devopsProjectDTO = null;
		try {
			devopsProjectDTO = new ObjectMapper().readValue(payload, DevopsProjectDTO.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		String harborProjectCode = devopsProjectDTO.getOrganizationCode() + "-" + devopsProjectDTO.getProjectCode();

		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(devopsProjectDTO.getProjectId());
		projectDTO.setCode(devopsProjectDTO.getProjectCode());
		projectDTO.setName(devopsProjectDTO.getProjectName());
		projectDTO.setOrganizationId(devopsProjectDTO.getOrganizationId());

		UserDTO userDTO = c7nBaseService.listUserById(devopsProjectDTO.getUserId());

		HarborProjectVo harborProjectVo = new HarborProjectVo();
		harborProjectVo.setProjectDTO(projectDTO);
		harborProjectVo.setUserDTO(userDTO);
		harborProjectVo.setCode(harborProjectCode);
		harborProjectVo.setName(devopsProjectDTO.getProjectName());
		harborProjectVo.setPublicFlag("false");
		harborProjectVo.setContentTrustFlag("false");
		harborProjectVo.setPreventVulnerableFlag("false");
		harborProjectVo.setAutoScanFlag("false");
		harborProjectVo.setUseSysCveFlag("true");
		harborProjectVo.setUseProjectCveFlag("false");

		HarborQuotaVo harborQuotaVo = harborQuotaService.getGlobalQuota();
		harborProjectVo.setCountLimit(harborQuotaVo.getCountLimit());
		harborProjectVo.setStorageNum(harborQuotaVo.getStorageNum());
		harborProjectVo.setStorageUnit(harborQuotaVo.getStorageUnit());

		createHarborProject(harborProjectVo);
		return payload;
	}

	public void createHarborProject(HarborProjectVo harborProjectVo){
		ProjectDTO projectDTO = harborProjectVo.getProjectDTO();
		Long projectId = projectDTO.getId();
		HarborRepository harborRepository = new HarborRepository(projectId,harborProjectVo.getCode(),projectDTO.getName(),harborProjectVo.getPublicFlag(),-1L,projectDTO.getOrganizationId());
		harborRepositoryRepository.insertSelective(harborRepository);

		transactionalProducer.apply(StartSagaBuilder.newBuilder()
						.withSagaCode(HarborConstants.HarborSagaCode.CREATE_PROJECT)
						.withLevel(ResourceLevel.PROJECT)
						.withRefType("dockerRepo")
						.withSourceId(projectId),
				startSagaBuilder -> {
					startSagaBuilder.withPayloadAndSerialize(harborProjectVo).withSourceId(projectId);
				});
	}


}
