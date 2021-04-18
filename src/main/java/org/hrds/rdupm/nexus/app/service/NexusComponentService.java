package org.hrds.rdupm.nexus.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 制品库_nexus 包信息应用服务
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusComponentService {

    /**
     * 包列表查询
     *
     * @param organizationId 组织ID
     * @param projectId      项目Id
     * @param deleteFlag     删除判断flag  true：需要返回是否允许删除标识  false: 不返回
     * @param componentQuery 查询参数
     * @param pageRequest    分页参数
     * @return Page<NexusServerComponentInfo>
     */
    Page<NexusServerComponentInfo> listComponents(Long organizationId,
                                                  Long projectId,
                                                  Boolean deleteFlag,
                                                  NexusComponentQuery componentQuery,
                                                  PageRequest pageRequest);

    /**
     * npm-包列表-版本列表查询
     *
     * @param organizationId 组织ID
     * @param projectId      项目Id
     * @param deleteFlag     删除判断flag  true：需要返回是否允许删除标识  false: 不返回
     * @param componentQuery 查询参数
     * @param pageRequest    分页参数
     * @return Page<NexusServerComponent>
     */
    Page<NexusServerComponent> listComponentsVersion(Long organizationId,
                                                     Long projectId,
                                                     Boolean deleteFlag,
                                                     NexusComponentQuery componentQuery,
                                                     PageRequest pageRequest);

    /**
     * 包删除
     *
     * @param organizationId 组织ID
     * @param projectId      项目Id
     * @param repositoryId   仓库Id
     * @param componentIds   待输出数据
     */
    void deleteComponents(Long organizationId, Long projectId, Long repositoryId, List<String> componentIds);


    void componentsUpload(Long organizationId, Long projectId,
                          NexusServerComponentUpload componentUpload, String filePath, MultipartFile assetPom);

    /**
     * NPM 包上传
     *
     * @param organizationId 组织ID
     * @param projectId      项目Id
     * @param repositoryId   仓库Id
     * @param assetTgz       tgz包文件
     */

    void npmComponentsUpload(Long organizationId, Long projectId, Long repositoryId, String filePath);

    /**
     * jar引入， pom配置
     *
     * @param componentInfo jar引入
     * @return NexusComponentGuideDTO
     */
    NexusComponentGuideDTO componentGuide(NexusServerComponentInfo componentInfo);
}
