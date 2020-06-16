package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepoDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 制品库-harbor自定义镜像仓库表 管理 API
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@RestController("harborCustomRepoController.v1")
@RequestMapping("/v1/{organizationId}/harbor-custom-repos")
public class HarborCustomRepoController extends BaseController {
    @Autowired
    private HarborCustomRepoService harborCustomRepoService;
    @Autowired
    private HarborCustomRepoRepository harborCustomRepoRepository;


    @ApiOperation(value = "校验自定义镜像仓库信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/check/custom-repo")
    public ResponseEntity<?> checkCustomRepo(@ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        validObject(harborCustomRepo);
        return Results.success(harborCustomRepoService.checkCustomRepo(harborCustomRepo));
    }

    @ApiOperation(value = "判断是否存在共享仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/exist-share/{projectId}")
    public ResponseEntity<?> existProjectShareCustomRepo(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable(value = "projectId") Long projectId) {
        return Results.success(harborCustomRepoService.existProjectShareCustomRepo(projectId));
    }

    @ApiOperation(value = "项目层-查询自定义仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-project/{projectId}")
    public ResponseEntity<List<HarborCustomRepoDTO>> listRepoByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable(value = "projectId") Long projectId) {
        return Results.success(harborCustomRepoService.listByProjectId(projectId));
    }

    @ApiOperation(value = "组织层-查询自定义仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-org")
    public ResponseEntity<Page<HarborCustomRepo>> listRepoByOrg(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable(value = "organizationId") Long organizationId,
                                                                @ApiIgnore @SortDefault(value = HarborCustomRepo.FIELD_PROJECT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        HarborCustomRepo repo = new HarborCustomRepo();
        repo.setOrganizationId(organizationId);
        return Results.success(harborCustomRepoService.listByOrg(repo, pageRequest));
    }

    @ApiOperation(value = "项目层-创建时查询未关联仓库的应用服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-all-services/{projectId}")
    public ResponseEntity<List<AppServiceDTO>> listAppServiceByCreate(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId) {
        return Results.success(harborCustomRepoService.listAppServiceByCreate(projectId));
    }


    @ApiOperation(value = "项目层-创建harbor自定义镜像仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/create/{projectId}")
    public ResponseEntity createByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                          @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        validObject(harborCustomRepo);
        harborCustomRepoService.createByProject(projectId, harborCustomRepo);
        return Results.success();
    }

    @ApiOperation(value = "项目层-查询自定义仓库明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/project/{customRepoId}")
    public ResponseEntity<HarborCustomRepo> detailByProject(@ApiParam(value = "自定义仓库ID", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @PathVariable("customRepoId") Long customRepoId) {
        return Results.success(harborCustomRepoService.detailByRepoId(customRepoId));
    }

    @ApiOperation(value = "项目层-修改自定义仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/update/{projectId}")
    public ResponseEntity updateByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                          @ApiParam(value = "自定义镜像仓库", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.updateByProject(projectId, harborCustomRepo);
        return Results.success();
    }

    @ApiOperation(value = "项目层-删除harbor自定义镜像仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity deleteByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                          @ApiParam(value = "自定义镜像仓库", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @RequestBody HarborCustomRepoDTO harborCustomRepoDTO) {
        HarborCustomRepo harborCustomRepo = new HarborCustomRepo(harborCustomRepoDTO);
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.deleteByProject(projectId,harborCustomRepo);
        return Results.success();

    }

    @ApiOperation(value = "项目层-关联应用服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/relate-service/{projectId}")
    public ResponseEntity relateServiceByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                 @ApiParam(value = "自定义镜像仓库", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.relateServiceByProject(projectId, harborCustomRepo);
        return Results.success();
    }

    @ApiOperation(value = "项目层-查询关联应用服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/relate-service/{projectId}")
    public ResponseEntity<Page<AppServiceDTO>> pageRelatedServiceByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                                           @ApiParam(value = "自定义镜像仓库ID", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @RequestParam Long customRepoId,
                                                                           @ApiParam(value = "应用服务名称") @RequestParam(required = false) String name,
                                                                           @ApiParam(value = "应用服务编码") @RequestParam(required = false) String code,
                                                                           @ApiIgnore PageRequest pageRequest) {
        Page<AppServiceDTO> page = harborCustomRepoService.pageRelatedServiceByProject(projectId, customRepoId, name, code, pageRequest);
        return Results.success(page);
    }

    @ApiOperation(value = "项目层-查询未关联应用服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/no-relate-service/{repoId}")
    public ResponseEntity<List<AppServiceDTO>> pageNoRelatedService(@ApiParam(value = "自定义仓库id", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @PathVariable("repoId") Long repoId) {
        List<AppServiceDTO> unRelatedServices = harborCustomRepoService.getNoRelatedAppService(repoId);
        return Results.success(unRelatedServices);
    }

    @ApiOperation(value = "项目层-删除自定义仓库和应用服务关联关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/delete-relation/{appServiceId}")
    public ResponseEntity deleteRelation(@ApiParam(value = "关联应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId,
                                         @ApiParam(value = "自定义镜像仓库", required = true) @Encrypt(HarborCustomRepo.ENCRYPT_KEY) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.deleteRelation(appServiceId, harborCustomRepo);
        return Results.success();
    }

    @ApiOperation(value = "组织层-查询自定义仓库明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/org/{customRepoId}")
    public ResponseEntity<HarborCustomRepo> detailByOrg(@ApiParam(value = "自定义仓库ID", required = true) @PathVariable("customRepoId") Long customRepoId) {
        return Results.success(harborCustomRepoService.detailByRepoId(customRepoId));
    }

    @ApiOperation(value = "组织层-查询关联应用服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/relate-service")
    public ResponseEntity<Page<AppServiceDTO>> pageRelatedServiceByOrg(@ApiParam(value = "猪齿鱼组织ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                       @ApiParam(value = "自定义镜像仓库", required = true) @RequestParam Long customRepoId,
                                                                       @ApiIgnore PageRequest pageRequest) {
        Page<AppServiceDTO> page = harborCustomRepoService.pageRelatedServiceByOrg(organizationId, customRepoId, pageRequest);
        return Results.success(page);
    }

}
