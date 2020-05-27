package org.hrds.rdupm.nexus.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;

import java.util.List;

/**
 * 制品库_nexus权限表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusAuthService {

    /**
     * 权限列表查询
     * @param pageRequest 分页参数
     * @param nexusAuth 查询参数
     * @return Page<NexusAuth>
     */
    Page<NexusAuth> pageList(PageRequest pageRequest, NexusAuth nexusAuth);

    /**
     * 分配权限
     * @param projectId 项目Id
     * @param nexusAuthList 权限数据
     */
    void create(Long projectId, List<NexusAuth> nexusAuthList);

    /**
     * 更新权限
     * @param nexusAuth 权限数据
     */
    void update(NexusAuth nexusAuth);

    /**
     * 删除权限
     * @param nexusAuth 权限数据
     */
    void delete(NexusAuth nexusAuth);
}
