package org.hrds.rdupm.harbor.app.service.sagahandler;

import java.io.IOException;
import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.RegisterOrgDTO;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/11/30 4:59 下午
 */
@Component
public class RegisterOrgSagaHandler {

    public static final String REGISTER_ORG = "register-org";

    public static final String REGISTER_HARBOR_INIT_PROJECT = "register-harbor-init-project";

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    @Resource
    private TransactionalProducer transactionalProducer;

    @Autowired
    private HarborQuotaService harborQuotaService;

    @SagaTask(code = REGISTER_HARBOR_INIT_PROJECT, description = "docker-创建默认仓库", sagaCode = REGISTER_ORG, maxRetryCount = 4, seq = 95)
    public String createDefaultRepo(String payload) {
        RegisterOrgDTO registerOrgDTO = null;
        try {
            registerOrgDTO = new ObjectMapper().readValue(payload, RegisterOrgDTO.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }


        ProjectDTO organization = registerOrgDTO.getOrganization();
        ProjectDTO projectDTO = registerOrgDTO.getProject();
        UserDTO userDTO = registerOrgDTO.getUser();
        if(organization == null || projectDTO == null || userDTO == null){
            throw new CommonException("error.harbor.registerOrg.info.empty");
        }
        projectDTO.setOrganizationId(organization.getId());
        String harborProjectCode = organization.getCode() + "-" + projectDTO.getCode();

        userDTO = c7nBaseService.listUserById(userDTO.getId());
        UserDTO userDTO2 = new UserDTO(userDTO.getId(),userDTO.getLoginName(),userDTO.getRealName(),userDTO.getEmail());

        HarborProjectVo harborProjectVo = new HarborProjectVo();
        harborProjectVo.setProjectDTO(projectDTO);
        harborProjectVo.setUserDTO(userDTO2);
        harborProjectVo.setCode(harborProjectCode);
        harborProjectVo.setName(projectDTO.getName());
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
                        startSagaBuilder -> { startSagaBuilder.withPayloadAndSerialize(harborProjectVo).withSourceId(projectId);
        });
    }
}
