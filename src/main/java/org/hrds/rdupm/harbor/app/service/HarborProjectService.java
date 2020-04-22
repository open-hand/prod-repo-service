package org.hrds.rdupm.harbor.app.service;

import java.util.Map;

import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 10:53 上午
 */
public interface HarborProjectService {

	/***
	 * 创建镜像仓库
	 * @param projectId 猪齿鱼项目ID
	 * @param harborProjectVo 镜像仓库信息
	 */
	void create(Long projectId, HarborProjectVo harborProjectVo);

	/***
	 * 根据镜像仓库ID查询明细
	 * @param harborId
	 * @return
	 */
	HarborProjectVo detail(Long harborId);

	void update(Long projectId, HarborProjectVo harborProjectVo);
}
