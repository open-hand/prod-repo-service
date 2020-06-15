package org.hrds.rdupm.nexus.infra.repository.impl;

import org.hrds.rdupm.nexus.infra.mapper.NexusProjectServiceMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusProjectService;
import org.hrds.rdupm.nexus.domain.repository.NexusProjectServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 制品库-项目与nexus服务关系表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-06-10 20:33:59
 */
@Component
public class NexusProjectServiceRepositoryImpl extends BaseRepositoryImpl<NexusProjectService> implements NexusProjectServiceRepository {

    @Autowired
    private NexusProjectServiceMapper nexusProjectServiceMapper;

    @Override
    public void disAbleByProjectId(Long projectId) {
        nexusProjectServiceMapper.disAbleByProjectId(projectId);
    }

    @Override
    public void enableById(Long projectServiceId) {

    }
}
