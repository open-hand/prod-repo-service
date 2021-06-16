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

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.*;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRobotRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.CustomContextUtil;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/26 5:09 下午
 */
@Component
public class HarborProjectCreateHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private HarborHttpClient harborHttpClient;

	@Autowired
	private HarborAuthService harborAuthService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HarborQuotaService harborQuotaService;

	@Autowired
	private HarborProjectService harborProjectService;

	@Resource
	private HarborRepositoryMapper harborRepositoryMapper;
	@Autowired
    private HarborRobotRepository harborRobotRepository;
	@Autowired
    private HarborRobotService harborRobotService;
	@Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_USER,description = "创建Docker镜像仓库：创建用户",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 1,maxRetryCount = 3,outputSchemaClass = String.class)
	private String createProjectUserSaga(String message){
		try {
			HarborProjectVo harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
			UserDTO userDTO = harborProjectVo.getUserDTO();
			harborAuthService.saveHarborUser(userDTO);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_REPO,description = "创建Docker镜像仓库：创建镜像仓库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 2,maxRetryCount = 3, outputSchemaClass = String.class)
	private String createProjectRepoSaga(String message) throws JsonProcessingException {
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		UserDTO userDTO = harborProjectVo.getUserDTO();
		String userName = userDTO.getLoginName();

		CustomUserDetails userDetails = DetailsHelper.getUserDetails();
		//创建Harbor项目
		HarborProjectDTO harborProjectDTO = new HarborProjectDTO(harborProjectVo);
		//填入创建用户的上下文,去创建仓库
		CustomContextUtil.setDefaultIfNull(harborProjectVo.getUserDTO());
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_PROJECT,null,harborProjectDTO,false);
		//查询harbor-id
		Integer harborId = null;
		Map<String,Object> paramMap2 = new HashMap<>(3);
		paramMap2.put("name",harborProjectVo.getCode());
		paramMap2.put("public",harborProjectVo.getPublicFlag());
//		paramMap2.put("owner",userName);
		ResponseEntity<String> projectResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_PROJECT,paramMap2,null,false);
		List<String> projectList= JSONObject.parseArray(projectResponse.getBody(),String.class);
		if(CollectionUtils.isNotEmpty(projectList)){
			Gson gson = new Gson();
			for(String object : projectList){
				HarborProjectDTO projectResponseDto = gson.fromJson(object, HarborProjectDTO.class);
				if(harborProjectVo.getCode().equals(projectResponseDto.getName())){
					harborId = projectResponseDto.getHarborId();
					break;
				}
			}
		}
		if(harborId == null){
			throw new CommonException("error.harbor.project.get.harborId");
		}
		harborProjectVo.setHarborId(harborId);
		return objectMapper.writeValueAsString(harborProjectVo);
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_DB,description = "创建Docker镜像仓库：更新harbor_id字段到数据库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 3,maxRetryCount = 3, outputSchemaClass = String.class)
	private String createProjectDbSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		ProjectDTO projectDTO = harborProjectVo.getProjectDTO();
		Integer harborId = harborProjectVo.getHarborId();
		Long projectId = projectDTO.getId();
		harborRepositoryMapper.updateHarborIdByProjectId(projectId,harborId);
		return message;
	}


	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_AUTH,description = "创建Docker镜像仓库：保存创建者权限到数据库",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 4,maxRetryCount = 3, outputSchemaClass = String.class)
	private String createProjectAuthSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		UserDTO userDTO = harborProjectVo.getUserDTO();
		String userName = userDTO.getLoginName();
		Long userId = userDTO.getId();
		String realName = userDTO.getRealName();
		ProjectDTO projectDTO = harborProjectVo.getProjectDTO();

		List<HarborAuth> authList = new ArrayList<>();
		HarborAuth harborAuth = new HarborAuth();
		harborAuth.setUserId(userId);
		harborAuth.setLoginName(userName);
		harborAuth.setRealName(realName);
		harborAuth.setHarborRoleValue(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleValue());
		try {
			harborAuth.setEndDate(new SimpleDateFormat(BaseConstants.Pattern.DATE).parse("2099-12-31"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		authList.add(harborAuth);
		harborAuthService.saveOwnerAuth(projectDTO.getId(),projectDTO.getOrganizationId(),harborProjectVo.getHarborId(),authList);
		return message;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_QUOTA,description = "创建Docker镜像仓库：保存存储容量配置",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 5,maxRetryCount = 3)
	private void createProjectQuotaSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		if(harborProjectVo.getStorageNum() == -1 && harborProjectVo.getCountLimit() == -1){
			return;
		}
		harborQuotaService.saveQuota(harborProjectVo,harborProjectVo.getHarborId());
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.CREATE_PROJECT_CVE,description = "创建Docker镜像仓库：保存cve白名单",
			sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT,seq = 5,maxRetryCount = 3)
	private void createProjectCveSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		harborProjectService.saveWhiteList(harborProjectVo,harborProjectVo.getHarborId());
	}

    @SagaTask(code = HarborConstants.HarborSagaCode.ROBOT_SAGA_TASK_CODE, description = "创建harbor机器人账户",
            sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT, seq = 4, maxRetryCount = 3, outputSchemaClass = String.class)
    public String generateRobot(String message){
        HarborProjectVo projectVo = new Gson().fromJson(message, HarborProjectVo.class);

        List<HarborRepository> repositoryList = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectVo.getProjectDTO().getId()))
                .andWhere(Sqls.custom().andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID, projectVo.getProjectDTO().getOrganizationId()))
                .build());
        if (CollectionUtils.isEmpty(repositoryList)) {
            throw new CommonException("error.harbor.robot.repository.select");
        }
        HarborRepository repository = repositoryList.get(0);

        List<HarborRobot> dbRobotList = harborRobotRepository.selectByCondition(Condition.builder(HarborRobot.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRobot.FIELD_PROJECT_ID, projectVo.getProjectDTO().getId())
                        .andEqualTo(HarborRobot.FIELD_ORGANIZATION_ID, projectVo.getProjectDTO().getOrganizationId()))
                .build());
        if (CollectionUtils.isNotEmpty(dbRobotList)) {
            harborRobotRepository.batchDeleteByPrimaryKey(dbRobotList);
        }
        List<HarborRobot> harborRobotList = new ArrayList<>(2);

        HarborRobot harborRobot = new HarborRobot();
        harborRobot.setProjectId(repository.getProjectId());
        harborRobot.setHarborProjectId(repository.getHarborId());
        harborRobot.setOrganizationId(repository.getOrganizationId());
        //创建pull账户
        harborRobot.setName(repository.getCode() + BaseConstants.Symbol.MIDDLE_LINE + HarborConstants.HarborRobot.ACTION_PULL);
        harborRobot.setAction(HarborConstants.HarborRobot.ACTION_PULL);
        harborRobot.setDescription(repository.getCode() + BaseConstants.Symbol.SPACE  + HarborConstants.HarborRobot.ACTION_PULL + BaseConstants.Symbol.SPACE + HarborConstants.HarborRobot.ROBOT);
        harborRobotList.add(harborRobotService.createRobot(harborRobot));

        //创建push账户
        HarborUtil.resetDomain(harborRobot);
        harborRobot.setName(repository.getCode() + BaseConstants.Symbol.MIDDLE_LINE + HarborConstants.HarborRobot.ACTION_PUSH);
        harborRobot.setAction(HarborConstants.HarborRobot.ACTION_PUSH);
        harborRobot.setDescription(repository.getCode() + BaseConstants.Symbol.SPACE  + HarborConstants.HarborRobot.ACTION_PUSH + BaseConstants.Symbol.SPACE + HarborConstants.HarborRobot.ROBOT);
        harborRobotList.add(harborRobotService.createRobot(harborRobot));
        return new Gson().toJson(harborRobotList);
    }
}
