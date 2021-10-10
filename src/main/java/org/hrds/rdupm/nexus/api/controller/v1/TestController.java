package org.hrds.rdupm.nexus.api.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.QuotasVO;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.app.job.NexusCapacityTask;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/10/4
 */
@RestController("testController.v1")
@RequestMapping("/v1/test")
public class TestController {

    @Autowired
    private NexusCapacityTask nexusCapacityTask;
    @Autowired
    private HarborRepositoryMapper harborRepositoryMapper;

    @Autowired
    private HarborHttpClient harborHttpClient;

    @Autowired
    private HarborQuotaService harborQuotaService;

    @GetMapping("/test")
    @Permission(permissionPublic = true)
    public void test() {
        //查询该项目下是否有默认的docker仓库
        HarborRepository harborRepository = new HarborRepository();
        harborRepository.setOrganizationId(1131L);
        harborRepository.setProjectId(228483927390908416L);
        HarborRepository repository = harborRepositoryMapper.selectOne(harborRepository);
        if (repository == null) {
            return;
        }
        List<QuotasVO> allHarborQuotas = harborQuotaService.getAllHarborQuotas();
        Integer projectQuotasId = getProjectQuotasId(repository.getCode(), allHarborQuotas);
        //如果存在harbor仓库，则容量限制
        //判断harbor中是否存在当前用户
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("id", repository.getHarborId());

        // v1  {"hard":{"count":101,"storage":104857600}}
        // v2 {"hard":{"storage":193986560}}
        //先要查到仓库对应的quotas id
        Map<String, Object> hard = new HashMap<>(1);
        Map<String, Object> storage = new HashMap<>(1);
        storage.put("storage", HarborUtil.getStorageLimit(5, HarborConstants.GB));
        hard.put("hard", storage);
        ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_QUOTAS, null, hard, true, projectQuotasId);

//        nexusCapacityTask.harborCapacitylimit(null);
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
