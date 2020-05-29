package org.hrds.rdupm.nexus.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.nexus.infra.mapper.NexusAuthMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 制品库_nexus权限表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@Component
public class NexusAuthRepositoryImpl extends BaseRepositoryImpl<NexusAuth> implements NexusAuthRepository {

    @Autowired
    private NexusAuthMapper nexusAuthMapper;

    @Override
    public List<String> getRoleList(Long repositoryId) {
        return nexusAuthMapper.getRoleList(DetailsHelper.getUserDetails().getUserId(), repositoryId);
    }
}
