package org.hrds.rdupm.nexus.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 制品库_nexus仓库信息表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
public interface NexusRepositoryMapper extends BaseMapper<NexusRepository> {

	/**
	 * 查询项目关联或自建的仓库
	 * @param projectId 项目Id
	 * @param repoType 制品库类型
	 * @return 仓库名称列表
	 */
	List<String> getRepositoryByProject(@Param("projectId") Long projectId, @Param("repoType") String repoType);
}
