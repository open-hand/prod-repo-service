package org.hrds.rdupm.harbor.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 制品库-harbor仓库服务关联表 管理 API
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@RestController("harborRepoServiceController.v1")
@RequestMapping("/v1/{organizationId}/harbor-repo-services")
public class HarborRepoServiceController extends BaseController {

    @Autowired
    private HarborRepoServiceRepository harborRepoServiceRepository;

    @ApiOperation(value = "制品库-harbor仓库服务关联表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<HarborRepoService>> list(HarborRepoService harborRepoService, @ApiIgnore @SortDefault(value = HarborRepoService.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<HarborRepoService> list = harborRepoServiceRepository.pageAndSort(pageRequest, harborRepoService);
        return Results.success(list);
    }

    @ApiOperation(value = "制品库-harbor仓库服务关联表明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<HarborRepoService> detail(@PathVariable Long id) {
        HarborRepoService harborRepoService = harborRepoServiceRepository.selectByPrimaryKey(id);
        return Results.success(harborRepoService);
    }

    @ApiOperation(value = "创建制品库-harbor仓库服务关联表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<HarborRepoService> create(@RequestBody HarborRepoService harborRepoService) {
        validObject(harborRepoService);
        harborRepoServiceRepository.insertSelective(harborRepoService);
        return Results.success(harborRepoService);
    }

    @ApiOperation(value = "修改制品库-harbor仓库服务关联表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<HarborRepoService> update(@RequestBody HarborRepoService harborRepoService) {
        SecurityTokenHelper.validToken(harborRepoService);
        harborRepoServiceRepository.updateByPrimaryKeySelective(harborRepoService);
        return Results.success(harborRepoService);
    }

    @ApiOperation(value = "删除制品库-harbor仓库服务关联表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody HarborRepoService harborRepoService) {
        SecurityTokenHelper.validToken(harborRepoService);
        harborRepoServiceRepository.deleteByPrimaryKey(harborRepoService);
        return Results.success();
    }

}
