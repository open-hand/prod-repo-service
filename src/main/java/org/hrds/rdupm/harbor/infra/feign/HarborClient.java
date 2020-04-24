package org.hrds.rdupm.harbor.infra.feign;

import java.util.List;
import java.util.Map;

import org.hrds.rdupm.harbor.infra.dto.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 16:18
 */
public interface HarborClient {
    /* 项目相关接口 */

    @GET("api/projects")
    Call<List<ProjectDetail>> listProject(@Query("name") String name);

    @GET("api/search")
    Call<ProjectRepository> projectRepositorySearch(@Query("q") String q);

    @POST("api/projects")
    Call<Void> insertProject(@Body Project harborProject);

    @POST("api/projects")
    Call<Void> insertProject(@QueryMap Map<String,String> entityName, @Body Project harborProject);

    @PUT("api/projects/{project_id}")
    Call<Void> updateProject(@Path("project_id") Integer projectId, @Body ProjectUpdateDTO projectUpdateDTO);

    @GET("api/projects/{project_id}")
    Call<ProjectDetail> projectDetail(@Path("project_id") Long projectId);

    @DELETE("api/projects/{project_id}")
    Call<Void> deleteProject(@Path("project_id") Long projectId);

    @POST("/projects/{project_id}/robots")
    Call<List<ProjectMember>> createRobots(@Body Project harborProject);

    @GET("api/projects/{project_id}/logs")
    Call<List<Log>> projectLogs(@Path("project_id") Integer projectId, @Query("username") String username, @Query("repository") String repository, @Query("tag") String tag, @Query("operation") String operation, @Query("page") Integer page, @Query("page_size") Integer pageSize);

    /* 用户相关接口 */

    @POST("api/users")
    Call<Void> insertUser(@Body User harborUser);

    @GET("api/users/current")
    Call<User> getCurrentUser();

    @GET("api/users")
    Call<List<User>> listUser(@Query("username") String username,@Query("email") String email);

    @GET("api/users/{user_id}")
    Call<User> getUserById(@Path("user_id") Integer userId);

    @GET("api/users/search")
    Call<List<User>> searchUserByName(@Query("username") String username);

    @PUT("api/users/{user_id}")
    Call<Void> updateUser(@Path("user_id") Integer userId,@Body UserUpdateDTO userUpdateDTO);

    @DELETE("api/users/{user_id}")
    Call<Void> deleteUser(@Path("user_id") Integer userId);

    @PUT("api/users/{user_id}/sysadmin")
    Call<Void> setAdminRole(@Path("user_id") Integer userId,@Body HasAdminRoleDTO hasAdminRoleDTO);

    @PUT("api/users/{user_id}/password")
    Call<Void> setPassword(@Path("user_id") Integer userId,@Body PasswordUpdateDTO passwordUpdateDTO);


    /* 项目成员相关接口 */

    @POST("api/projects/{project_id}/members")
    Call<Void> setProjectMember(@Path("project_id") Integer projectId, @Body Role role);

    @POST("api/projects/{project_id}/members")
    Call<Void> setProjectMember(@Path("project_id") Integer projectId, @Body ProjectMember projectMember);

    @GET("api/projects/{project_id}/members/{mid}")
    Call<ProjectMember> getMemberByMid(@Path("project_id") Integer projectId, @Path("mid") Integer memberId);

    @PUT("api/projects/{project_id}/members/{mid}")
    Call<Void> updateMember(@Path("project_id") Integer projectId, @Path("mid") Integer memberId,@Body ProjectMemberUpdateDTO projectMemberUpdateDTO);

    @DELETE("api/projects/{project_id}/members/{mid}")
    Call<Void> deleteMember(@Path("project_id") Integer projectId, @Path("mid") Integer memberId);

    @DELETE("api/projects/{project_id}/members/{user_id}")
    Call<Void> deleteLowVersionMember(@Path("project_id") Integer projectId, @Path("user_id") Integer userId);

    @GET("api/projects/{project_id}/members")
    Call<List<ProjectMember>> getProjectMembers(@Path("project_id")Integer projectId, @Query("entityname") String entityname);

    @GET("api/projects/{project_id}/members")
    Call<Object> listProjectMember(@Path("project_id") Long projectId);

    /* 日志 */

    @GET("api/logs")
    Call<List<Log>> getLogs(@Query("username") String username, @Query("repository") String repository, @Query("tag") String tag, @Query("operation") String operation, @Query("page") Integer page, @Query("page_size") Integer pageSize);

    /* ------------------------------------------------------- */

    /* Harbor配置属性 */

    @GET("api/configurations")
    Call<Configurations> getConfigurations();

    @PUT("api/configurations")
    Call<Void> setConfigurations(@Body ConfigurationsUpdateDTO configurationsUpdateDTO);


    /* 镜像扫描器 */

    @GET("api/scanners")
    Call<List<Scanner>> listScanners();

    @GET("api/systeminfo")
    Call<SystemInfo> getSystemInfo();


}
