package org.hrds.rdupm.harbor.api.controller;

import java.util.List;

import io.choerodon.core.annotation.Permission;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.app.service.TestAppService;
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
public class HarborTestController {

    @Autowired
    TestAppService testAppService;

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
        return Results.success(testAppService.checkHarbor(url, userName, password, project, email));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "项目信息列表查询")
    @GetMapping(value = "/project_list")
    public ResponseEntity<List<ProjectDetail>> listProject(
            @ApiParam(value = "harborProject")
            @RequestParam(required = false) String name) {
        return Results.success(testAppService.listProject(name));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "项目镜像信息查询")
    @GetMapping(value = "/search_project_repository")
    public ResponseEntity<ProjectRepository> searchProjectRepository(
            @ApiParam(value = "harborProject")
            @RequestParam(required = false) String q) {
        return Results.success(testAppService.searchProjectRepository(q));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建新项目")
    @PostMapping(value = "/project_new")
    public void createProject(
            @ApiParam(value = "harborProject")
            @RequestBody Project project) {
        testAppService.createProject(project);
        //return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id查询项目详情")
    @GetMapping(value = "/project_detail/{project_id}")
    public ResponseEntity<ProjectDetail> projectDetailById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "project_id") Long projectId) {
        return  Results.success(testAppService.projectDetailById(projectId));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id删除项目")
    @DeleteMapping(value = "/delete_project/{project_id}")
    public ResponseEntity<?> deleteProjectById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "project_id") Long projectId) {
        testAppService.deleteProject(projectId);
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
        testAppService.updateProject(projectId,projectUpdateDTO);
        return  Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "获取当前用户信息")
    @GetMapping(value = "/users/current_user")
    public ResponseEntity<User> currentUser() {
        return Results.success(testAppService.getCurrentUser());
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据id查找用户信息")
    @GetMapping(value = "/users/{user_id}")
    public ResponseEntity<User> getUserById(
            @ApiParam(value = "projectId")
            @PathVariable(value = "user_id") Integer userId) {
        return Results.success(testAppService.getUserById(userId));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "分页查询用户信息(仅管理员可用)")
    @GetMapping(value = "/users/user_list")
    public ResponseEntity<List<User>> listUser(
            @ApiParam(value = "username")
            @RequestParam(required = false) String username,
            @ApiParam(value = "email")
            @RequestParam(required = false) String email) {
        return Results.success(testAppService.getUserList(username,email));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据用户名查询用户信息")
    @GetMapping(value = "/users/user_list_name")
    public ResponseEntity<List<User>> searchUserByName(
            @ApiParam(value = "username")
            @RequestParam(required = true) String username) {
        return Results.success(testAppService.searchUserByName(username));
    }


    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建新用户")
    @PostMapping(value = "/users/create_user")
    public ResponseEntity<?> createUser(
            @ApiParam(value = "user")
            @RequestBody User user) {
        testAppService.createUser(user);
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
        testAppService.updateUser(userId,userUpdateDTO);
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "删除用户")
    @PutMapping(value = "/users/delete_user/{user_id}")
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "user_id")
            @PathVariable(value = "user_id")Integer userId) {
        testAppService.deleteUser(userId);
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
        testAppService.setAdminRole(userId,hasAdminRoleDTO);
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
        testAppService.setPassword(userId,passwordUpdateDTO);
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
        return Results.success(testAppService.getProjectMembers(projectId,entityname));
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "创建项目成员")
    @PostMapping(value = "/projects/{project_id}/members")
    public ResponseEntity<?> createProjectMembers(
            @ApiParam(value = "project_id")
            @PathVariable(value = "project_id")Integer projectId,
            @ApiParam(value = "project_member")
            @RequestBody ProjectMember projectMember) {
        testAppService.createProjectMembers(projectId,projectMember);
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
        return Results.success(testAppService.getProjectMembersByMid(projectId,mid));
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
        testAppService.updateProjectMember(projectId,mid,projectMemberUpdateDTO);
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
        testAppService.deleteProjectMember(projectId,mid);
        return Results.success();
    }
}
