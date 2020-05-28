package org.hrds.rdupm.nexus.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hzero.export.vo.ExportParam;

import javax.servlet.http.HttpServletResponse;
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
     * 导出
     * @param pageRequest 分页参数
     * @param nexusAuth 查询参数
     * @param exportParam
     * @param response
     * @return
     */
    Page<NexusAuth> export(PageRequest pageRequest, NexusAuth nexusAuth, ExportParam exportParam, HttpServletResponse response);

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

    /**
     * 创建用户权限-仓库创建与关联时赋权
     * @param userIds 用户Id
     * @param repositoryId 仓库Id
     * @param roleCode 角色code NexusConstants.NexusRoleEnum
     * @return List<NexusAuth>
     */
    List<NexusAuth> createNexusAuth(List<Long> userIds, Long repositoryId, String roleCode);

    /**
     * 定时任务移除过期权限
     */
    void expiredBatchNexusAuth();

    /**
     * 移除过期权限
     */
    void expiredNexusAuth(NexusAuth nexusAuth);

}
