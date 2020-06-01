package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.*;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.swagger.annotation.Permission;
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
                                                                    @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId,
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
                                                                    @ApiParam(value = "仓库主键Id", required = true) @PathVariable(name = "repositoryId") Long repositoryId,
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

    @ApiOperation(value = "maven仓库 关联")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/project/{projectId}/maven/repo/related")
    public ResponseEntity<NexusRepositoryRelatedDTO> relatedMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                              @RequestBody NexusRepositoryRelatedDTO nexusRepositoryRelatedDTO) {
        validObject(nexusRepositoryRelatedDTO);
        nexusRepositoryRelatedDTO.validParam();
        return Results.success(nexusRepositoryService.relatedMavenRepo(organizationId, projectId, nexusRepositoryRelatedDTO));
    }

    @ApiOperation(value = "maven仓库列表，当前项目下所有")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/self/all")
    public ResponseEntity<List<NexusRepositoryDTO>> listMavenRepoAll(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                         @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                         NexusRepositoryQueryDTO queryDTO) {
        queryDTO.setProjectId(projectId);
        queryDTO.setOrganizationId(organizationId);
        queryDTO.setRepoType(NexusConstants.RepoType.MAVEN);
        return Results.success(nexusRepositoryService.listRepoAll(queryDTO, NexusConstants.RepoQueryData.REPO_PROJECT));
    }

    @ApiOperation(value = "maven仓库列表，项目之外的其它仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/other")
    public ResponseEntity<Page<NexusRepositoryDTO>> listOtherRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                  @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                  NexusRepositoryQueryDTO queryDTO,
                                                                  @ApiIgnore PageRequest pageRequest) {
        queryDTO.setProjectId(projectId);
        queryDTO.setOrganizationId(organizationId);
        queryDTO.setRepoType(NexusConstants.RepoType.MAVEN);
        return Results.success(nexusRepositoryService.listRepo(pageRequest, queryDTO, NexusConstants.RepoQueryData.REPO_EXCLUDE_PROJECT));
    }

    @ApiOperation(value = "maven仓库组创建，获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/group")
    public ResponseEntity<List<NexusRepositoryDTO>> groupRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listRepoName(query, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "npm仓库组创建，获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/npm/repo/group")
    public ResponseEntity<List<NexusRepositoryDTO>> groupNpmRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                              @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listRepoName(query, NexusConstants.RepoType.NPM));
    }

    @ApiOperation(value = "maven仓库 关联， 获取仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/related")
    public ResponseEntity<List<NexusRepositoryDTO>> listRelatedMavenRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                         @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        return Results.success(nexusRepositoryService.listRepoNameAll(projectId, true, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "获取当前项目关联列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/current")
    public ResponseEntity<List<NexusRepositoryDTO>> listRepoByProjectId(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                      @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        return Results.success(nexusRepositoryService.listRepoName(query, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "获取nexus服务所有仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/maven/repo/all")
    public ResponseEntity<List<NexusRepositoryDTO>> listRepoAll() {
        return Results.success(nexusRepositoryService.listRepoNameAll(null, false, NexusConstants.RepoType.MAVEN));
    }

    @ApiOperation(value = "包上传，仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/project/{projectId}/maven/repo/component")
    public ResponseEntity<List<NexusRepositoryDTO>> listComponentRepo(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                                      @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId) {
        return Results.success(nexusRepositoryService.listComponentRepo(projectId));
    }

    @ApiOperation(value = "配置指引信息，查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/maven/repo/guide/{repositoryName}")
    public ResponseEntity<NexusGuideDTO> mavenRepoGuide(@ApiParam(value = "仓库名称", required = true) @PathVariable(name = "repositoryName") String repositoryName,
                                                        @ApiParam(value = "showPushFlag 是否返回发布的配置信息") @RequestParam(name = "showPushFlag", defaultValue = "false") Boolean showPushFlag) {
        return Results.success(nexusRepositoryService.mavenRepoGuide(repositoryName, showPushFlag));
    }
}
