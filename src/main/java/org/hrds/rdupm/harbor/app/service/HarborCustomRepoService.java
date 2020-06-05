package org.hrds.rdupm.harbor.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;

/**
 * 制品库-harbor自定义镜像仓库表应用服务
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
public interface HarborCustomRepoService {
    /***
     * 校验自定义镜像仓库
     * @param harborCustomRepo 自定义镜像仓库信息
     * @return Boolean
     */
    Boolean checkCustomRepo(HarborCustomRepo harborCustomRepo);

    /**
     * 根据项目ID查询自定义仓库信息
     *
     * @param projectId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return
     */
    List<HarborCustomRepo> listByProjectId(Long projectId);

    /**
     * 根据自定义仓库ID查询详情
     *
     * @param repoId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return
     */
    HarborCustomRepo detailByRepoId(Long repoId);

    /***
     * 项目层-创建自定义镜像仓库
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     */
    void createByProject(Long projectId, HarborCustomRepo harborCustomRepo);

    /***
     * 项目层-删除自定义镜像仓库
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     */
    void deleteByProject(Long projectId, HarborCustomRepo harborCustomRepo);

    /***
     * 项目层-关联应用服务
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     * @param appServiceIds
     */
    void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, Set<Long> appServiceIds);

    /***
     * 项目层-查询关联应用服务列表
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     * @return Page<AppServiceDTO>
     */
    Page<AppServiceDTO> pageRelatedService(Long projectId, HarborCustomRepo harborCustomRepo);


    /***
     * 组织层-创建自定义镜像仓库
     * @param organizationId 猪齿鱼组织ID
     * @param harborCustomRepo 自定义镜像仓库信息
     */
    void createByOrg(Long organizationId, HarborCustomRepo harborCustomRepo);
}
