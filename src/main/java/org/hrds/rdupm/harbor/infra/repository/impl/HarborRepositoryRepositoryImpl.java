package org.hrds.rdupm.harbor.infra.repository.impl;

import io.choerodon.core.oauth.DetailsHelper;

import java.util.Objects;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * 制品库-harbor镜像仓库表 资源库实现
 *
 * @author xiuhong.chen@hand-china.com 2020-04-22 09:53:19
 */
@Component
public class HarborRepositoryRepositoryImpl extends BaseRepositoryImpl<HarborRepository> implements HarborRepositoryRepository {

    @Override
    public HarborRepository getHarborRepositoryById(Long projectId) {
        // todo scp 上线之前删除
//		Long organizationId = DetailsHelper.getUserDetails().getTenantId();
        HarborRepository harborRepository = this.selectByCondition(Condition.builder(HarborRepository.class).where(Sqls.custom()
//				.andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID,organizationId)
                        .andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectId)
        ).build()).stream().findFirst().orElse(null);
        return harborRepository;
    }

    @Override
    public HarborRepository getHarborRepositoryByHarborId(Long harborId) {
        HarborRepository harborRepository = this.selectByCondition(Condition.builder(HarborRepository.class).where(Sqls.custom()
                .andEqualTo(HarborRepository.FIELD_HARBOR_ID, harborId)
        ).build()).stream().findFirst().orElse(null);
        return harborRepository;
    }

    @Override
    public Boolean checkName(Long projectId, String repositoryName) {
        HarborRepository harborRepository = this.selectByCondition(Condition.builder(HarborRepository.class).where(Sqls.custom()
                .andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectId)
                .andEqualTo(HarborRepository.FIELD_CODE, repositoryName)
        ).build()).stream().findFirst().orElse(null);
        if (Objects.isNull(harborRepository)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
