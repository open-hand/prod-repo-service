package org.hrds.rdupm.nexus.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerBlobStore;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 制品库_nexus仓库信息表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@RestController("nexusRepositoryController.v1")
@RequestMapping("/v1/nexus-repositorys")
public class NexusRepositoryController extends BaseController {

    @Autowired
    private NexusRepositoryRepository nexusRepository;
    @Autowired
    private NexusRepositoryService nexusRepositoryService;

    @ApiOperation(value = "maven仓库创建")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{organizationId}/project/{projectId}/maven/repo")
    public ResponseEntity<NexusRepositoryCreateDTO> createMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        return Results.success(nexusRepositoryService.createMavenRepo(organizationId, projectId,nexusRepoCreateDTO));
    }

    @ApiOperation(value = "maven仓库更新")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PutMapping("/{organizationId}/project/{projectId}/maven/repo/{repositoryId}")
    public ResponseEntity<NexusRepositoryCreateDTO> updateMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        return Results.success(nexusRepositoryService.updateMavenRepo(organizationId, projectId, repositoryId, nexusRepoCreateDTO));
    }

    @ApiOperation(value = "maven仓库删除")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @DeleteMapping("/{organizationId}/project/{projectId}/maven/repo/{repositoryId}")
    public ResponseEntity<?> deleteMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                             @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                             @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId) {
        nexusRepositoryService.deleteMavenRepo(organizationId, projectId, repositoryId);
        return Results.success();
    }

    @ApiOperation(value = "maven仓库 关联")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{organizationId}/project/{projectId}/maven/repo/related")
    public ResponseEntity<NexusRepositoryRelatedDTO> relatedMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                              @RequestBody NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {
        validObject(nexusRepositoryRelatedDTO);
        nexusRepositoryRelatedDTO.validParam();
        return Results.success(nexusRepositoryService.relatedMavenRepo(organizationId, projectId, nexusRepositoryRelatedDTO));
    }

    @ApiOperation(value = "maven仓库列表，自建或关联的")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/self")
    public ResponseEntity<PageInfo<NexusRepositoryDTO>> listMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                      @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                      NexusRepositoryQueryDTO queryDTO,
                                                                      @ApiIgnore PageRequest pageRequest) {
        queryDTO.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listMavenRepo(pageRequest, queryDTO));
    }

    @ApiOperation(value = "maven仓库列表，项目之外的其它仓库")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/other")
    public ResponseEntity<PageInfo<NexusRepositoryDTO>> listOtherMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                           @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                           NexusRepositoryQueryDTO queryDTO,
                                                                           @ApiIgnore PageRequest pageRequest) {
        queryDTO.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listOtherMavenRepo(pageRequest, queryDTO));
    }

    @ApiOperation(value = "maven仓库列表，获取存储器")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/blob")
    public ResponseEntity<List<NexusServerBlobStore>> listMavenRepoBlob() {
        return Results.success(nexusRepositoryService.listMavenRepoBlob());
    }

    @ApiOperation(value = "maven仓库组创建，获取仓库列表")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/group")
    public ResponseEntity<List<NexusServerRepository>> groupRepo() {
        return Results.success(nexusRepositoryService.groupRepo());
    }

    @ApiOperation(value = "配置指引信息，查询")
    @Permission(permissionPublic = true)
    @GetMapping("maven/repo/guide/{repositoryName}")
    public ResponseEntity<NexusGuideDTO> mavenRepoGuide(@ApiParam(value = "仓库名称", required = true) @PathVariable(name = "repositoryName") String repositoryName,
                                                        @ApiParam(value = "showPushFlag 是否返回发布的配置信息") @RequestParam(name = "showPushFlag", defaultValue = "false") Boolean showPushFlag) {
        return Results.success(nexusRepositoryService.mavenRepoGuide(repositoryName, showPushFlag));
    }
}
