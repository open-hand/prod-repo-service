//package org.hrds.rdupm.nexus.api.controller.v1;
//
//import com.github.pagehelper.PageInfo;
//import io.choerodon.core.annotation.Permission;
//import io.choerodon.core.enums.ResourceType;
//import io.swagger.annotations.ApiParam;
//import org.hrds.rdupm.nexus.app.service.NexusUserService;
//import org.hrds.rdupm.nexus.domain.entity.NexusUser;
//import org.hzero.core.util.Results;
//import org.hzero.core.base.BaseController;
//import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import io.choerodon.mybatis.pagehelper.domain.PageRequest;
//import io.swagger.annotations.ApiOperation;
//import springfox.documentation.annotations.ApiIgnore;
//
///**
// * 制品库_nexus仓库默认用户信息表 管理 API
// *
// * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
// */
//@RestController("nexusUserController.v1")
//@RequestMapping("/v1/nexus-users")
//public class NexusUserController extends BaseController {
//
//    @Autowired
//    private NexusUserRepository nexusUserRepository;
//    @Autowired
//    private NexusUserService nexusUserService;
//
//    @ApiOperation(value = "项目层-发布权限列表列表查询")
//    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
//    @GetMapping("/{organizationId}/project/{projectId}")
//    public ResponseEntity<PageInfo<NexusUser>> listUser(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
//                                                        @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
//                                                        NexusUser nexusUser,
//                                                        @ApiIgnore PageRequest pageRequest) {
//        nexusUser.setOrganizationId(organizationId);
//        nexusUser.setProjectId(projectId);
//        return Results.success(nexusUserRepository.listUserPro(nexusUser, pageRequest));
//    }
//
//    @ApiOperation(value = "项目层-修改密码")
//    @Permission(type = ResourceType.PROJECT,permissionPublic = true)
//    @PostMapping("/{organizationId}/project/{projectId}/password/update")
//    public ResponseEntity<?> updatePassword(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
//                                            @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
//                                            @RequestBody NexusUser nexusUser) {
//        nexusUser.setOrganizationId(organizationId);
//        nexusUser.setProjectId(projectId);
//        nexusUser.validChangePassword();
//        nexusUserService.updatePassword(nexusUser);
//        return Results.success();
//    }
//
//    @ApiOperation(value = "项目层-修改发布权限")
//    @Permission(type = ResourceType.PROJECT,permissionPublic = true)
//    @PostMapping("/{organizationId}/project/{projectId}/auth/update")
//    public ResponseEntity<?> updatePushAuth(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
//                                            @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
//                                            @RequestBody NexusUser nexusUser) {
//        nexusUser.setOrganizationId(organizationId);
//        nexusUser.setProjectId(projectId);
//        nexusUserService.updatePushAuth(nexusUser);
//        return Results.success();
//    }
//
//}
