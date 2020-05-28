package org.hrds.rdupm.nexus.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库_nexus日志表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusLogMapper extends BaseMapper<NexusLog> {
    /**
     * 组织层-NPM操作日志列表
     *
     * @param organizationId
     * @param repoType
     * @param projectId
     * @param neRepositoryName
     * @param realName
     * @param operateType
     * @param startDate
     * @param endDate
     * @return List<NexusLog>
     */
    List<NexusLog> listLog(@Param("organizationId") Long organizationId, @Param("repoType") String repoType, @Param("projectId") Long projectId, @Param("neRepositoryName") String neRepositoryName,
                           @Param("realName") String realName, @Param("operateType") String operateType, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                           @Param("repositoryId") Long repositoryId);

}
