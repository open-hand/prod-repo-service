package org.hrds.rdupm.harbor.app.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.api.vo.QuotasVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.annotation.JobTask;

/**
 * Created by wangxiang on 2021/9/27
 */
@Component
public class HarborCapacityTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(HarborCapacityTask.class);


    /**
     * 免费版 试用版 标准版 一个项目限制 20GB
     */
    @Value("${harbor.choerodon.capacity.limit.base: 20}")
    private Integer harborBaseCapacityLimit;

    /**
     * 企业版 一个项目限制50G
     */
    @Value("${harbor.choerodon.capacity.limit.business: 50}")
    private Integer harborBusinessCapacityLimit;

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private HarborRepositoryMapper harborRepositoryMapper;

    @Autowired
    private HarborHttpClient harborHttpClient;

    @Autowired
    private HarborQuotaService harborQuotaService;


    @JobTask(maxRetryCount = 3, code = "harborCapacitylimit", description = "SaaS组织,试用组织Harbor容量的限制")
    public void harborCapacitylimit(Map<String, Object> map) {
        LOGGER.info("》》》》》》》》》》start harbor capacity limit 》》》》》》》》》》》");
        //1.查询所有正在生效的SaaS组织
        //1.查询正在生效的免费版，标准版的SaaS组织
        List<String> saasLevels = Arrays.asList(SaasLevelEnum.FREE.name(), SaasLevelEnum.STANDARD.name(), SaasLevelEnum.SENIOR.name());
        List<ExternalTenantVO> saasTenants = c7nBaseService.querySaasTenants(saasLevels);
        List<ExternalTenantVO> registerTenants = c7nBaseService.queryRegisterTenant();

        List<ExternalTenantVO> registerAndBaseSaasTenants = new ArrayList<>();
        List<ExternalTenantVO> busSaasTenants = new ArrayList<>();


        if (!CollectionUtils.isEmpty(saasTenants)) {
            // 过滤出免费版，标准版
            List<ExternalTenantVO> freeAndStandardTenants = saasTenants.stream().filter(externalTenantVO -> Arrays.asList(SaasLevelEnum.FREE.name(), SaasLevelEnum.STANDARD.name()).contains(externalTenantVO.getSaasLevel())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(freeAndStandardTenants)) {
                registerAndBaseSaasTenants.addAll(freeAndStandardTenants);
            }
            //过滤出企业版本
            List<ExternalTenantVO> busExternalTenantVO = saasTenants.stream().filter(externalTenantVO -> StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.SENIOR.getLevelValue())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(busExternalTenantVO)) {
                busSaasTenants.addAll(busExternalTenantVO);
            }
        }
        if (!CollectionUtils.isEmpty(registerTenants)) {
            registerAndBaseSaasTenants.addAll(registerTenants);
        }
        //获取所有仓库的 配额 quotas
        List<QuotasVO> allHarborQuotas = harborQuotaService.getAllHarborQuotas();
        if (CollectionUtils.isEmpty(allHarborQuotas)) {
            LOGGER.info(" Harbor has no project");
            return;
        }
        //处理基础版本的仓库
        if (!CollectionUtils.isEmpty(registerAndBaseSaasTenants)) {
            registerAndBaseSaasTenants.forEach(saaSTenantVO -> {
                LOGGER.info("处理组织id为：{}的harbor仓库", saaSTenantVO.getTenantId());
                updateHarborCapacity(saaSTenantVO, harborBaseCapacityLimit, allHarborQuotas);
            });
        }
        //处理商业版本的仓库
        if (!CollectionUtils.isEmpty(busSaasTenants)) {
            busSaasTenants.forEach(externalTenantVO -> {
                LOGGER.info("处理组织id为：{}的harbor仓库", externalTenantVO.getTenantId());
                updateHarborCapacity(externalTenantVO, harborBusinessCapacityLimit, allHarborQuotas);
            });
        }
        LOGGER.info("》》》》》》》》》》end harbor capacity limit 》》》》》》》》》》》");
    }


    private void updateHarborCapacity(ExternalTenantVO saaSTenantVO, Integer limit, List<QuotasVO> allHarborQuotas) {
        //2.查询组织下的项目
        List<ProjectDTO> projectDTOS = c7nBaseService.queryProjectByOrgId(saaSTenantVO.getTenantId());
        if (CollectionUtils.isEmpty(projectDTOS)) {
            return;
        }
        projectDTOS.forEach(projectDTO -> {
            //查询该项目下是否有默认的docker仓库
            HarborRepository harborRepository = new HarborRepository();
            harborRepository.setOrganizationId(saaSTenantVO.getTenantId());
            harborRepository.setProjectId(projectDTO.getId());
            HarborRepository repository = harborRepositoryMapper.selectOne(harborRepository);
            if (repository == null) {
                return;
            }
            //如果存在harbor仓库，则容量限制
            //判断harbor中是否存在当前用户
//            Map<String, Object> paramMap = new HashMap<>(1);
//            paramMap.put("id", repository.getHarborId());

            //获取quotas id
            Integer projectQuotasId = getProjectQuotasId(repository.getCode(), allHarborQuotas);
            if (projectQuotasId == null) {
                LOGGER.error("{} Quotas Id is null", repository.getCode());
            }


            // v1  {"hard":{"count":101,"storage":104857600}}
            // v2 {"hard":{"storage":193986560}}
            Map<String, Object> hard = new HashMap<>(1);
            Map<String, Object> storage = new HashMap<>(1);
            storage.put("storage", HarborUtil.getStorageLimit(limit, HarborConstants.GB));
            hard.put("hard", storage);
            ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_QUOTAS, null, null, true, projectQuotasId);
        });
    }

    private Integer getProjectQuotasId(String code, List<QuotasVO> allHarborQuotas) {
        List<QuotasVO> quotasVOS = allHarborQuotas.stream().filter(quotasVO -> StringUtils.equalsIgnoreCase(quotasVO.getRef().getName(), code)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(quotasVOS)) {
            return quotasVOS.get(0).getId();
        } else {
            return null;
        }
    }
}
