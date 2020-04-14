package org.hrds.rdupm.harbor.app.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hrds.rdupm.harbor.app.service.HarborAppService;
import org.hrds.rdupm.harbor.infra.config.ConfigurationProperties;
import org.hrds.rdupm.harbor.infra.config.HarborConfigurationProperties;
import org.hrds.rdupm.harbor.infra.dto.*;
import org.hrds.rdupm.harbor.infra.feign.HarborClient;
import org.hrds.rdupm.harbor.infra.handler.RetrofitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 11:38
 */
@Service
@EnableConfigurationProperties(HarborConfigurationProperties.class)
public class HarborAppServiceImpl implements HarborAppService {
    public static final Logger LOGGER = LoggerFactory.getLogger(HarborAppServiceImpl.class);

    private static final String HARBOR = "harbor";

    @Value("${services.harbor.baseUrl:#{null}}")
    private  String harborBaseUrl;

    @Value("${services.harbor.username:#{admin}}")
    private  String harborUserName;

    @Value("${services.harbor.password:#{Harbor12345}}")
    private  String harborPassword;

    @Value("${services.harbor.password:#{true}}")
    private  String insecureSkipTlsVerify;

    @Override
    public Boolean checkHarbor(String url, String userName, String password, String project, String email) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(url);
        configurationProperties.setUsername(userName);
        configurationProperties.setPassword(password);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setProject(project);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<User> getUser = harborClient.getCurrentUser();
        Response<User> userResponse;
        try {
            userResponse = getUser.execute();
            if (userResponse.raw().code() != 200) {
                if (userResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(userResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
        //校验用户的邮箱是否匹配
        if (!email.equals(userResponse.body().getEmail())) {
            throw new CommonException("error.user.email.not.equal");
        }

        //如果传入了project,校验用户是否有project的权限
        if (project != null) {
            Call<List<ProjectDetail>> listProject = harborClient.listProject(project);
            Response<List<ProjectDetail>> projectResponse;
            try {
                projectResponse = listProject.execute();
                if (projectResponse.body() == null) {
                    throw new CommonException("error.harbor.project.permission");
                } else {
                    List<ProjectDetail> projects = (projectResponse.body()).stream().filter(a -> (a.getName().equals(configurationProperties.getProject()))).collect(Collectors.toList());
                    if (projects.isEmpty()) {
                        throw new CommonException("error.harbor.project.permission");
                    }
                }
            } catch (IOException e) {
                throw new CommonException(e);
            }
        }
        return true;
    }

    @Override
    public List<ProjectDetail> listProject(String name) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setProject(name);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<ProjectDetail>> getProjectList = harborClient.listProject(name);
        Response<List<ProjectDetail>> projectListResponse;
        try {
            projectListResponse = getProjectList.execute();
            if (projectListResponse.body() == null) {
                throw new CommonException("error.harbor.project.null");
            } else {
                List<ProjectDetail> projects = projectListResponse.body();
                if (projects.isEmpty()) {
                    throw new CommonException("error.harbor.project.null");
                } else {
                    return projects;
                }
            }
        } catch (IOException e){
            throw new CommonException(e);
        }
    }

    @Override
    public ProjectRepository searchProjectRepository(String q) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setProject(q);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<ProjectRepository> getProjectRepository = harborClient.projectRepositorySearch(q);
        Response<ProjectRepository> projectRepositoryResponse;
        try {
            projectRepositoryResponse = getProjectRepository.execute();
            if (projectRepositoryResponse.body() == null) {
                throw new CommonException("error.harbor.project.null");
            } else {
                ProjectRepository projectRepository = projectRepositoryResponse.body();
                if (Objects.isNull(projectRepository)) {
                    throw new CommonException("error.harbor.project.null");
                } else {
                    return projectRepository;
                }
            }
        } catch (IOException e){
            throw new CommonException(e);
        }
    }

    @Override
    public void createProject(Project project) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setProject(project.getName());
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> createProject = harborClient.insertProject(project);
        Response<Void> createProjectResponse;
        try {
            createProjectResponse = createProject.execute();
            if (createProjectResponse.raw().code() != 200) {
                if (createProjectResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(createProjectResponse.errorBody().string());
                }
            }
        } catch (IOException e){
            throw new CommonException(e);
        }
    }

    @Override
    public ProjectDetail projectDetailById(Long projectId) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<ProjectDetail> projectDetailCall = harborClient.projectDetail(projectId);
        Response<ProjectDetail> projectDetailResponse;
        try {
            projectDetailResponse = projectDetailCall.execute();
            if (projectDetailResponse.body() == null) {
                throw new CommonException("error.harbor.project.permission");
            } else {
                ProjectDetail projectDetail = projectDetailResponse.body();
                if (Objects.isNull(projectDetail)) {
                    throw new CommonException("error.harbor.project.null");
                } else {
                    return projectDetail;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void deleteProject(Long projectId) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        //校验该用户是否有删除项目的权限
        Call<ProjectDetail> projectDetailCall = harborClient.projectDetail(projectId);
        Response<ProjectDetail> projectDetailResponse;
        try {
            projectDetailResponse = projectDetailCall.execute();
            if (projectDetailResponse.body() == null) {
                throw new CommonException("error.harbor.project.permission");
            } else {
                ProjectDetail projectDetail = projectDetailResponse.body();
                if (Objects.isNull(projectDetail)) {
                    throw new CommonException("error.harbor.project.null");
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
        //删除项目
        Call<Void> deleteProjectCall = harborClient.deleteProject(projectId);
        Response<Void> deleteProjectResponse ;
        try {
            deleteProjectResponse = deleteProjectCall.execute();
            if (deleteProjectResponse.raw().code() != 200) {
                if (deleteProjectResponse.raw().code() == 401){
                    throw new CommonException("error.harbor.project.permission");
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void updateProject(Integer projectId, ProjectUpdateDTO projectUpdateDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setProject(projectUpdateDTO.getName());
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> updateProjectCall = harborClient.updateProject(projectId,projectUpdateDTO);
        Response<Void> updateProjectResponse;
        try {
            updateProjectResponse = updateProjectCall.execute();
            if (updateProjectResponse.raw().code() != 200) {
                if (updateProjectResponse.raw().code() == 400){
                    throw new CommonException("error.harbor.project.id.illegal");
                } else if (updateProjectResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (updateProjectResponse.raw().code() == 403) {
                    throw new CommonException("error.harbor.project.permission");
                } else {
                    throw new CommonException(updateProjectResponse.errorBody().string());
                }
            }
        } catch (IOException e){
            throw new CommonException(e);
        }
    }

    @Override
    public User getCurrentUser() {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<User> getUser = harborClient.getCurrentUser();
        Response<User> userResponse;
        try {
            userResponse = getUser.execute();
            if (userResponse.raw().code() != 200) {
                if (userResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(userResponse.errorBody().string());
                }
            } else {
                User user = userResponse.body();
                if(Objects.isNull(user) ){
                    throw new CommonException("error.harbor.user.not.exists");
                } else {
                    return user;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public User getUserById(Integer userId) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<User> getUser = harborClient.getUserById(userId);
        Response<User> userResponse;
        try {
            userResponse = getUser.execute();
            if (userResponse.raw().code() != 200) {
                if (userResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(userResponse.errorBody().string());
                }
            } else {
                User user = userResponse.body();
                if(Objects.isNull(user) ){
                    throw new CommonException("error.harbor.user.not.exists");
                } else {
                    return user;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<User> getUserList(String username,String email) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<User>> getUserList = harborClient.listUser(username,email);
        Response<List<User>> userResponse;
        try {
            userResponse = getUserList.execute();
            if (userResponse.raw().code() != 200) {
                if (userResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(userResponse.errorBody().string());
                }
            } else {
                List<User> userList = userResponse.body();
                if(CollectionUtils.isEmpty(userList)){
                    throw new CommonException("error.harbor.username.illeage");
                } else {
                    return userList;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<User> searchUserByName(String username) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<User>> searchUserCall = harborClient.searchUserByName(username);
        Response<List<User>> userResponse;
        try {
            userResponse = searchUserCall.execute();
            if (userResponse.raw().code() != 200) {
                if (userResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(userResponse.errorBody().string());
                }
            } else {
                List<User> userList = userResponse.body();
                if(CollectionUtils.isEmpty(userList)){
                    throw new CommonException("error.harbor.username.illeage");
                } else {
                    return userList;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void createUser(User user) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> createUserCall = harborClient.insertUser(user);
        Response<Void> createUserResponse;
        try {
            createUserResponse = createUserCall.execute();
            if (createUserResponse.raw().code() != 201) {
                if (createUserResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (createUserResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(createUserResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void updateUser(Integer userId, UserUpdateDTO userUpdateDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> updateUserCall = harborClient.updateUser(userId,userUpdateDTO);
        Response<Void> updateUserResponse;
        try {
            updateUserResponse = updateUserCall.execute();
            if (updateUserResponse.raw().code() != 200) {
                if (updateUserResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.id");
                } else if (updateUserResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (updateUserResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(updateUserResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> deleteUserCall = harborClient.deleteUser(userId);
        Response<Void> deleteUserResponse;
        try {
            deleteUserResponse = deleteUserCall.execute();
            if (deleteUserResponse.raw().code() != 200) {
                if (deleteUserResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.id");
                } else if (deleteUserResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (deleteUserResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(deleteUserResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void setAdminRole(Integer userId, HasAdminRoleDTO hasAdminRoleDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> setAdminCall = harborClient.setAdminRole(userId,hasAdminRoleDTO);
        Response<Void> setAdminResponse;
        try {
            setAdminResponse = setAdminCall.execute();
            if (setAdminResponse.raw().code() != 200) {
                if (setAdminResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.id");
                } else if (setAdminResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (setAdminResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(setAdminResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void setPassword(Integer userId, PasswordUpdateDTO passwordUpdateDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> setPasswordCall = harborClient.setPassword(userId,passwordUpdateDTO);
        Response<Void> setPasswordResponse;
        try {
            setPasswordResponse = setPasswordCall.execute();
            if (setPasswordResponse.raw().code() != 200) {
                if (setPasswordResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.id");
                } else if (setPasswordResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (setPasswordResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(setPasswordResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<ProjectMember> getProjectMembers(Integer projectId, String entityname) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<ProjectMember>> getProjectMembersCall = harborClient.getProjectMembers(projectId,entityname);
        Response<List<ProjectMember>> getProjectMembersResponse;
        try {
            getProjectMembersResponse = getProjectMembersCall.execute();
            if (getProjectMembersResponse.raw().code() != 200) {
                if (getProjectMembersResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(getProjectMembersResponse.errorBody().string());
                }
            } else {
                List<ProjectMember> memberList = getProjectMembersResponse.body();
                if(CollectionUtils.isEmpty(memberList)){
                    throw new CommonException("error.harbor.username.illeage");
                } else {
                    return memberList;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void createProjectMembers(Integer projectId, ProjectMember projectMember) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> createProjectMembersCall = harborClient.setProjectMember(projectId,projectMember);
        Response<Void> createProjectMembersResponse;
        try {
            createProjectMembersResponse = createProjectMembersCall.execute();
            if (createProjectMembersResponse.raw().code() != 201) {
                if (createProjectMembersResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.projectId");
                } else if (createProjectMembersResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (createProjectMembersResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(createProjectMembersResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public ProjectMember getProjectMembersByMid(Integer projectId, Integer mid) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<ProjectMember> getProjectMemberCall = harborClient.getMemberByMid(projectId,mid);
        Response<ProjectMember> getProjectMemberResponse;
        try {
            getProjectMemberResponse = getProjectMemberCall.execute();
            if (getProjectMemberResponse.raw().code() != 200) {
                if (getProjectMemberResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(getProjectMemberResponse.errorBody().string());
                }
            } else {
               ProjectMember member = getProjectMemberResponse.body();
                if(Objects.isNull(member)){
                    throw new CommonException("error.harbor.username.illeage");
                } else {
                    return member;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void updateProjectMember(Integer projectId, Integer mid, ProjectMemberUpdateDTO projectMemberUpdateDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> updateProjectMemberCall = harborClient.updateMember(projectId,mid,projectMemberUpdateDTO);
        Response<Void> updateProjectMemberResponse;
        try {
            updateProjectMemberResponse = updateProjectMemberCall.execute();
            if (updateProjectMemberResponse.raw().code() != 200) {
                if (updateProjectMemberResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.projectId");
                } else if (updateProjectMemberResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (updateProjectMemberResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(updateProjectMemberResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void deleteProjectMember(Integer projectId, Integer mid) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> deleteProjectMemberCall = harborClient.deleteMember(projectId,mid);
        Response<Void> deleteProjectMemberResponse;
        try {
            deleteProjectMemberResponse = deleteProjectMemberCall.execute();
            if (deleteProjectMemberResponse.raw().code() != 200) {
                if (deleteProjectMemberResponse.raw().code() == 400) {
                    throw new CommonException("error.harbor.invalid.projectId");
                } else if (deleteProjectMemberResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (deleteProjectMemberResponse.raw().code() == 403){
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(deleteProjectMemberResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<Log> getLogs(String username, String repository, String tag, String operation, Integer page, Integer pageSize) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<Log>> getLogsCall = harborClient.getLogs(username,repository,tag,operation, page, pageSize);
        Response<List<Log>> getLogsResponse;
        try {
            getLogsResponse = getLogsCall.execute();
            if (getLogsResponse.raw().code() != 200) {
                if (getLogsResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(getLogsResponse.errorBody().string());
                }
            } else {
                List<Log> logs = getLogsResponse.body();
                return logs;
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<Log> getProjectLogs(Integer projectId, String username, String repository, String tag, String operation, Integer page, Integer pageSize) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<Log>> getProjectLogsCall = harborClient.projectLogs(projectId,username,repository,tag,operation,page,pageSize);
        Response<List<Log>> getProjectLogsResponse;
        try {
            getProjectLogsResponse = getProjectLogsCall.execute();
            if (getProjectLogsResponse.raw().code() != 200) {
                if (getProjectLogsResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(getProjectLogsResponse.errorBody().string());
                }
            } else {
                List<Log> logs = getProjectLogsResponse.body();
                return logs;
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public Configurations getConfigurations() {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Configurations> getConfigurationsCall = harborClient.getConfigurations();
        Response<Configurations> getConfigurationsResponse;
        try {
            getConfigurationsResponse = getConfigurationsCall.execute();
            if (getConfigurationsResponse.raw().code() != 200) {
                if (getConfigurationsResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(getConfigurationsResponse.errorBody().string());
                }
            } else {
                Configurations configurations = getConfigurationsResponse.body();
                if(Objects.isNull(configurations)){
                    throw new CommonException("error.harbor.logs.null");
                } else {
                    return configurations;
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }

    }

    @Override
    public void setConfigurations(ConfigurationsUpdateDTO configurationsUpdateDTO) {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<Void> setConfigurationsCall = harborClient.setConfigurations(configurationsUpdateDTO);
        Response<Void> setConfigurationsResponse;
        try {
            setConfigurationsResponse = setConfigurationsCall.execute();
            if (setConfigurationsResponse.raw().code() != 200) {
                if (setConfigurationsResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else if (setConfigurationsResponse.raw().code() == 403) {
                    throw new CommonException("error.harbor.user.permission");
                } else {
                    throw new CommonException(setConfigurationsResponse.errorBody().string());
                }
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<Scanner> listScanners() {
        ConfigurationProperties configurationProperties = new ConfigurationProperties();
        configurationProperties.setBaseUrl(harborBaseUrl);
        configurationProperties.setUsername(harborUserName);
        configurationProperties.setPassword(harborPassword);
        configurationProperties.setInsecureSkipTlsVerify(true);
        configurationProperties.setType(HARBOR);
        Retrofit retrofit = RetrofitHandler.initRetrofit(configurationProperties);
        HarborClient harborClient = retrofit.create(HarborClient.class);
        Call<List<Scanner>> listScannersCall = harborClient.listScanners();
        Response<List<Scanner>> listScannersResponse;
        try {
            listScannersResponse = listScannersCall.execute();
            if (listScannersResponse.raw().code() != 200) {
                if (listScannersResponse.raw().code() == 401) {
                    throw new CommonException("error.harbor.user.password");
                } else {
                    throw new CommonException(listScannersResponse.errorBody().string());
                }
            } else {
                List<Scanner> scannerList = listScannersResponse.body();
                return scannerList;
            }
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }
}
