package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hzero.core.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 制品库_nexus服务信息配置表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@RestController("nexusServerConfigController.v1")
@RequestMapping("/v1/nexus-server-configs")
public class NexusServerConfigController extends BaseController {

    @Autowired
    private NexusServerConfigService nexusServerConfigService;


    @ApiOperation(value = "项目层-制品库-创建自定义nexus服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/project/{projectId}")
    public ResponseEntity<NexusServerConfig> create(@ApiParam(value = "组织ID", required = true) @PathVariable Long organizationId,
                                                    @ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId,
                                                    @RequestBody NexusServerConfig nexusProjectService) {
        validObject(nexusProjectService);
        return Results.success(nexusServerConfigService.createServerConfig(organizationId, projectId, nexusProjectService));
    }

    @ApiOperation(value = "项目层-制品库-修改nexus服务密码")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/project/{projectId}/updatePwd")
    public ResponseEntity<NexusServerConfig> updatePassword(@ApiParam(value = "组织ID", required = true) @PathVariable Long organizationId,
                                                            @ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId,
                                                            @ApiParam("必输字段configId、旧密码oldPassword、新密码password、确认密码rePassword") @RequestBody NexusServerConfig nexusServerConfig) {
        return Results.success(nexusServerConfigService.updatePwd(organizationId, projectId, nexusServerConfig));
    }

    @ApiOperation(value = "项目层-制品库-nexus服务信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/list")
    public ResponseEntity<List<NexusServerConfig>> queryServerConfig(@ApiParam(value = "组织ID", required = true) @PathVariable Long organizationId,
                                                                     @ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId) {
        return Results.success(nexusServerConfigService.listServerConfig(organizationId, projectId));
    }

    @ApiOperation(value = "项目层-制品库-nexus服务启用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/project/{projectId}/enable")
    public ResponseEntity<?> enableProjectServerConfig(@ApiParam(value = "组织ID", required = true) @PathVariable Long organizationId,
                                                       @ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId,
                                                       @RequestBody NexusServerConfig nexusServerConfig) {

        nexusServerConfigService.enableProjectServerConfig(organizationId, projectId, nexusServerConfig);
        return Results.success();
    }



}
