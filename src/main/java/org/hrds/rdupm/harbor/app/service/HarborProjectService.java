package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;

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

	/***
	 * 更新镜像仓库配置
	 * @param projectId
	 * @param harborProjectVo
	 */
	void update(Long projectId, HarborProjectVo harborProjectVo);

	/***
	 * 项目层--查询镜像仓库列表
	 * @param projectId
	 * @return
	 */
	List<HarborRepository> listByProject(Long projectId);

	/***
	 * 组织层--查询镜像仓库列表
	 * @param organizationId
	 * @return
	 */
	List<HarborRepository> listByOrg(Long organizationId);
}
