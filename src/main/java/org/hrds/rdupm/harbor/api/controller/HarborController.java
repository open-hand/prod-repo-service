package org.hrds.rdupm.harbor.api.controller;

import java.util.List;

import io.choerodon.core.annotation.Permission;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.app.service.HarborAppService;
import org.hrds.rdupm.harbor.infra.dto.*;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 11:30
 */
@RestController
@RequestMapping(value = "/v1/projects/project_config")
public class HarborController {

    @Autowired
    HarborAppService harborAppService;

    /**
     * 校验harbor配置信息是否正确
     *
     * @param url      harbor地址
     * @param userName harbor用户名
     * @param password harbor密码
     * @param project  harbor项目
     * @param email    harbor邮箱
     */
    @Permission(permissionPublic = true)
    @ApiOperation(value = "校验harbor配置信息是否正确")
    @GetMapping(value = "/check_harbor")
    public ResponseEntity<?> checkHarbor(
//            @ApiParam(value = "项目id", required = true)
//            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "harbor地址", required = true)
            @RequestParam String url,
            @ApiParam(value = "harbor用户名", required = true)
            @RequestParam String userName,
            @ApiParam(value = "harbor密码", required = true)
            @RequestParam String password,
            @ApiParam(value = "harborProject")
            @RequestParam(required = false) String project,
            @ApiParam(value = "harbor邮箱", required = true)
            @RequestParam String email) {
        return Results.success(harborAppService.checkHarbor(url, userName, password, project, email));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "项目信息列表查询")
    @GetMapping(value = "/project_list")
    public ResponseEntity<List<ProjectDetail>> listProject(
            @ApiParam(value = "harborProject")
            @RequestParam(required = false) String name) {
        return Results.success(harborAppService.listProject(name));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "项目镜像信息查询")
    @GetMapping(value = "/search_project_repository")
    public ResponseEntity<ProjectRepository> searchProjectRepository(
            @ApiParam(value = "harborProject")
            @RequestParam(required = false) String q) {
        return Results.success(harborAppService.searchProjectRepository(q));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建新项目")
    @PostMapping(value = "/project_new")
    public ResponseEntity<?> createProject(
            @ApiParam(value = "harborProject")
            @RequestBody Project project) {
        harborAppService.createProject(project);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id查询项目详情")
    @GetMapping(value = "/project_detail/{project_id}")
    public ResponseEntity<ProjectDetail> projectDetailById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "project_id") Long projectId) {
        return  Results.success(harborAppService.projectDetailById(projectId));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id删除项目")
    @DeleteMapping(value = "/delete_project/{project_id}")
    public ResponseEntity<?> deleteProjectById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "project_id") Long projectId) {
        harborAppService.deleteProject(projectId);
        return  Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "更新项目属性")
    @DeleteMapping(value = "/update_project/{project_id}")
    public ResponseEntity<?> updateProjectById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "project_id") Integer projectId,
            @ApiParam(value = "project")
            @RequestBody ProjectUpdateDTO projectUpdateDTO) {
        harborAppService.updateProject(projectId,projectUpdateDTO);
        return  Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "获取当前用户信息")
    @GetMapping(value = "/users/current_user")
    public ResponseEntity<User> currentUser() {
        return Results.success(harborAppService.getCurrentUser());
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id查找用户信息")
    @GetMapping(value = "/users/{user_id}")
    public ResponseEntity<User> getUserById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "user_id") Integer userId) {
        return Results.success(harborAppService.getUserById(userId));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "分页查询用户信息(仅管理员可用)")
    @GetMapping(value = "/users/user_list")
    public ResponseEntity<List<User>> listUser(
            @ApiParam(value = "username")
            @RequestParam(required = false) String username,
            @ApiParam(value = "email")
            @RequestParam(required = false) String email) {
        return Results.success(harborAppService.getUserList(username,email));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据用户名查询用户信息")
    @GetMapping(value = "/users/user_list_name")
    public ResponseEntity<List<User>> searchUserByName(
            @ApiParam(value = "username")
            @RequestParam(required = true) String username) {
        return Results.success(harborAppService.searchUserByName(username));
    }


    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建新用户")
    @PostMapping(value = "/users/create_user")
    public ResponseEntity<?> createUser(
            @ApiParam(value = "user")
            @RequestBody User user) {
        harborAppService.createUser(user);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "更新用户信息")
    @PutMapping(value = "/users/update_user/{user_id}")
    public ResponseEntity<?> createUser(
            @ApiParam(value = "user_id")
            @PathVariable(value = "user_id")Integer userId,
            @ApiParam(value = "user")
            @RequestBody UserUpdateDTO userUpdateDTO) {
        harborAppService.updateUser(userId,userUpdateDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "删除用户")
    @PutMapping(value = "/users/delete_user/{user_id}")
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "user_id")
            @PathVariable(value = "user_id")Integer userId) {
        harborAppService.deleteUser(userId);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "设置用户为管理员")
    @PutMapping(value = "/users/set_admin/{user_id}")
    public ResponseEntity<?> setAdminRole(
            @ApiParam(value = "user_id")
            @PathVariable(value = "user_id")Integer userId,
            @ApiParam(value = "has_admin_role")
            @RequestBody HasAdminRoleDTO hasAdminRoleDTO) {
        harborAppService.setAdminRole(userId,hasAdminRoleDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "修改用户密码")
    @PutMapping(value = "/users/set_password/{user_id}")
    public ResponseEntity<?> setPassword(
            @ApiParam(value = "user_id")
            @PathVariable(value = "user_id")Integer userId,
            @ApiParam(value = "password")
            @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        harborAppService.setPassword(userId,passwordUpdateDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "获取所有项目成员")
    @GetMapping(value = "/projects/{project_id}/members")
    public ResponseEntity<List<ProjectMember>> getProjectMembers(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "entityname")
            @RequestParam String entityname) {
        return Results.success(harborAppService.getProjectMembers(projectId,entityname));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建项目成员")
    @PostMapping(value = "/projects/{project_id}/members")
    public ResponseEntity<?> createProjectMembers(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "project_member")
            @RequestBody ProjectMember projectMember) {
        harborAppService.createProjectMembers(projectId,projectMember);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询指定ID的项目成员")
    @GetMapping(value = "/projects/{project_id}/members/{mid}")
    public ResponseEntity<ProjectMember> createProjectMembers(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "member_id")
            @PathVariable(value = "mid")Integer mid) {
        return Results.success(harborAppService.getProjectMembersByMid(projectId,mid));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "更新项目成员")
    @PutMapping(value = "/projects/{project_id}/members/{mid}")
    public ResponseEntity<?> updateProjectMember(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "member_id")
            @PathVariable(value = "mid")Integer mid,
            @ApiParam(value = "role")
            @RequestBody ProjectMemberUpdateDTO projectMemberUpdateDTO) {
        harborAppService.updateProjectMember(projectId,mid,projectMemberUpdateDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "删除项目成员")
    @DeleteMapping(value = "/projects/{project_id}/members/{mid}")
    public ResponseEntity<?> deleteProjectMember(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "member_id")
            @PathVariable(value = "mid")Integer mid) {
        harborAppService.deleteProjectMember(projectId,mid);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询镜像日志")
    @GetMapping(value = "/logs")
    public ResponseEntity<List<Log>> getLogs(
            @ApiParam(value = "username")
            @RequestParam(required = false) String username,
            @ApiParam(value = "repository")
            @RequestParam(required = false) String repository,
            @ApiParam(value = "tag")
            @RequestParam(required = false) String tag,
            @ApiParam(value = "operation")
            @RequestParam(required = false) String operation,
            @ApiParam(value = "begin_timestamp")
            @RequestParam(required = false) String beginTimestamp,
            @ApiParam(value = "end_timestamp")
            @RequestParam(required = false) String endTimestamp,
            @ApiParam(value = "page")
            @RequestParam(required = false) Integer page,
            @ApiParam(value = "page_size")
            @RequestParam(required = false) Integer pageSize) {
        return Results.success(harborAppService.getLogs(username,repository,tag,operation,page,pageSize));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询当前项目的镜像日志")
    @GetMapping(value = "/projects/{project_id}/logs")
    public ResponseEntity<List<Log>> getProjectLogs(
            @ApiParam(value = "username")
            @PathVariable(value = "project_id") Integer projectId,
            @ApiParam(value = "username")
            @RequestParam(required = false) String username,
            @ApiParam(value = "repository")
            @RequestParam(required = false) String repository,
            @ApiParam(value = "tag")
            @RequestParam(required = false) String tag,
            @ApiParam(value = "operation")
            @RequestParam(required = false) String operation,
            @ApiParam(value = "page")
            @RequestParam(required = false) Integer page,
            @ApiParam(value = "page_size")
            @RequestParam(required = false) Integer pageSize) {
        return Results.success(harborAppService.getProjectLogs(projectId,username,repository,tag,operation,page,pageSize));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询仓库配置属性")
    @GetMapping(value = "/configurations")
    public ResponseEntity<Configurations> getConfigurations() {
        return Results.success(harborAppService.getConfigurations());
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "修改仓库配置属性")
    @PutMapping(value = "/configurations")
    public ResponseEntity<?> getConfigurations(
            @ApiParam(value = "operation")
            @RequestBody  ConfigurationsUpdateDTO configurationsUpdateDTO ) {
        harborAppService.setConfigurations(configurationsUpdateDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "列表查询扫描器")
    @GetMapping(value = "/scanners")
    public ResponseEntity<List<Scanner>> listScanners() {
        return Results.success(harborAppService.listScanners());
    }


}
