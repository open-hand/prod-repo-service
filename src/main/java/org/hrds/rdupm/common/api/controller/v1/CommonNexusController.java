package org.hrds.rdupm.common.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.common.api.vo.UserNexusInfo;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/9/9
 */
@RestController("commonNexusController.v1")
@RequestMapping("/v1/nexus")
public class CommonNexusController {

    @Autowired
    private NexusServerConfigService nexusServerConfigService;


    @ApiOperation(value = "公开的接口，代理通过配置的id 查询nexus的地址")
    @Permission(permissionPublic = true)
    @GetMapping("/service/config")
    public ResponseEntity<NexusServerConfig> queryNexusServiceConfigById(
            @RequestParam(value = "nexusServiceConfigId") Long nexusServiceConfigId) {
        return Results.success(nexusServerConfigService.queryNexusServiceConfigById(nexusServiceConfigId));
    }


    @ApiOperation(value = "公开的接口，记录拉取jar包日志")
    @Permission(permissionPublic = true)
    @PostMapping("/audit/log")
    public ResponseEntity<Void> auditNexusLog(
            @RequestBody UserNexusInfo userNexusInfo) {
        nexusServerConfigService.auditNexusLog(userNexusInfo);
        return Results.success();
    }

}
