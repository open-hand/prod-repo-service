package org.hrds.rdupm.nexus.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 制品库_nexus服务信息配置表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusServerConfigMapper extends BaseMapper<NexusServerConfig> {

    /**
     * 查询服务配置数据
     * @param configId 主键Id
     * @param projectId 项目Id
     * @return NexusServerConfig 服务配置信息
     */
    NexusServerConfig queryServiceConfig(@Param("configId") Long configId, @Param("projectId") Long projectId);

    NexusServerConfig queryEnableProjectServiceConfig(@Param("projectId") Long projectId);

    NexusServerConfig queryEnableOrganizationServiceConfig(@Param("organizationId") Long organizationId);

    /**
     * 查询nexus服务配置信息，通过仓库Id
     * @param repositoryId 仓库Id
     * @return NexusServerConfig 服务配置信息
     */
    NexusServerConfig queryServiceConfigByRepositoryId(@Param("repositoryId") Long repositoryId);


    /**
     * 查询项目下，nexus服务配置信息
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @return List<NexusServerConfig>
     */
    List<NexusServerConfig> queryList(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId);

}
