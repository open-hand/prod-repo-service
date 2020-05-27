package org.hrds.rdupm.nexus.api.controller.v1;

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
    private NexusLogRepository nexusLogRepository;

    @ApiOperation(value = "制品库_nexus日志表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<NexusLog>> list(NexusLog nexusLog, @ApiIgnore @SortDefault(value = NexusLog.FIELD_LOG_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<NexusLog> list = nexusLogRepository.pageAndSort(pageRequest, nexusLog);
        return Results.success(list);
    }

    @ApiOperation(value = "制品库_nexus日志表明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{logId}")
    public ResponseEntity<NexusLog> detail(@PathVariable Long logId) {
        NexusLog nexusLog = nexusLogRepository.selectByPrimaryKey(logId);
        return Results.success(nexusLog);
    }

    @ApiOperation(value = "创建制品库_nexus日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<NexusLog> create(@RequestBody NexusLog nexusLog) {
        validObject(nexusLog);
        nexusLogRepository.insertSelective(nexusLog);
        return Results.success(nexusLog);
    }

    @ApiOperation(value = "修改制品库_nexus日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<NexusLog> update(@RequestBody NexusLog nexusLog) {
        SecurityTokenHelper.validToken(nexusLog);
        nexusLogRepository.updateByPrimaryKeySelective(nexusLog);
        return Results.success(nexusLog);
    }

    @ApiOperation(value = "删除制品库_nexus日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody NexusLog nexusLog) {
        SecurityTokenHelper.validToken(nexusLog);
        nexusLogRepository.deleteByPrimaryKey(nexusLog);
        return Results.success();
    }

}
