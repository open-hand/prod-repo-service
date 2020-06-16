package org.hrds.rdupm.nexus.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusProjectService;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库-项目与nexus服务关系表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-06-10 20:33:59
 */
public interface NexusProjectServiceMapper extends BaseMapper<NexusProjectService> {

    /**
     * 更新项目下，所有服务为不启用
     * @param projectId 项目Id
     * @param userId 用户Id
     */
    void disAbleByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);

}
