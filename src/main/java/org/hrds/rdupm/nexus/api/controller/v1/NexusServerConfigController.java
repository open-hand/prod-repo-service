package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

/**
 * 制品库_nexus服务信息配置表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@RestController("nexusServerConfigController.v1")
@RequestMapping("/v1/nexus-server-configs")
public class NexusServerConfigController extends BaseController {

    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;
    @Autowired
    private NexusServerConfigService nexusServerConfigService;

//    @ApiOperation(value = "nexus服务信息配置")
//    @Permission(type = ResourceType.SITE, permissionPublic = true)
//    @PostMapping
//    public ResponseEntity<NexusServerConfig> createServerConfig(@RequestBody NexusServerConfig nexusServerConfig) {
//        validObject(nexusServerConfig);
//        return Results.success(nexusServerConfigService.createServerConfig( nexusServerConfig));
//    }

    @ApiOperation(value = "nexus服务信息配置更新")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @PutMapping
    public ResponseEntity<NexusServerConfig> updateServerConfig(@RequestBody NexusServerConfig nexusServerConfig) {
        validObject(nexusServerConfig);
        return Results.success(nexusServerConfigService.updateServerConfig( nexusServerConfig));
    }

    @ApiOperation(value = "查询 nexus服务信息配置")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping
    public ResponseEntity<NexusServerConfig> queryServerConfig() {
        return Results.success(nexusServerConfigService.queryServerConfig());
    }



}
