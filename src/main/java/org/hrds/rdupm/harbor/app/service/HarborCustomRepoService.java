package org.hrds.rdupm.harbor.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepoDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoDTO;
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
     * 项目层-查询自定义仓库列表（制品库管理页面）
     *
     * @param projectId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return
     */
    List<HarborCustomRepoDTO> listByProjectId(Long projectId);

    /**
     * 组织层-查询自定义仓库列表
     *
     * @param harborCustomRepo
     * @author mofei.li@hand-china.com 2020-06-08 13:45
     * @return
     */
    Page<HarborCustomRepo> listByOrg(HarborCustomRepo harborCustomRepo, PageRequest pageRequest);

    /**
     * 根据自定义仓库ID查询详情
     *
     * @param repoId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return
     */
    HarborCustomRepo detailByRepoId(Long repoId);

    /***
     * 项目层-创建时查询项目下所有应用服务
     * @param projectId 猪齿鱼项目ID
     */
    List<AppServiceDTO> listAllAppServiceByCreate(Long projectId);

    /***
     * 项目层-创建自定义镜像仓库
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     */
    void createByProject(Long projectId, HarborCustomRepo harborCustomRepo);

    /***
     * 项目层-修改自定义镜像仓库
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     */
    void updateByProject(Long projectId, HarborCustomRepo harborCustomRepo);

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
     * @param appServiceIds 关联应用服务id
     */
    void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, Set<Long> appServiceIds);

    /**
     * 查询当前自定义仓库未关联的应用服务
     *
     * @param repoId
     * @author mofei.li@hand-china.com 2020-06-05 15:06
     * @return
     */
    List<AppServiceDTO> getNoRelatedAppService(Long repoId);

    /***
     * 项目层-查询关联应用服务列表
     * @param projectId 猪齿鱼项目ID
     * @param harborCustomRepo 自定义镜像仓库信息
     * @param pageRequest
     * @return Page<AppServiceDTO>
     */
    Page<AppServiceDTO> pageRelatedServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, PageRequest pageRequest);

    /***
     * 组织层-查询关联应用服务列表
     * @param organizationId 猪齿鱼组织ID
     * @param harborCustomRepo 自定义镜像仓库信息
     * @param pageRequest
     * @return Page<AppServiceDTO>
     */
    Page<AppServiceDTO> pageRelatedServiceByOrg(Long organizationId, HarborCustomRepo harborCustomRepo, PageRequest pageRequest);


    //--------------------------------------  提供给猪齿鱼应用服务调用   -------------------------------------//
    /**
     * 查询项目下所有猪齿鱼应用服务
     *
     * @param projectId 猪齿鱼项目ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return List<HarborCustomRepo>
     */
    List<HarborCustomRepo> listAllCustomRepoByProject(Long projectId);

    /**
     * 查询应用服务已关联的自定义仓库，不存在则返回所在项目的默认仓库
     *
     * @param projectId 猪齿鱼项目ID
     * @param appServiceId 应用服务ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return HarborRepoDTO
     */
    HarborRepoDTO listRelatedCustomRepoOrDefaultByService(Long projectId, Long appServiceId);


    /**
     * 查询应用服务未关联的自定义仓库
     *
     * @param appServiceId 应用服务ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return List<HarborCustomRepo>
     */
    List<HarborCustomRepo> listUnRelatedCustomRepoByService(Long appServiceId);
}
