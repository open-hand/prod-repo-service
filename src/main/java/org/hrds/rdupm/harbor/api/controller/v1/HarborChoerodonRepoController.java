package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoVo;
import org.hrds.rdupm.harbor.app.service.HarborC7nRepoService;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.app.service.HarborImageTagService;
import org.hrds.rdupm.harbor.domain.entity.HarborAllRepoDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoDTO;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 制品库-Harbor猪齿鱼 API
 *
 * @author mofei.li@hand-china.com 2020/06/11 10:42
 */
@RestController("harborChoerodonRepoController.v1")
@RequestMapping("/v1/harbor-choerodon-repos")
public class HarborChoerodonRepoController extends BaseController {
    @Autowired
    private HarborCustomRepoService harborCustomRepoService;
    @Autowired
    private HarborC7nRepoService harborC7nRepoService;
    @Autowired
    private HarborImageTagService harborImageTagService;

    @ApiOperation(value = "应用服务-查询项目下所有自定义仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/list_all_custom_repo")
    public ResponseEntity<List<HarborCustomRepo>> listAllCustomRepoByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId) {
        List<HarborCustomRepo> list = harborCustomRepoService.listAllCustomRepoByProject(projectId);
        return Results.success(list);
    }

    @ApiOperation(value = "应用服务-查询关联的自定义仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/{appServiceId}/list_related_custom_repo")
    public ResponseEntity<HarborCustomRepo> listRelatedCustomRepoByService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                                           @ApiParam(value = "应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId) {
        return Results.success(harborCustomRepoService.listRelatedCustomRepoOrDefaultByService(projectId, appServiceId));
    }

    @ApiOperation(value = "应用服务-保存关联关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/project/{projectId}/{appServiceId}/save_relation")
    public ResponseEntity saveRelationByService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                @ApiParam(value = "应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId,
                                                @ApiParam(value = "自定义仓库ID", required = true) @Encrypt @RequestParam Long customRepoId) {
        harborCustomRepoService.saveRelationByService(projectId, appServiceId, customRepoId);
        return Results.success();
    }

    @ApiOperation(value = "应用服务-删除关联关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/project/{projectId}/{appServiceId}/delete_relation")
    public ResponseEntity deleteRelationByService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                  @ApiParam(value = "应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId,
                                                  @ApiParam(value = "自定义仓库ID", required = true) @Encrypt @RequestParam Long customRepoId) {
        harborCustomRepoService.deleteRelationByService(projectId, appServiceId, customRepoId);
        return Results.success();
    }

    @ApiOperation(value = "删除应用服务与所有自定义仓库的关联关系")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @DeleteMapping("/project/{projectId}/{appServiceId}/delete_all_relation")
    public ResponseEntity deleteAllRelationByService(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                     @ApiParam(value = "应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId) {
        harborCustomRepoService.deleteAllRelationByService(projectId, appServiceId);
        return Results.success();
    }

    @ApiOperation(value = "仓库配置查询接口")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/{appServiceId}/harbor_repo_config")
    public ResponseEntity<HarborRepoDTO> queryHarborRepoConfig(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                               @ApiParam(value = "应用服务ID", required = true) @PathVariable("appServiceId") Long appServiceId) {
        return Results.success(harborCustomRepoService.getHarborRepoConfig(projectId, appServiceId));
    }

    @ApiOperation(value = "根据Harbor仓库ID查询仓库配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/harbor_config_by_id")
    public ResponseEntity<HarborRepoDTO> queryHarborRepoConfigById(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                                   @ApiParam(value = "仓库ID", required = false) @Encrypt @RequestParam(required = false) Long repoId,
                                                                   @ApiParam(value = "仓库类型", required = true) @RequestParam String repoType) {
        return Results.success(harborCustomRepoService.getHarborRepoConfigByRepoId(projectId, repoId, repoType));
    }

    @ApiOperation(value = "查询项目下所有Harbor仓库")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/all_harbor_config")
    public ResponseEntity<HarborAllRepoDTO> queryAllHarborRepoConfig(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId) {
        return Results.success(harborCustomRepoService.getAllHarborRepoConfigByProject(projectId));
    }

    @ApiOperation(value = "查询Harbor仓库镜像列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/listHarborImage")
    public ResponseEntity<?> queryHarborImages(@ApiParam(value = "仓库ID", required = true) @Encrypt @RequestParam Long repoId,
                                               @ApiParam(value = "仓库类型", required = true) @RequestParam String repoType,
                                               @ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName) {
        return Results.success(harborC7nRepoService.getImagesByRepoId(repoId, repoType, imageName));
    }

    //added 2020.07.06
    @ApiOperation(value = "根据项目ID获取镜像仓库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/listImageRepo")
    public ResponseEntity<List<HarborC7nRepoVo>> listImageRepo(@ApiParam(value = "猪齿鱼项目ID", required = true) @RequestParam("projectId") Long projectId) {
        return Results.success(harborC7nRepoService.listImageRepo(projectId));
    }

    @ApiOperation(value = "根据仓库类型+仓库ID+镜像名称获取获取镜像版本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/listImageTag")
    public ResponseEntity<HarborC7nRepoImageTagVo> listImageTag(@ApiParam(value = "仓库类型", required = true) @RequestParam String repoType,
                                                                @ApiParam(value = "仓库ID", required = true) @Encrypt @RequestParam Long repoId,
                                                                @ApiParam(value = "镜像名称", required = true) @RequestParam String imageName,
                                                                @ApiParam(value = "镜像版本号,模糊查询") @RequestParam(required = false) String tagName) {
        return Results.success(harborC7nRepoService.listImageTag(repoType, repoId, imageName, tagName));
    }

    //added 2020.07.21
    @ApiOperation(value = "根据项目ID+应用服务ID获取镜像版本列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/listImageTagByAppServiceId")
    public ResponseEntity<HarborC7nRepoImageTagVo> listImageTagByAppServiceId(@ApiParam(value = "猪齿鱼项目ID", required = true) @RequestParam Long projectId,
                                                                              @ApiParam(value = "应用服务ID", required = true) @RequestParam Long appServiceId) {
        return Results.success(harborC7nRepoService.listImageTagByAppServiceId(projectId, appServiceId));
    }

    @ApiOperation(value = "项目层/组织层--删除镜像TAG")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping(value = "/image-tag/delete")
    public ResponseEntity delete(@ApiParam(value = "仓库名称") @RequestParam String repoName,
                                 @ApiParam(value = "版本号") @RequestParam String tagName) {
        harborImageTagService.delete(repoName, tagName, true);
        return Results.success();
    }

    //added 2020.10.22

    /***
     * 若应用服务ID数组为空，则所有应用服务都做关联
     * @param projectId 项目ID
     * @param repoId    仓库ID
     * @param repoType  仓库类型 DEFAULT_REPO、CUSTOM_REPO
     * @param appServiceIds 应用服务ID数组
     * @return
     */
    @ApiOperation(value = "应用服务-批量保存关联关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/project/{projectId}/batch_save_relation")
    public ResponseEntity batchSaveRelationByServiceIds(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable("projectId") Long projectId,
                                                        @ApiParam(value = "仓库ID", required = true) @Encrypt @RequestParam Long repoId,
                                                        @ApiParam(value = "仓库类型", required = true) @RequestParam String repoType,
                                                        @ApiParam(value = "应用服务ID列表", required = false) @RequestBody(required = false) List<Long> appServiceIds) {
        harborCustomRepoService.batchSaveRelationByServiceIds(projectId, repoId, repoType, appServiceIds);
        return Results.success();
    }

    @ApiOperation(value = "应用服务-批量保存关联关系")
    @Permission(permissionWithin = true)
    @PostMapping("/harbor_repo_config/by_ids")
    public ResponseEntity<List<HarborRepoDTO>> queryHarborReposByIds(@ApiParam(value = "仓库ids", required = false) @RequestBody(required = false) List<Long> harborConfigIds) {
        return Results.success(harborC7nRepoService.queryHarborReposByIds(harborConfigIds));
    }

}
