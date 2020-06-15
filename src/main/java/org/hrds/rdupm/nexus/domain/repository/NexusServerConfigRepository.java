package org.hrds.rdupm.nexus.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 制品库_nexus服务信息配置表资源库
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusServerConfigRepository extends BaseRepository<NexusServerConfig> {
    /**
     * 查询服务配置数据
     * @param configId 主键Id
     * @param projectId 项目Id
     * @return NexusServerConfig 服务配置信息
     */
    NexusServerConfig queryServiceConfig(Long configId, Long projectId);

    /**
     * 查询当前项目，启用的nexus服务配置数据
     * @param projectId 项目Id
     * @return NexusServerConfig 服务配置信息
     */
    NexusServerConfig queryEnableServiceConfig(Long projectId);

    /**
     * 查询nexus服务配置信息，通过仓库Id
     * @param repositoryId 仓库Id
     * @return NexusServerConfig 服务配置信息
     */
    NexusServerConfig queryServiceConfigByRepositoryId(Long repositoryId);

    /**
     * 查询项目下，nexus服务配置信息
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @return List<NexusServerConfig>
     */
    List<NexusServerConfig> queryList(Long organizationId, Long projectId);

}
