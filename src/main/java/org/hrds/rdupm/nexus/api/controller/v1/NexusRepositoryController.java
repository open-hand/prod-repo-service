package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;

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

    @ApiOperation(value = "maven仓库查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/{repositoryId}")
    public ResponseEntity<NexusRepositoryDTO> getMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                           @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                           @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId) {
        return Results.success(nexusRepositoryService.getRepo(organizationId, projectId, repositoryId));
    }

    @ApiOperation(value = "npm仓库查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/npm/repo/{repositoryId}")
    public ResponseEntity<NexusRepositoryDTO> getNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                           @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                           @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId) {
        return Results.success(nexusRepositoryService.getRepo(organizationId, projectId, repositoryId));
    }

    @ApiOperation(value = "maven仓库创建")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/maven/repo")
    public ResponseEntity<NexusRepositoryCreateDTO> createMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        nexusRepoCreateDTO.setOrganizationId(organizationId);
        nexusRepoCreateDTO.setProjectId(projectId);
        nexusRepoCreateDTO.setFormat(NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT);
        nexusRepoCreateDTO.setRepoType(NexusConstants.RepoType.MAVEN);
        return Results.success(nexusRepositoryService.createRepo(organizationId, projectId, nexusRepoCreateDTO));
    }

    @ApiOperation(value = "npm仓库创建")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/npm/repo")
    public ResponseEntity<NexusRepositoryCreateDTO> createNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        nexusRepoCreateDTO.setOrganizationId(organizationId);
        nexusRepoCreateDTO.setProjectId(projectId);
        nexusRepoCreateDTO.setFormat(NexusApiConstants.NexusRepoFormat.NPM_FORMAT);
        nexusRepoCreateDTO.setRepoType(NexusConstants.RepoType.NPM);
        return Results.success(nexusRepositoryService.createRepo(organizationId, projectId, nexusRepoCreateDTO));
    }

    @ApiOperation(value = "maven仓库更新")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/project/{projectId}/maven/repo/{repositoryId}")
    public ResponseEntity<NexusRepositoryCreateDTO> updateMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        nexusRepoCreateDTO.setOrganizationId(organizationId);
        nexusRepoCreateDTO.setProjectId(projectId);
        nexusRepoCreateDTO.setFormat(NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT);
        nexusRepoCreateDTO.setRepoType(NexusConstants.RepoType.MAVEN);
        return Results.success(nexusRepositoryService.updateRepo(organizationId, projectId, repositoryId, nexusRepoCreateDTO));
    }

    @ApiOperation(value = "npm仓库更新")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/project/{projectId}/npm/repo/{repositoryId}")
    public ResponseEntity<NexusRepositoryCreateDTO> updateNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        nexusRepoCreateDTO.setOrganizationId(organizationId);
        nexusRepoCreateDTO.setProjectId(projectId);
        nexusRepoCreateDTO.setFormat(NexusApiConstants.NexusRepoFormat.NPM_FORMAT);
        nexusRepoCreateDTO.setRepoType(NexusConstants.RepoType.NPM);
        return Results.success(nexusRepositoryService.updateRepo(organizationId, projectId, repositoryId, nexusRepoCreateDTO));
    }

    @ApiOperation(value = "maven仓库删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/project/{projectId}/maven/repo/{repositoryId}")
    public ResponseEntity<?> deleteMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                             @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                             @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId) {
        nexusRepositoryService.deleteRepo(organizationId, projectId, repositoryId);
        return Results.success();
    }

    @ApiOperation(value = "npm仓库删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/project/{projectId}/npm/repo/{repositoryId}")
    public ResponseEntity<?> deleteNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                             @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                             @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId) {
        nexusRepositoryService.deleteRepo(organizationId, projectId, repositoryId);
        return Results.success();
    }

    @ApiOperation(value = "maven仓库组创建，获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/group")
    public ResponseEntity<List<NexusRepositoryListDTO>> groupRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listRepoName(query, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "npm仓库组创建，获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/npm/repo/group")
    public ResponseEntity<List<NexusRepositoryListDTO>> groupNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listRepoName(query, NexusConstants.RepoType.NPM));
    }

    @ApiOperation(value = "maven仓库 关联， 获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/related")
    public ResponseEntity<List<NexusRepositoryListDTO>> listRelatedMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                         @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        return Results.success(nexusRepositoryService.listRepoNameAll(projectId, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "npm仓库 关联， 获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/npm/repo/related")
    public ResponseEntity<List<NexusRepositoryListDTO>> listRelatedNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                         @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        return Results.success(nexusRepositoryService.listRepoNameAll(projectId, NexusConstants.RepoType.NPM));
    }

    @ApiOperation(value = "maven仓库 关联")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/maven/repo/related")
    public ResponseEntity<NexusRepositoryRelatedDTO> relatedMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                      @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                      @RequestBody NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {
        validObject(nexusRepositoryRelatedDTO);
        nexusRepositoryRelatedDTO.setRepoType(NexusConstants.RepoType.MAVEN);
        return Results.success(nexusRepositoryService.relatedRepo(organizationId, projectId, nexusRepositoryRelatedDTO));
    }

    @ApiOperation(value = "npm仓库 关联")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/npm/repo/related")
    public ResponseEntity<NexusRepositoryRelatedDTO> relatedNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @RequestBody NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {
        validObject(nexusRepositoryRelatedDTO);
        nexusRepositoryRelatedDTO.setRepoType(NexusConstants.RepoType.NPM);
        return Results.success(nexusRepositoryService.relatedRepo(organizationId, projectId, nexusRepositoryRelatedDTO));
    }

    @ApiOperation(value = "配置指引信息，查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/maven/repo/guide/{repositoryName}")
    public ResponseEntity<NexusGuideDTO> mavenRepoGuide(@ApiParam(value = "仓库名称", required = true) @PathVariable(name = "repositoryName") String repositoryName,
                                                        @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt(NexusRepository.ENCRYPT_KEY) Long repositoryId,
                                                        @ApiParam(value = "showPushFlag 是否返回发布的配置信息") @RequestParam(name = "showPushFlag", defaultValue = "false") Boolean showPushFlag) {
        return Results.success(nexusRepositoryService.mavenRepoGuide(repositoryId, repositoryName, showPushFlag));
    }

    @ApiOperation(value = "仓库生效/失效")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/enable")
    public ResponseEntity<?> nexusRepoEnableAndDisAble(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") Long repositoryId,
                                                                 @ApiParam(value = "失效/启用：N:失效 Y:启用", required = true) @RequestParam(name = "enableFlag") String enableFlag) {
        AssertUtils.notNull(repositoryId, "repositoryId not null");
        AssertUtils.notNull(enableFlag, "enableFlag not null");
        nexusRepositoryService.nexusRepoEnableAndDisAble(organizationId, projectId, repositoryId, enableFlag);
        return Results.success();
    }

    @ApiOperation(value = "CI-流水线-获取项目下仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/ci/repo/list")
    public ResponseEntity<List<NexusRepoDTO>> getRepoByProject(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                               @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                               @ApiParam(value = "制品类型: MAVEN、NPM ", required = true) @RequestParam String repoType,
                                                               @ApiParam(value = "nexus仓库类型: hosted、proxy、group", required = false) @RequestParam(name = "type", required = false) String type) {

        return Results.success(nexusRepositoryService.getRepoByProject(organizationId, projectId, repoType, type));
    }

    @ApiOperation(value = "CI-流水线-获取项目下仓库列表-包含用户信息")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @GetMapping("/{organizationId}/project/{projectId}/ci/repo/user/list")
    public ResponseEntity<List<NexusRepoDTO>> getRepoUserByProject(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                   @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                   @ApiParam(value = "仓库主键list", required = true) @RequestParam List<Long> repositoryIds) {

        return Results.success(nexusRepositoryService.getRepoUserByProject(organizationId, projectId, repositoryIds));
    }

}
