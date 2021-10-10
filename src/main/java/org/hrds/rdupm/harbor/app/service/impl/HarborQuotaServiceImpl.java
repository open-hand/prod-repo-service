package org.hrds.rdupm.harbor.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.util.*;

import com.google.gson.Gson;

import io.choerodon.core.exception.CommonException;

import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.api.vo.QuotasVO;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:27 下午
 */
@Service
public class HarborQuotaServiceImpl implements HarborQuotaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HarborQuotaServiceImpl.class);

    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    @Autowired
    private HarborHttpClient harborHttpClient;

    @Override
    public void updateProjectQuota(Long projectId, HarborProjectVo harborProjectVo) {
        HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
        if (harborRepository == null) {
            throw new CommonException("error.harbor.project.not.exist");
        }
        this.saveQuota(harborProjectVo, harborRepository.getHarborId().intValue());
    }

    @Override
    public void updateGlobalQuota(HarborProjectVo harborProjectVo) {
        Long storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(), harborProjectVo.getStorageUnit());
        Map<String, Object> bodyMap = new HashMap<>(2);
        bodyMap.put("count_per_project", harborProjectVo.getCountLimit());
        bodyMap.put("storage_per_project", storageLimit);
        harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_GLOBAL_QUOTA, null, bodyMap, true);
    }

    /***
     * 保存存储容量配置
     * @param harborProjectVo
     * @param harborId
     */
    @Override
    public void saveQuota(HarborProjectVo harborProjectVo, Integer harborId) {
        Long storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(), harborProjectVo.getStorageUnit());
        Map<String, Object> qutoaObject = new HashMap<>(1);
        Map<String, Object> hardObject = new HashMap<>(2);
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            hardObject.put("count", harborProjectVo.getCountLimit());
        }
        hardObject.put("storage", storageLimit);
        qutoaObject.put("hard", hardObject);
        harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT_QUOTA, null, qutoaObject, true, harborId);
    }

    @Override
    public HarborQuotaVo getProjectQuota(Long projectId) {
        HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
        if (harborRepository == null) {
            throw new CommonException("error.harbor.project.not.exist");
        }
        ResponseEntity<String> quotaResponseEntity = null;
        //获取存储容量
        try {
            quotaResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_SUMMARY, null, null, true, harborRepository.getHarborId());
        } catch (Exception e) {
            return new HarborQuotaVo(-1, 0, -1L, -1, "B", 0L, new BigDecimal(0), "B");
        }
        Map<String, Object> summaryMap = new Gson().fromJson(quotaResponseEntity.getBody(), Map.class);
        if (summaryMap == null) {
            return new HarborQuotaVo(-1, 0, -1L, -1, "B", 0L, new BigDecimal(0), "B");
        }
        Map<String, Object> quotaMap = (Map<String, Object>) summaryMap.get("quota");
        Map<String, Object> hardMap = (Map<String, Object>) quotaMap.get("hard");
        Map<String, Object> usedMap = (Map<String, Object>) quotaMap.get("used");
        if (hardMap == null || usedMap == null) {
            return new HarborQuotaVo(-1, 0, -1L, -1, "B", 0L, new BigDecimal(0), "B");
        }
        Double hardCount = (Double) hardMap.get("count");
        Long hardStorage = ((Double) hardMap.get("storage")).longValue();
        Double usedCount = (Double) usedMap.get("count");
        Long usedStorage = ((Double) usedMap.get("storage")).longValue();

        HarborQuotaVo harborQuotaVo = new HarborQuotaVo();
        harborQuotaVo.setCountLimit(hardCount == null ? 0 : hardCount.intValue());
        harborQuotaVo.setUsedCount(usedCount == null ? 0 : usedCount.intValue());
        harborQuotaVo.setStorageLimit(hardStorage);
        harborQuotaVo.setUsedStorage(usedStorage);

        Map<String, Object> storageLimitMap = HarborUtil.getStorageNumUnit(hardStorage);
        harborQuotaVo.setStorageNum(((Long) storageLimitMap.get("storageNum")).intValue());
        harborQuotaVo.setStorageUnit((String) storageLimitMap.get("storageUnit"));
        Map<String, Object> usedStorageMap = HarborUtil.getUsedStorageNumUnit(usedStorage);
        harborQuotaVo.setUsedStorageNum((BigDecimal) usedStorageMap.get("storageNum"));
        harborQuotaVo.setUsedStorageUnit((String) usedStorageMap.get("storageUnit"));

        return harborQuotaVo;
    }

    @Override
    public HarborQuotaVo getGlobalQuota() {
        ResponseEntity<String> quotaResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_GLOBAL_QUOTA, null, null, true);
        Map<String, Object> quotaMap = new Gson().fromJson(quotaResponseEntity.getBody(), Map.class);
        if (quotaMap == null) {
            return null;
        }
        Map<String, Object> countMap = (Map<String, Object>) quotaMap.get("count_per_project");
        Map<String, Object> storageMap = (Map<String, Object>) quotaMap.get("storage_per_project");
        Double hardCount = (Double) countMap.get("value");
        Long hardStorage = ((Double) storageMap.get("value")).longValue();

        HarborQuotaVo harborQuotaVo = new HarborQuotaVo();
        harborQuotaVo.setCountLimit(hardCount.intValue());
        harborQuotaVo.setStorageLimit(hardStorage);

        Map<String, Object> storageLimitMap = HarborUtil.getStorageNumUnit(hardStorage);
        harborQuotaVo.setStorageNum(((Long) storageLimitMap.get("storageNum")).intValue());
        harborQuotaVo.setStorageUnit((String) storageLimitMap.get("storageUnit"));

        return harborQuotaVo;
    }

    @Override
    public List<QuotasVO> getAllHarborQuotas() {
//        {
//            "id": 922,
//                "ref": {
//            "id": 951,
//                    "name": "tttt-xxx",
//                    "owner_name": "admin"
//        },
//            "creation_time": "2021-10-09T08:01:09.935986Z",
//                "update_time": "2021-10-09T08:01:09.935986Z",
//                "hard": {
//            "storage": 13194139533312
//        },
//            "used": {
//            "storage": 0
//        }
//        },

        List<QuotasVO> quotasVOS = new ArrayList<>();
        //获取存储容量  循环查询 查询所有

        ResponseEntity<String> userResponse = null;
        Integer page = 1;
        Integer pageSize = 100;
        try {
            do {
                Map<String, Object> paramMap = new HashMap<>(1);
                paramMap.put("page", page);
                paramMap.put("page_size", pageSize);
                userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_QUOTAS, paramMap, null, true);
                if (userResponse != null && StringUtils.isNotEmpty(userResponse.getBody())) {
                    List<QuotasVO> quotasVOList = JsonHelper.unmarshalByJackson(userResponse.getBody(), new TypeReference<List<QuotasVO>>() {
                    });
                    if (!CollectionUtils.isEmpty(quotasVOList)) {
                        quotasVOS.addAll(quotasVOList);
                    }
                }
                page = ++page;
            } while (userResponse != null && userResponse.getBody() != null && !StringUtils.equalsIgnoreCase("null", userResponse.getBody()));
        } catch (Exception e) {
            LOGGER.error("error.query.harbors.quotas", e);
            return Collections.EMPTY_LIST;
        }
        return quotasVOS;
    }

}
