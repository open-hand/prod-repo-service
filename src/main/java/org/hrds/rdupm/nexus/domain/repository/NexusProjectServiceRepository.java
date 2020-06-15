package org.hrds.rdupm.nexus.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusProjectService;

/**
 * 制品库-项目与nexus服务关系表资源库
 *
 * @author weisen.yang@hand-china.com 2020-06-10 20:33:59
 */
public interface NexusProjectServiceRepository extends BaseRepository<NexusProjectService> {

    void disAbleByProjectId(Long projectId);

    void enableById(Long projectServiceId);
}
