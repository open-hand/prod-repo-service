package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.api.dto.C7nNexusComponentDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusRepoDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusServerDTO;
import org.hrds.rdupm.nexus.client.nexus.model.NexusComponentQuery;

import java.util.List;

/**
 * 制品库_nexus 猪齿鱼接口应用服务
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
public interface NexusChoerodonService {
    /**
     * choerodon-获取项目下nexus服务列表
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @return  List<C7nNexusServerDTO>
     */
    List<C7nNexusServerDTO> getNexusServerByProject(Long organizationId, Long projectId);

    /**
     * choerodon-获取nexus服务下、项目下的maven仓库
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @param configId nexus服务配置ID
     * @param repoType 仓库类型
     * @return List<C7nNexusRepoDTO>
     */
    List<C7nNexusRepoDTO> getRepoByConfig(Long organizationId, Long projectId, Long configId, String repoType);

    /**
     * maven/npm 版本查询
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @param repositoryId 仓库Id
     * @param repoType 仓库类型
     * @param groupId groupId
     * @param artifactId artifactId/name
     * @param versionRegular 版本正则
     * @return List<C7nNexusComponentDTO>
     */
    List<C7nNexusComponentDTO> listMavenComponents(Long organizationId, Long projectId, Long repositoryId, String repoType, String groupId, String artifactId, String versionRegular);

    /**
     * 查询groupId
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @param repositoryId 仓库Id
     * @param groupId groupId
     * @return List<String>
     */
    List<String> listMavenGroup(Long organizationId, Long projectId, Long repositoryId, String groupId);

    /**
     * 查询artifactId
     * @param organizationId 组织Id
     * @param projectId 项目Id
     * @param repositoryId 仓库Id
     * @param artifactId artifactId
     * @return List<String>
     */
    List<String> listMavenArtifactId(Long organizationId, Long projectId, Long repositoryId, String artifactId);

}
