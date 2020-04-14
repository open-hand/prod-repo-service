package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import org.hrds.rdupm.harbor.infra.dto.*;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 11:37
 */
public interface HarborAppService {

    /**
     * 校验harbor配置信息是否正确
     *
     * @param url      harbor地址
     * @param userName harbor用户名
     * @param password harbor密码
     * @param project  harbor项目
     * @param email    harbor邮箱
     * @return Boolean
     */
    Boolean checkHarbor(String url, String userName, String password, String project, String email);


    /**
     * 列表查询项目信息
     * name为可选参数，为空查询所有项目，不为空查询对应名称的项目信息
     *
     * @param name  harbor项目名称
     * @return Boolean
     */
    List<ProjectDetail> listProject(String name);

    /**
     * 查询项目信息和镜像信息
     * q 为查询参数  项目名/镜像名
     *
     * @param q  harbor项目名称/镜像名称
     * @return Boolean
     */
    ProjectRepository searchProjectRepository(String q);


    /**
     * 创建新项目
     *
     * @param project  创建项目信息
     * @return
     */
    void createProject (Project project);

    /**
     * 查询项目详细信息
     *
     * @param projectId  项目id
     * @return ProjectDetail
     */
    ProjectDetail projectDetailById(Long projectId);

    /**
     * 根据项目id删除项目
     *
     * @param projectId  项目id
     * @return
     */
    void deleteProject(Long projectId);

    /**
     * 根据项目id更新项目
     *
     * @param projectId  项目id
     * @param projectUpdateDTO 项目属性更新参数
     * @return
     */
    void updateProject(Integer projectId, ProjectUpdateDTO projectUpdateDTO);

    /**
     * 查询当前用户的信息
     *
     * @return User
     */
    User getCurrentUser();

    /**
     * 根据id查询用户信息
     *
     * @param userId
     * @return User
     */
    User getUserById(Integer userId);

    /**
     * 列表查询用户信息
     *
     * @param username
     * @param email
     * @return User
     */
    List<User> getUserList(String username,String email);

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return User
     */
    List<User> searchUserByName(String username);

    /**
     * 新建用户
     *
     * @param user
     * @return User
     */
    void createUser(User user);

    /**
     * 更新用户
     *
     * @param userId
     * @param userUpdateDTO
     * @return
     */
    void updateUser(Integer userId,UserUpdateDTO userUpdateDTO);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    void deleteUser(Integer userId);

    /**
     * 将用户设置为管理员
     *
     * @param userId
     * @param hasAdminRoleDTO
     * @return
     */
    void setAdminRole(Integer userId, HasAdminRoleDTO hasAdminRoleDTO);

    /**
     * 修改用户密码
     *
     * @param userId
     * @param passwordUpdateDTO
     * @return
     */
    void setPassword(Integer userId, PasswordUpdateDTO passwordUpdateDTO);

    /**
     * 查询所有项目成员
     *
     * @param projectId
     * @param entityname
     * @return
     */
    List<ProjectMember> getProjectMembers(Integer projectId, String entityname);

    /**
     * 创建项目成员
     *
     * @param projectId
     * @param projectMember
     * @return
     */
    void createProjectMembers(Integer projectId, ProjectMember projectMember);

    /**
     * 查询指定id的项目成员
     *
     * @param projectId
     * @param mid
     * @return
     */
    ProjectMember getProjectMembersByMid(Integer projectId, Integer mid);

    /**
     * 更新项目成员
     *
     * @param projectId
     * @param mid
     * @param projectMemberUpdateDTO
     * @return
     */
    void updateProjectMember(Integer projectId, Integer mid, ProjectMemberUpdateDTO  projectMemberUpdateDTO);

    /**
     * 删除项目成员
     *
     * @param projectId
     * @param mid
     * @return
     */
    void deleteProjectMember(Integer projectId, Integer mid);

    /**
     * 查询仓库日志
     *
     * @param username
     * @param repository
     * @param tag
     * @param operation
     * @param page
     * @param pageSize
     * @return
     */
    List<Log> getLogs(String username, String  repository, String tag, String operation, Integer page, Integer pageSize);

    /**
     * 查询当前项目的仓库日志
     *
     * @param projectId
     * @param username
     * @param repository
     * @param tag
     * @param operation
     * @param page
     * @param pageSize
     * @return
     */
    List<Log> getProjectLogs(Integer projectId, String username, String  repository, String tag, String operation, Integer page, Integer pageSize);

    /**
     * 查询仓库配置属性
     *
     * @return Configurations
     */
    Configurations getConfigurations();

    /**
     * 修改仓库配置属性
     *
     * @return Configurations
     */
    void setConfigurations(ConfigurationsUpdateDTO configurationsUpdateDTO);

    /**
     * 列表查询扫描器
     *
     * @return List<Scanner>
     */
    List<Scanner> listScanners();
}
