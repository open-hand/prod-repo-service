package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
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
	void createSaga(Long projectId, HarborProjectVo harborProjectVo);

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
	void updateSaga(Long projectId, HarborProjectVo harborProjectVo);

	/***
	 * 项目层--查询镜像仓库列表
	 * @param projectId
	 * @param dto
	 * @return
	 */
	List<HarborRepository> listByProject(Long projectId, HarborRepository dto);

	/***
	 * 组织层--查询镜像仓库列表
	 * @param harborRepository
	 * @param pageRequest
	 * @return
	 */
	Page<HarborRepository> listByOrg( HarborRepository harborRepository,PageRequest pageRequest);

	/***
	 * 删除镜像仓库
	 * @param projectId
	 */
	void delete(Long projectId);

	/***
	 * 保存白名单
	 * @param harborProjectVo
	 * @param harborId
	 */
	void saveWhiteList(HarborProjectVo harborProjectVo, Integer harborId);

	/***
	 * 修改访问级别
	 * @param projectId
	 * @param publicFlag
	 */
	void updatePublicFlag(Long projectId, String publicFlag);

    Boolean checkName(Long projectId, String repositoryName);

}
