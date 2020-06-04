package org.hrds.rdupm.nexus.api.controller.v1;

import java.util.Date;

import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.app.service.NexusLogService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.repository.NexusLogRepository;
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
 * 制品库_nexus日志表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@RestController("nexusLogController.v1")
@RequestMapping("/v1/{organizationId}/nexus-logs")
public class NexusLogController extends BaseController {
    @Autowired
    private NexusLogService nexusLogService;

    @Autowired
    private NexusLogRepository nexusLogRepository;

    @ApiOperation(value = "组织层-日志查询接口")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/org-log")
    public ResponseEntity<Page<NexusLog>> listLogByOrg(@ApiParam(value = "猪齿鱼组织ID",required = true) @PathVariable(value = "organizationId") Long organizationId,
                                                       @ApiParam(value = "仓库类型", required = true) @RequestParam String repoType,
                                                       @ApiParam("猪齿鱼项目ID") @RequestParam(required = false) Long projectId,
                                                       @ApiParam("仓库名称") @RequestParam(required = false) String neRepositoryName,
                                                       @ApiParam("操作人用户名") @RequestParam(required = false) String realName,
                                                       @ApiParam("操作类型") @RequestParam(required = false) String operateType,
                                                       @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
                                                       @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
                                                       @ApiIgnore PageRequest pageRequest) {
        return Results.success(nexusLogService.listLog(organizationId, repoType, projectId, neRepositoryName, realName, operateType, startDate, endDate, null, pageRequest));
    }

    @ApiOperation(value = "项目层-日志查询接口")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/org/project")
    public ResponseEntity<Page<NexusLog>> listLogByProject(@ApiParam("猪齿鱼组织ID") @PathVariable(value = "organizationId") Long organizationId,
                                                           @ApiParam(value = "仓库类型", required = true) @RequestParam String repoType,
                                                           @ApiParam(value = "仓库Id", required = true) @RequestParam Long repositoryId,
                                                           @ApiParam("猪齿鱼项目ID") @RequestParam(required = false) Long projectId,
                                                           @ApiParam("仓库名称") @RequestParam(required = false) String neRepositoryName,
                                                           @ApiParam("操作人用户名") @RequestParam(required = false) String realName,
                                                           @ApiParam("操作类型") @RequestParam(required = false) String operateType,
                                                           @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
                                                           @ApiParam("结束日期") @RequestParam(required = false) Date endDate,
                                                           @ApiIgnore PageRequest pageRequest) {
        return Results.success(nexusLogService.listLog(organizationId, repoType, projectId, neRepositoryName, realName, operateType, startDate, endDate, repositoryId, pageRequest));
    }
}
