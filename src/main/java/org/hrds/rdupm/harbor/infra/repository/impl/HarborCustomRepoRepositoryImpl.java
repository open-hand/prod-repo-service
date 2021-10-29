package org.hrds.rdupm.harbor.infra.repository.impl;

import java.util.Objects;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * 制品库-harbor自定义镜像仓库表 资源库实现
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@Component
public class HarborCustomRepoRepositoryImpl extends BaseRepositoryImpl<HarborCustomRepo> implements HarborCustomRepoRepository {

    @Override
    public Boolean checkName(Long projectId, String repositoryName) {
        HarborCustomRepo harborCustomRepo = this.selectByCondition(Condition.builder(HarborCustomRepo.class).where(Sqls.custom()
                .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                .andEqualTo(HarborCustomRepo.FIELD_REPO_NAME, repositoryName)
        ).build()).stream().findFirst().orElse(null);
        if (Objects.isNull(harborCustomRepo)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
  
}
