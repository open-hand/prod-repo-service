package org.hrds.rdupm.nexus.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;

import java.util.List;

/**
 * 制品库_nexus权限表资源库
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusAuthRepository extends BaseRepository<NexusAuth> {
    /**
     * 获取当前用户，对应仓库的权限角色
     * @param repositoryId 仓库Id
     * @return 角色code
     */
    List<String> getRoleList(Long repositoryId);
}
