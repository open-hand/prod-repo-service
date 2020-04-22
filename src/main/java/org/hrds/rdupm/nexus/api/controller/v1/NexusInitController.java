package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.nexus.app.service.NexusInitService;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 制品库_nexus仓库 初始化 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@RestController("nexusInitController.v1")
@RequestMapping("/v1/{organizationId}/init")
public class NexusInitController extends BaseController {

    @Autowired
    private NexusInitService nexusInitService;

    @ApiOperation(value = "脚本初始化与更新")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping("/script")
    public ResponseEntity<?> initScript() {
        nexusInitService.initScript();
        return Results.success();
    }

    @ApiOperation(value = "匿名用户-拉取权限初始化：默认给予所有仓库拉取权限")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @PostMapping("/anonymous")
    public ResponseEntity<?> initAnonymous(@RequestBody List<String> repositoryNames) {
        nexusInitService.initAnonymous(repositoryNames);
        return Results.success();
    }
}
