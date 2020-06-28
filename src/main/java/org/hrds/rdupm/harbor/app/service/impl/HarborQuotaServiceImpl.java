package org.hrds.rdupm.harbor.app.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:27 下午
 */
@Service
public class HarborQuotaServiceImpl implements HarborQuotaService {

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Override
	public void updateProjectQuota(Long projectId, HarborProjectVo harborProjectVo) {
		HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		this.saveQuota(harborProjectVo,harborRepository.getHarborId().intValue());
	}

	@Override
	public void updateGlobalQuota(HarborProjectVo harborProjectVo) {
		Long storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(),harborProjectVo.getStorageUnit());
		Map<String,Object> bodyMap = new HashMap<>(2);
		bodyMap.put("count_per_project",harborProjectVo.getCountLimit());
		bodyMap.put("storage_per_project",storageLimit);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_GLOBAL_QUOTA,null,bodyMap,true);
	}

	/***
	 * 保存存储容量配置
	 * @param harborProjectVo
	 * @param harborId
	 */
	@Override
	public void saveQuota(HarborProjectVo harborProjectVo, Integer harborId){
		Long storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(),harborProjectVo.getStorageUnit());
		Map<String,Object> qutoaObject = new HashMap<>(1);
		Map<String,Object> hardObject = new HashMap<>(2);
		hardObject.put("count",harborProjectVo.getCountLimit());
		hardObject.put("storage",storageLimit);
		qutoaObject.put("hard",hardObject);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT_QUOTA,null,qutoaObject,true,harborId);
	}

	@Override
	public HarborQuotaVo getProjectQuota(Long projectId) {
		HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
		if(harborRepository == null){
			throw new CommonException("error.harbor.project.not.exist");
		}
		ResponseEntity<String> quotaResponseEntity = null;
		//获取存储容量
		try{
			quotaResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_SUMMARY,null,null,true,harborRepository.getHarborId());
		}catch (Exception e){
			return new HarborQuotaVo(-1,0,-1L,-1,"B",0L,new BigDecimal(0),"B");
		}
		Map<String,Object> summaryMap = new Gson().fromJson(quotaResponseEntity.getBody(),Map.class);
		if(summaryMap == null){
			return new HarborQuotaVo(-1,0,-1L,-1,"B",0L,new BigDecimal(0),"B");
		}
		Map<String,Object> quotaMap = (Map<String, Object>) summaryMap.get("quota");
		Map<String,Object> hardMap = (Map<String, Object>) quotaMap.get("hard");
		Map<String,Object> usedMap = (Map<String, Object>) quotaMap.get("used");
		if(hardMap == null || usedMap == null){
			return new HarborQuotaVo(-1,0,-1L,-1,"B",0L,new BigDecimal(0),"B");
		}
		Double hardCount = (Double) hardMap.get("count");
		Long hardStorage = ((Double) hardMap.get("storage")).longValue();
		Double usedCount = (Double) usedMap.get("count");
		Long usedStorage = ((Double) usedMap.get("storage")).longValue();

		HarborQuotaVo harborQuotaVo = new HarborQuotaVo();
		harborQuotaVo.setCountLimit(hardCount.intValue());
		harborQuotaVo.setUsedCount(usedCount.intValue());
		harborQuotaVo.setStorageLimit(hardStorage);
		harborQuotaVo.setUsedStorage(usedStorage);

		Map<String,Object> storageLimitMap = HarborUtil.getStorageNumUnit(hardStorage);
		harborQuotaVo.setStorageNum(((Long) storageLimitMap.get("storageNum")).intValue());
		harborQuotaVo.setStorageUnit((String) storageLimitMap.get("storageUnit"));
		Map<String,Object> usedStorageMap = HarborUtil.getUsedStorageNumUnit(usedStorage);
		harborQuotaVo.setUsedStorageNum((BigDecimal) usedStorageMap.get("storageNum"));
		harborQuotaVo.setUsedStorageUnit((String) usedStorageMap.get("storageUnit"));

		return harborQuotaVo;
	}

	@Override
	public HarborQuotaVo getGlobalQuota() {
		ResponseEntity<String> quotaResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_GLOBAL_QUOTA,null,null,true);
		Map<String,Object> quotaMap = new Gson().fromJson(quotaResponseEntity.getBody(),Map.class);
		if(quotaMap == null){
			return null;
		}
		Map<String,Object> countMap = (Map<String, Object>) quotaMap.get("count_per_project");
		Map<String,Object> storageMap = (Map<String, Object>) quotaMap.get("storage_per_project");
		Double hardCount = (Double) countMap.get("value");
		Long hardStorage = ((Double) storageMap.get("value")).longValue();

		HarborQuotaVo harborQuotaVo = new HarborQuotaVo();
		harborQuotaVo.setCountLimit(hardCount.intValue());
		harborQuotaVo.setStorageLimit(hardStorage);

		Map<String,Object> storageLimitMap = HarborUtil.getStorageNumUnit(hardStorage);
		harborQuotaVo.setStorageNum(((Long) storageLimitMap.get("storageNum")).intValue());
		harborQuotaVo.setStorageUnit((String) storageLimitMap.get("storageUnit"));

		return harborQuotaVo;
	}

}
