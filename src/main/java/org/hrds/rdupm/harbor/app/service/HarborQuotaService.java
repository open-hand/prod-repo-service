package org.hrds.rdupm.harbor.app.service;

import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:27 下午
 */
public interface HarborQuotaService {


	/***
	 * 修改项目资源配额
	 * @param projectId
	 * @param harborProjectVo
	 */
	void updateProjectQuota(Long projectId, HarborProjectVo harborProjectVo);

	/***
	 * 全局--更新项目资源配额
	 * @param harborProjectVo
	 */
	void updateGlobalQuota(HarborProjectVo harborProjectVo);


	/***
	 * 保存存储配置
	 * @param harborProjectVo
	 * @param harborId
	 */
	void saveQuota(HarborProjectVo harborProjectVo, Integer harborId);

	/***
	 * 根据项目ID获取资源配额
	 * @param projectId
	 * @return
	 */
	HarborQuotaVo getProjectQuota(Long projectId);

	/***
	 * 获取全局资源配额
	 * @return
	 */
	HarborQuotaVo getGlobalQuota();
}
