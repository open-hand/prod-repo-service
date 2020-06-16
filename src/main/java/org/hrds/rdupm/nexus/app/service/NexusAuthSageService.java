package org.hrds.rdupm.nexus.app.service;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;

/**
 * 删除成员， nexus服务， 权限处理
 * @author weisen.yang@hand-china.com 2020-03-27
 */
public interface NexusAuthSageService {


    /**
     * 删除团队成员， nexus（maven/npm）仓库权限处理
     * @param nexusRepository 仓库信息
     * @param userId 用户Id
     */
    void handlerRepo(NexusRepository nexusRepository, Long userId);

    /**
     * saga 监听 - 权限处理
     * @param nexusRepository 仓库信息
     */
    void handlerRepoAuth(NexusRepository nexusRepository);
}
