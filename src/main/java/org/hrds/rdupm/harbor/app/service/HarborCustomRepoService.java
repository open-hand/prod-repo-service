package org.hrds.rdupm.harbor.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.domain.entity.HarborAllRepoDTO;
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
     * 判断是否存在共享仓库
     *
     * @param projectId
     * @author mofei.li@hand-china.com 2020-06-12 11:24
     * @return
     */
    Boolean existProjectShareCustomRepo(Long projectId);

    /**
     * 项目层-查询自定义仓库列表（制品库管理页面）
     *
     * @param projectId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return List<HarborCustomRepoDTO>
     */
    List<HarborCustomRepoDTO> listByProjectId(Long projectId);

    /**
     * 组织层-查询自定义仓库列表
     *
     * @param harborCustomRepo
     * @param pageRequest
     * @author mofei.li@hand-china.com 2020-06-08 13:45
     * @return Page<HarborCustomRepo>
     */
    Page<HarborCustomRepo> listByOrg(HarborCustomRepo harborCustomRepo, PageRequest pageRequest);

    /**
     * 根据自定义仓库ID查询详情
     *
     * @param repoId
     * @author mofei.li@hand-china.com 2020-06-05 14:30
     * @return HarborCustomRepo
     */
    HarborCustomRepo detailByRepoId(Long repoId);

    /***
     * 项目层-创建时查询未关联仓库的应用服务
     * @param projectId 猪齿鱼项目ID
     * @return
     */
    List<AppServiceDTO> listAppServiceByCreate(Long projectId);

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
     */
    void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo);

    /**
     * 查询当前自定义仓库未关联的应用服务
     *
     * @param repoId
     * @author mofei.li@hand-china.com 2020-06-05 15:06
     * @return List<AppServiceDTO>
     */
    List<AppServiceDTO> getNoRelatedAppService(Long repoId);

    /***
     * 项目层-查询关联应用服务列表
     * @param projectId 猪齿鱼项目ID
     * @param customRepoId 自定义镜像仓库id
     * @param appServiceName 应用服务名称
     * @param appServiceCode 应用服务编码
     * @param pageRequest
     * @return Page<AppServiceDTO>
     */
    Page<AppServiceDTO> pageRelatedServiceByProject(Long projectId, Long  customRepoId, String appServiceName, String appServiceCode, PageRequest pageRequest);

    /**
     * 项目层-删除自定义仓库-应用服务关联关系
     * @param appServiceId 应用服务ID
     * @param harborCustomRepo 自定义仓库
     * @author mofei.li@hand-china.com 2020-06-09 17:47
     * @return
     */
    void deleteRelation(Long appServiceId, HarborCustomRepo harborCustomRepo);

    /***
     * 组织层-查询关联应用服务列表
     * @param organizationId 猪齿鱼组织ID
     * @param customRepoId 自定义镜像仓库id
     * @param pageRequest
     * @return Page<AppServiceDTO>
     */
    Page<AppServiceDTO> pageRelatedServiceByOrg(Long organizationId, Long customRepoId, PageRequest pageRequest);


    //--------------------------------------  提供给猪齿鱼应用服务调用   -------------------------------------//
    /**
     * 应用服务-查询项目下所有猪齿鱼应用服务
     *
     * @param projectId 猪齿鱼项目ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return List<HarborCustomRepo>
     */
    List<HarborCustomRepo> listAllCustomRepoByProject(Long projectId);

    /**
     * 应用服务-查询已关联的自定义仓库
     *
     * @param projectId 猪齿鱼项目ID
     * @param appServiceId 应用服务ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return HarborCustomRepo
     */
    HarborCustomRepo listRelatedCustomRepoOrDefaultByService(Long projectId, Long appServiceId);

    /**
     * 应用服务-保存关联关系
     *
     * @param projectId 猪齿鱼项目ID
     * @param appServiceId 应用服务ID
     * @param customRepoId 自定义仓库ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return
     */
    void saveRelationByService(Long projectId, Long appServiceId, Long customRepoId);

    /**
     * 应用服务-删除关联关系
     *
     * @param projectId 猪齿鱼项目ID
     * @param appServiceId 应用服务ID
     * @param customRepoId 自定义仓库ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return
     */
    void deleteRelationByService(Long projectId, Long appServiceId, Long customRepoId);

    /**
     * 流水线-查询Harbor仓库配置接口
     *
     * @param projectId 猪齿鱼项目ID
     * @param appServiceId 应用服务ID
     * @author mofei.li@hand-china.com 2020-06-08 16:49
     * @return
     */
    HarborRepoDTO getHarborRepoConfig(Long projectId, Long appServiceId);

    /**
     * 根据harbor仓库ID查询仓库配置
     *
     * @param projectId 猪齿鱼项目ID
     * @param repoId 仓库ID
     * @param repoType 仓库类型
     * @return
     */
    HarborRepoDTO getHarborRepoConfigByRepoId(Long projectId, Long repoId, String repoType);

    /**
     * 查询项目所有harbor仓库配置
     *
     * @param projectId
     * @return HarborAllRepoDTO
     */
    HarborAllRepoDTO getAllHarborRepoConfigByProject(Long projectId);

    /**
     * 查询Harbor仓库镜像信息
     *
     * @param repoId
     * @param imageName
     * @return
     */
    List<HarborImageVo> getImagesByRepoId(Long repoId, String imageName);

    /***
     * 批量保存应用服务和仓库的关联关系
     * 若应用服务ID数组为空，则所有应用服务都做关联
     * @param projectId
     * @param repoId
     * @param repoType
     * @param appServiceIds
     */
    void batchSaveRelationByServiceIds(Long projectId, Long repoId, String repoType, List<Long> appServiceIds);
}
