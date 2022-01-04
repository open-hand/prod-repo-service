package org.hrds.rdupm.common.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.common.api.vo.UserNexusInfo;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.nexus.api.vo.NexusRepositoryVO;
import org.hrds.rdupm.nexus.api.vo.NexusServerConfigVO;
import org.hrds.rdupm.nexus.app.service.NexusComponentHandService;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
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

    @Autowired
    private NexusRepositoryService nexusRepositoryService;

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private NexusComponentHandService nexusComponentHandService;


    @ApiOperation(value = "公开的接口，代理通过配置的id 查询nexus的地址")
    @Permission(permissionPublic = true)
    @GetMapping("/service/config")
    public ResponseEntity<NexusServerConfigVO> queryNexusServiceConfigById(
            @RequestParam(value = "config_server_id") Long nexusServiceConfigId) {
        return Results.success(nexusServerConfigService.queryNexusServiceConfigById(nexusServiceConfigId));
    }

    @ApiOperation(value = "公开的接口，通过仓库名查询仓库")
    @Permission(permissionPublic = true)
    @GetMapping("/repository")
    public ResponseEntity<NexusRepositoryVO> queryNexusRepositoryByName(
            @RequestParam(value = "config_server_id") Long nexusServiceConfigId,
            @RequestParam(value = "repository_name") String repositoryName) {
        return Results.success(nexusRepositoryService.queryNexusRepositoryByName(nexusServiceConfigId, repositoryName));
    }

    @ApiOperation(value = "公开的接口，查询项目已经使用的nexus的总容量(仓库为空返回-1)")
    @Permission(permissionPublic = true)
    @GetMapping("/project/capacity")
    public ResponseEntity<Long> queryNexusProjectCapacity(
            @RequestParam(value = "repository_id") Long repositoryId) {
        return Results.success(nexusRepositoryService.queryNexusProjectCapacity(repositoryId));
    }

    @ApiOperation(value = "公开的接口，组织信息")
    @Permission(permissionPublic = true)
    @GetMapping("/external/tenant")
    public ResponseEntity<ExternalTenantVO> queryExternalTenantVO(
            @RequestParam(value = "organization_id") Long organizationId) {
        return Results.success(c7nBaseService.queryTenantByIdWithExternalInfo(organizationId));
    }

    @ApiOperation(value = "公开的接口，持久化jar")
    @Permission(permissionPublic = true)
    @GetMapping("/sync/assets/persistence")
    public ResponseEntity<Void> syncAssetsPersistence(
            @RequestParam(value = "repository_id") Long repositoryId,
            @RequestParam(value = "path") String path) {
        nexusComponentHandService.syncAssetsToDB(repositoryId, path);
        return Results.success();
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
