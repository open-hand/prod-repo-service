package org.hrds.rdupm.nexus.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 制品库_nexus权限表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@RestController("nexusAuthController.v1")
@RequestMapping("/v1/nexus-auths")
public class NexusAuthController extends BaseController {

    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private NexusAuthService nexusAuthService;

    @ApiOperation(value = "项目层-权限列表")
    @Permission(level = ResourceLevel.PROJECT)
    @GetMapping(value = "/{projectId}/list-project")
    public ResponseEntity<Page<NexusAuth>> listByProject(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId,
                                                             @ApiParam(value = "仓库Id", required = true) @RequestParam Long repositoryId,
                                                             @ApiParam("登录名") @RequestParam(required = false) String loginName,
                                                             @ApiParam("用户名") @RequestParam(required = false) String realName,
                                                             @ApiParam("权限角色Code") @RequestParam(required = false) String roleCode,
                                                             @ApiIgnore PageRequest pageRequest) {
        NexusAuth nexusAuth = new NexusAuth();
        nexusAuth.setRepositoryId(repositoryId);
        nexusAuth.setProjectId(projectId);
        nexusAuth.setLoginName(loginName);
        nexusAuth.setRealName(realName);
        nexusAuth.setRoleCode(roleCode);
        Page<NexusAuth> list = nexusAuthService.pageList(pageRequest, nexusAuth);
        return Results.success(list);
    }
    @ApiOperation(value = "项目层--导出权限")
    @Permission(level = ResourceLevel.PROJECT)
    @GetMapping("/{projectId}/export/project")
    public ResponseEntity<Page<NexusAuth>> projectExport(@ApiParam(value = "猪齿鱼项目ID", required = true) @PathVariable Long projectId,
                                                         @ApiParam(value = "仓库Id", required = true) @RequestParam Long repositoryId,
                                                         @ApiParam("登录名") @RequestParam(required = false) String loginName,
                                                         @ApiParam("用户名") @RequestParam(required = false) String realName,
                                                         @ApiParam("权限角色Code") @RequestParam(required = false) String roleCode,
                                                         @ApiIgnore PageRequest pageRequest,
                                                         @ApiParam("导出，输入exportType=DATA即可") ExportParam exportParam,
                                                         HttpServletResponse response) {
        this.setIds(exportParam);
        NexusAuth nexusAuth = new NexusAuth();
        nexusAuth.setRepositoryId(repositoryId);
        nexusAuth.setProjectId(projectId);
        nexusAuth.setLoginName(loginName);
        nexusAuth.setRealName(realName);
        nexusAuth.setRoleCode(roleCode);
        return Results.success(nexusAuthService.export(pageRequest, nexusAuth, exportParam, response));
    }



    @ApiOperation(value = "组织层-权限列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{organizationId}/list-org")
    public ResponseEntity<Page<NexusAuth>> listByOrg(@ApiParam(value = "猪齿鱼组织ID", required = true) @PathVariable Long organizationId,
                                                         @ApiParam("仓库名称") @RequestParam(required = false) String neRepositoryName,
                                                         @ApiParam("登录名") @RequestParam(required = false) String loginName,
                                                         @ApiParam("用户名") @RequestParam(required = false) String realName,
                                                         @ApiParam("权限角色Code") @RequestParam(required = false) String roleCode,
                                                         @ApiIgnore PageRequest pageRequest) {
        NexusAuth nexusAuth = new NexusAuth();
        nexusAuth.setOrganizationId(organizationId);
        nexusAuth.setLoginName(loginName);
        nexusAuth.setRealName(realName);
        nexusAuth.setRoleCode(roleCode);
        nexusAuth.setRoleCode(neRepositoryName);
        Page<NexusAuth> list = nexusAuthService.pageList(pageRequest, nexusAuth);
        return Results.success(list);
    }

    @ApiOperation(value = "组织层--导出权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/export/organization")
    public ResponseEntity<Page<NexusAuth>> orgExport(@ApiParam(value = "猪齿鱼组织ID", required = true) @PathVariable Long organizationId,
                                                     @ApiParam("仓库名称") @RequestParam(required = false) String neRepositoryName,
                                                     @ApiParam("登录名") @RequestParam(required = false) String loginName,
                                                     @ApiParam("用户名") @RequestParam(required = false) String realName,
                                                     @ApiParam("权限角色Code") @RequestParam(required = false) String roleCode,
                                                     @ApiIgnore PageRequest pageRequest,
                                                     @ApiParam("导出，输入exportType=DATA即可") ExportParam exportParam,
                                                     HttpServletResponse response) {
        this.setIds(exportParam);
        NexusAuth nexusAuth = new NexusAuth();
        nexusAuth.setOrganizationId(organizationId);
        nexusAuth.setLoginName(loginName);
        nexusAuth.setRealName(realName);
        nexusAuth.setRoleCode(roleCode);
        nexusAuth.setNeRepositoryName(neRepositoryName);
        return Results.success(nexusAuthService.export(pageRequest, nexusAuth, exportParam, response));
    }

    @ApiOperation(value = "项目层--权限明细")
    @Permission(level = ResourceLevel.PROJECT)
    @GetMapping("/detail/{authId}")
    public ResponseEntity<NexusAuth> detail(@PathVariable Long authId) {
        NexusAuth nexusAuth = nexusAuthRepository.selectByPrimaryKey(authId);
        return Results.success(nexusAuth);
    }

    @ApiOperation(value = "项目层--分配权限, 必输字段 endDate、roleCode 、userId、repositoryId")
    @Permission(level = ResourceLevel.PROJECT)
    @PostMapping("/{projectId}/create")
    public ResponseEntity<List<NexusAuth>> create(@ApiParam("猪齿鱼项目ID") @PathVariable Long projectId,
                                                   @RequestBody List<NexusAuth> nexusAuthList) {
        validObject(nexusAuthList);
        nexusAuthService.create(projectId, nexusAuthList);
        return Results.success(nexusAuthList);
    }

    @ApiOperation(value = "项目层--更新权限")
    @Permission(level = ResourceLevel.PROJECT)
    @PutMapping
    public ResponseEntity<NexusAuth> update(@RequestBody NexusAuth nexusAuth) {
        SecurityTokenHelper.validToken(nexusAuth);
        nexusAuthService.update(nexusAuth);
        return Results.success(nexusAuth);
    }

    @ApiOperation(value = "项目层--删除权限")
    @Permission(level = ResourceLevel.PROJECT)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody NexusAuth nexusAuth) {
        SecurityTokenHelper.validToken(nexusAuth);
        nexusAuthService.delete(nexusAuth);
        return Results.success();
    }


    /***
     * 设置导出全部列
     * @param exportParam
     */
    private void setIds(ExportParam exportParam){
        //无需在前台指定"列ids"
        Set<Long> ids = new HashSet<>(16);
        exportParam.setIds(ids);
        int fieldLength = NexusAuth.class.getDeclaredFields().length;
        for(int i = 1; i <= fieldLength + 1; i++){
            ids.add((long)i);
        }
    }
}
