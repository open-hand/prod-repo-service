package org.hrds.rdupm.harbor.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库-harbor镜像仓库表Mapper
 *
 * @author xiuhong.chen@hand-china.com 2020-04-22 09:53:19
 */
public interface HarborRepositoryMapper extends BaseMapper<HarborRepository> {

	/***
	 * 根据projectId更新harborId
	 * @param projectId
	 * @param harborId
	 */
	void updateHarborIdByProjectId(@Param("projectId") Long projectId, @Param("harborId") Integer harborId);

	List<HarborRepository> selectRepoNoAuth();
}
