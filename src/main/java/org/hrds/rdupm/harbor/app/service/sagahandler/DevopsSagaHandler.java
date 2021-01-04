package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;

import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.api.vo.ProjectCategoryVO;
import org.hrds.rdupm.harbor.api.vo.ProjectMapCategoryVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/06/08 5:13 下午
 */
@Component
public class DevopsSagaHandler {

	public static final String IAM_CREATE_PROJECT = "iam-create-project";

	public static final String IAM_CREATE_PROJECT_DEFAULT_REPO = "iam-create-project.createDefaultRepo";

	//项目添加项目类型
	public static final String ADD_PROJECT_CATEGORY = "iam-add-project-category";
	/**
	 * devops项目类型同步处理
	 */
	public static final String DEVOPS_PROJECT_CATEGORY_SYNC = "devops-project-category-sync";
	private static final String N_DEVOPS = "N_DEVOPS";



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
	public String createDefaultRepo(String payload) {
		DevopsProjectDTO devopsProjectDTO = null;
		try {
			devopsProjectDTO = new ObjectMapper().readValue(payload, DevopsProjectDTO.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		//不包含devops项目类型不做同步
		if (!devopsProjectDTO.getProjectCategoryVOS().stream().map(ProjectCategoryVO::getCode).collect(Collectors.toList()).contains(N_DEVOPS)) {
			return payload;
		}
		String harborProjectCode = devopsProjectDTO.getOrganizationCode() + "-" + devopsProjectDTO.getProjectCode();

		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(devopsProjectDTO.getProjectId());
		projectDTO.setCode(devopsProjectDTO.getProjectCode());
		projectDTO.setName(devopsProjectDTO.getProjectName());
		projectDTO.setOrganizationId(devopsProjectDTO.getOrganizationId());

		UserDTO userDTO = c7nBaseService.listUserById(devopsProjectDTO.getUserId());
		UserDTO userDTO2 = new UserDTO(userDTO.getId(),userDTO.getLoginName(),userDTO.getRealName(),userDTO.getEmail());

		HarborProjectVo harborProjectVo = new HarborProjectVo();
		harborProjectVo.setProjectDTO(projectDTO);
		harborProjectVo.setUserDTO(userDTO2);
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

	/**
	 * 增加项目devops的项目类型以后，需要改项目创建仓库
	 * @param payload
	 * @return
	 */
	@SagaTask(code = DEVOPS_PROJECT_CATEGORY_SYNC,
			description = "docker-创建默认仓库",
			sagaCode = ADD_PROJECT_CATEGORY,
			maxRetryCount = 3,
			seq = 1)
	public String addDevopsProjectCategory(String payload) {
		DevopsProjectDTO devopsProjectDTO = null;
		try {
			devopsProjectDTO = new ObjectMapper().readValue(payload, DevopsProjectDTO.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		//不包含devops项目类型不做同步
		if (!devopsProjectDTO.getProjectCategoryVOS().stream().map(ProjectCategoryVO::getCode).collect(Collectors.toList()).contains(N_DEVOPS)) {
			return payload;
		}
		createDefaultRepo(payload);
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
