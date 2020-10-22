package org.hrds.rdupm.harbor.infra.repository.impl;

import java.util.List;
import javax.annotation.Resource;

import org.hrds.rdupm.harbor.infra.mapper.HarborRepoServiceMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 制品库-harbor仓库服务关联表 资源库实现
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@Component
public class HarborRepoServiceRepositoryImpl extends BaseRepositoryImpl<HarborRepoService> implements HarborRepoServiceRepository {

    @Resource
    private HarborRepoServiceMapper harborRepoServiceMapper;

    @Override
    public void deleteRelationByProjectId(Long projectId) {
        harborRepoServiceMapper.deleteRelationByProjectId(projectId);
    }

    @Override
    public void updateProjectShareByProjectId(Long projectId, Boolean projectShare, Long repoId) {
        harborRepoServiceMapper.updateProjectShareByProjectId(projectId,projectShare.toString(),repoId);
    }

    @Override
    public void deleteOtherRelationByService(Long projectId, List<Long> appServiceIds, Long repoId) {
        harborRepoServiceMapper.deleteOtherRelationByService(projectId,appServiceIds,repoId);
    }
}
