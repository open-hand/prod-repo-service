package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

/**
 * 组件API
 *
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusComponentsApi {

    /**
     * 查询maven组件信息
     *
     * @param componentQuery 查询参数
     * @return List<NexusComponentInfo>
     */
    List<NexusServerComponent> searchMavenComponent(NexusComponentQuery componentQuery);

    /**
     * 查询组件信息 - groovy脚本
     *
     * @param componentQuery 查询参数
     * @return List<NexusComponentInfo>
     */
    List<NexusServerComponent> searchComponentScript(NexusComponentQuery componentQuery);

    /**
     * 查询maven组件信息,分组处理后
     *
     * @param componentQuery 查询参数
     * @return List<NexusComponentInfo>
     */
    List<NexusServerComponentInfo> searchMavenComponentInfo(NexusComponentQuery componentQuery);

    /**
     * 查询npm组件信息
     *
     * @param componentQuery 查询参数
     * @return List<NexusComponentInfo>
     */
    List<NexusServerComponent> searchNpmComponent(NexusComponentQuery componentQuery);

    /**
     * 查询npm组件信息,分组处理后
     *
     * @param componentQuery 查询参数
     * @return List<NexusComponentInfo>
     */
    List<NexusServerComponentInfo> searchNpmComponentInfo(NexusComponentQuery componentQuery);

    /**
     * 删除组件信息
     *
     * @param componentId 组件Id 通过rest api 查询接口返回的Id
     */
    void deleteComponent(String componentId);

    /**
     * 删除组件信息 - groovy脚本
     *
     * @param deleteParam 参数  components：通过groovy脚本查询接口返回的Id     repositoryName：仓库名称
     */
    void deleteComponentScript(NexusComponentDeleteParam deleteParam);

    /**
     * 组件jar包上传， 只支持maven release类型
     *
     * @param componentUpload 上传信息
     */
    void createMavenComponent(NexusServerComponentUpload componentUpload, NexusServer currentNexusServer);

    /**
     * 组件npm tgz包上传
     *
     * @param repositoryName 仓库名称
     * @param streamResource 上传信息
     */
    void createNpmComponent(String repositoryName, InputStreamResource streamResource, NexusServer nexusServer);

    List<NexusServerAsset> findAssets(String neRepositoryName, String path);
}
