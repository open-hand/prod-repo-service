package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;

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

    @ApiOperation(value = "修改制品库-harbor自定义镜像仓库表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<HarborCustomRepo> update(@RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoRepository.updateByPrimaryKeySelective(harborCustomRepo);
        return Results.success(harborCustomRepo);
    }


    @ApiOperation(value = "校验自定义镜像仓库信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/check/custom-repo")
    public ResponseEntity<?> checkCustomRepo(@ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        validObject(harborCustomRepo);
        return Results.success(harborCustomRepoService.checkCustomRepo(harborCustomRepo));
    }


    @ApiOperation(value = "项目层-创建harbor自定义镜像仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/create/{projectId}/")
    public ResponseEntity createByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                            @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        validObject(harborCustomRepo);
        harborCustomRepoService.createByProject(projectId, harborCustomRepo);
        return Results.success();
    }

    @ApiOperation(value = "项目层-删除harbor自定义镜像仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/delete/{projectId}/")
    public ResponseEntity deleteByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                          @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.deleteByProject(projectId,harborCustomRepo);
        return Results.success(harborCustomRepo);

    }

    @ApiOperation(value = "项目层-关联应用服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/relate-service/{projectId}/")
    public ResponseEntity relateServiceByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                 @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo,
                                                 @ApiParam(value = "关联应用服务ID", required = true) @RequestParam Set<Long> appServiceIds) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        harborCustomRepoService.relateServiceByProject(projectId, harborCustomRepo, appServiceIds);
        return Results.success();
    }

    @ApiOperation(value = "项目层-查询关联应用服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/relate-service/{projectId}/")
    public ResponseEntity<Page<AppServiceDTO>> pageRelatedService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                                  @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        Page<AppServiceDTO> page = harborCustomRepoService.pageRelatedService(projectId,harborCustomRepo);
        return Results.success(page);
    }

    @ApiOperation(value = "项目层-查询未关联应用服务列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/no-relate-service/{projectId}/")
    public ResponseEntity<List<AppServiceDTO>> pageNoRelatedService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                                    @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        SecurityTokenHelper.validToken(harborCustomRepo);
        Page<AppServiceDTO> page = harborCustomRepoService.pageRelatedService(projectId,harborCustomRepo);
        return Results.success(page);
    }


    @ApiOperation(value = "组织层-创建harbor自定义镜像仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/create")
    public ResponseEntity createByOrg(@ApiParam(value = "猪齿鱼组织ID", required = true) @PathVariable("organizationId") Long organizationId,
                                      @ApiParam(value = "自定义镜像仓库", required = true) @RequestBody HarborCustomRepo harborCustomRepo) {
        validObject(harborCustomRepo);
        harborCustomRepoService.createByOrg(organizationId, harborCustomRepo);
        return Results.success();
    }
}
