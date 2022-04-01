package org.hrds.rdupm.nexus.infra.repository.impl;

import java.util.List;

import org.hrds.rdupm.nexus.api.vo.ImmutableProjectInfoVO;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignClientUtils;

/**
 * 制品库_nexus服务信息配置表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Component
public class NexusServerConfigRepositoryImpl extends BaseRepositoryImpl<NexusServerConfig> implements NexusServerConfigRepository {

    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;
    @Override
    public NexusServerConfig queryServiceConfig(Long configId, Long projectId) {
        return nexusServerConfigMapper.queryServiceConfig(configId, projectId);
    }

    @Override
    public NexusServerConfig queryEnableServiceConfig(Long projectId) {
        // 项目启用的nexus配置 -> 租户配置的nexus配置 -> 平台配置的nexus配置
        NexusServerConfig projectConfig = nexusServerConfigMapper.queryEnableProjectServiceConfig(projectId);
        if (projectConfig == null) {
            // （弃用） 项目下没有自己启用的nexus配置。 获取Choerodon默认的
            // 查询租户下的nexus配置
            ImmutableProjectInfoVO immutableProjectInfoVO = FeignClientUtils.doRequest(() -> baseServiceFeignClient.immutableProjectInfoById(projectId), ImmutableProjectInfoVO.class);
            projectConfig = nexusServerConfigMapper.queryEnableOrganizationServiceConfig(immutableProjectInfoVO.getTenantId());
            // 查询平台的nexus配置
            if (projectConfig == null) {
                NexusServerConfig queryConfig = new NexusServerConfig();
                queryConfig.setDefaultFlag(BaseConstants.Flag.YES);
                projectConfig = nexusServerConfigMapper.selectOne(queryConfig);
            }
        }
        return projectConfig;
    }

    @Override
    public NexusServerConfig queryServiceConfigByRepositoryId(Long repositoryId) {
        return nexusServerConfigMapper.queryServiceConfigByRepositoryId(repositoryId);
    }

    @Override
    public List<NexusServerConfig> queryList(Long organizationId, Long projectId) {
        return nexusServerConfigMapper.queryList(organizationId, projectId);
    }
}
