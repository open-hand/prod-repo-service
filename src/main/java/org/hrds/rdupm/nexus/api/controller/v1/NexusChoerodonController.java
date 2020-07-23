package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.C7nNexusComponentDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusRepoDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusServerDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepoDTO;
import org.hrds.rdupm.nexus.app.service.NexusChoerodonService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.model.NexusComponentQuery;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 制品库_nexus 猪齿鱼 管理 API
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@RequestMapping("/v1/nexus-repositorys")
@RestController("nexusChoerodonController.v1")
public class NexusChoerodonController {

    @Autowired
    private NexusChoerodonService nexusChoerodonService;

    @ApiOperation(value = "choerodon-获取项目下nexus服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/choerodon/{organizationId}/project/{projectId}/nexus/server/list")
    public ResponseEntity<List<C7nNexusServerDTO>> getNexusServerByProject(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                           @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {

        return Results.success(nexusChoerodonService.getNexusServerByProject(organizationId, projectId));
    }

    @ApiOperation(value = "choerodon-获取nexus服务下、项目下的maven仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/choerodon/{organizationId}/project/{projectId}/repo/maven/list")
    public ResponseEntity<List<C7nNexusRepoDTO>> getRepoByConfig(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                 @ApiParam(value = "服务配Id", required = true) @RequestParam(name = "configId") @Encrypt(NexusServerConfig.ENCRYPT_KEY) Long configId) {

        return Results.success(nexusChoerodonService.getRepoByConfig(organizationId, projectId, configId, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "choerodon-获取maven仓库下的groupId")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/choerodon/{organizationId}/project/{projectId}/repo/maven/groupId")
    public ResponseEntity<List<String>> listMavenGroup(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                       @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                       @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                       @ApiParam(value = "groupId", required = false) @RequestParam(name = "groupId", required = false) String groupId) {
        return Results.success(nexusChoerodonService.listMavenGroup(organizationId, projectId, repositoryId, groupId));
    }

    @ApiOperation(value = "choerodon-获取maven仓库下的artifactId")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/choerodon/{organizationId}/project/{projectId}/repo/maven/artifactId")
    public ResponseEntity<List<String>> listMavenArtifactId(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                            @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                            @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                            @ApiParam(value = "artifactId", required = false) @RequestParam(name = "artifactId", required = false) String artifactId) {
        return Results.success(nexusChoerodonService.listMavenArtifactId(organizationId, projectId, repositoryId, artifactId));
    }

    @ApiOperation(value = "choerodon-获取maven仓库下的包列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/choerodon/{organizationId}/project/{projectId}/repo/maven/components")
    public ResponseEntity<List<C7nNexusComponentDTO>> listMavenComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                          @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                          @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                                          @ApiParam(value = "groupId", required = false) @RequestParam(name = "groupId", required = false) String groupId,
                                                                          @ApiParam(value = "artifactId", required = false) @RequestParam(name = "artifactId", required = false) String artifactId,
                                                                          @ApiParam(value = "versionRegular", required = false) @RequestParam(name = "versionRegular", required = false) String versionRegular) {
        return Results.success(nexusChoerodonService.listMavenComponents(organizationId, projectId, repositoryId, NexusConstants.RepoType.MAVEN, groupId, artifactId, versionRegular));
    }
}
