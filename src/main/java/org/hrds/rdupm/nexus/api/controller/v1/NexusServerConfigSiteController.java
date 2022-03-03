package org.hrds.rdupm.nexus.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 制品库_nexus服务信息配置表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@RestController
@RequestMapping("/v1/site/nexus-server-configs")
public class NexusServerConfigSiteController extends BaseController {

    @Autowired
    private NexusServerConfigService nexusServerConfigService;


    @ApiOperation(value = "平台层-制品库-校验nexus服务名称")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/check_name")
    public ResponseEntity<Boolean> checkName(@RequestParam("serverName") String serverName) {

        return Results.success(nexusServerConfigService.checkName(serverName));
    }

    @ApiOperation(value = "平台层-制品库-创建自定义nexus服务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<NexusServerConfig> create(@RequestBody NexusServerConfig nexusProjectService) {
        validObject(nexusProjectService);
        return Results.success(nexusServerConfigService.createSiteServerConfig(nexusProjectService));
    }

    @ApiOperation(value = "平台层-制品库-更新自定义nexus服务")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/{config_id}")
    public ResponseEntity<NexusServerConfig> update(
            @PathVariable(value = "config_id") Long configId,
            @RequestBody NexusServerConfig nexusProjectService) {
        validObject(nexusProjectService);
        return Results.success(nexusServerConfigService.updateSiteServerConfig(configId, nexusProjectService));
    }

    @ApiOperation(value = "平台层-制品库-nexus服务信息列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<List<NexusServerConfig>> queryServerConfig() {
        return Results.success(nexusServerConfigService.listSiteServerConfig());
    }

    @ApiOperation(value = "平台层-制品库-更新默认nexus配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/{config_id}/default")
    public ResponseEntity<Void> updateDefaultServer(@PathVariable(value = "config_id") Long configId) {
        nexusServerConfigService.updateDefaultServer(configId);
        return ResponseEntity.noContent().build();
    }

}
