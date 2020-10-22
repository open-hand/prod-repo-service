package org.hrds.rdupm.harbor.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;

/**
 * 制品库-harbor仓库服务关联表资源库
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
public interface HarborRepoServiceRepository extends BaseRepository<HarborRepoService> {

    void deleteRelationByProjectId(Long projectId);

    void updateProjectShareByProjectId(Long projectId, Boolean projectShare, Long repoId);

    void deleteOtherRelationByService(Long projectId, List<Long> appServiceIds, Long repoId);
}
