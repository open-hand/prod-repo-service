package org.hrds.rdupm.harbor.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hzero.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;


/**
 * 制品库-harbor自定义镜像仓库表 管理 API
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@RequestMapping("/v1/projects/{project_id}/custom_repos")
@RestController
public class HarborCustomRepoProjectController extends BaseController {
    @Autowired
    private HarborCustomRepoService harborCustomRepoService;


    @ApiOperation(value = "组织层-查询关联应用服务列表")
    @Permission(permissionWithin = true)
    @GetMapping("/{repo_id}/basic_info_internal")
    public ResponseEntity<HarborCustomRepo> queryById(@PathVariable("project_id") Long projectId,
                                                       @PathVariable("repo_id") Long repoId) {
        return ResponseEntity.ok(harborCustomRepoService.queryById(projectId, repoId));
    }

}
