package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import io.choerodon.core.domain.Page;

import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.DevopsServiceFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 文档库-用户表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-02-18 10:35:56
 */
@Service
public class C7nBaseServiceImpl implements C7nBaseService {

    @Resource
    private BaseFeignClient baseFeignClient;
    @Resource
    private DevopsServiceFeignClient devopsServiceFeignClient;

    @Override
    public Map<String, UserDTO> listUsersByLoginNames(Set<String> userNameSet) {
        ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listUsersByLoginNames(userNameSet.toArray(new String[userNameSet.size()]), false);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.emptyMap();
        } else {
            return responseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getLoginName, dto -> dto));
        }
    }

    @Override
    public Map<Long, ProjectDTO> queryProjectByIds(Set<Long> projectIdSet) {
        ResponseEntity<List<ProjectDTO>> projectResponseEntity = baseFeignClient.queryByIds(projectIdSet);
        if (projectResponseEntity == null || CollectionUtils.isEmpty(projectResponseEntity.getBody())) {
            return Collections.emptyMap();
        } else {
            return projectResponseEntity.getBody().stream().collect(Collectors.toMap(ProjectDTO::getId, dto -> dto));
        }
    }

    @Override
    public ProjectDTO queryProjectById(Long projectId) {
        ResponseEntity<ProjectDTO> responseEntity = baseFeignClient.query(projectId);
        if (responseEntity != null) {
            return responseEntity.getBody();
        } else {
            return new ProjectDTO();
        }
    }

    @Override
    public Map<Long, UserDTO> listUsersByIds(Set<Long> userIdSet) {
        ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()]), false);
        if (userDtoResponseEntity == null || CollectionUtils.isEmpty(userDtoResponseEntity.getBody())) {
            return Collections.emptyMap();
        } else {
            return userDtoResponseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getId, dto -> dto));
        }
    }

    @Override
    public Map<Long, UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIds(Long projectId, Set<Long> userIdSet) {
        ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId, userIdSet);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.emptyMap();
        } else {
            return responseEntity.getBody().stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId, dto -> dto));
        }
    }

    @Override
    public UserDTO listUserById(Long userId) {
        Set<Long> userIdSet = new HashSet<>(1);
        userIdSet.add(userId);
        Map<Long, UserDTO> map = this.listUsersByIds(userIdSet);
        return map.get(userId);
    }

    @Override
    public UserDTO queryByLoginName(String loginName) {
        Set<String> userNameSet = new HashSet<>(1);
        userNameSet.add(loginName);
        Map<String, UserDTO> map = this.listUsersByLoginNames(userNameSet);
        return map.get(loginName);
    }

    @Override
    public List<UserDTO> listProjectUsersByIdName(Long projectId, String name) {
        ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listUsersByName(projectId, name);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.emptyList();
        } else {
            return responseEntity.getBody();
        }
    }

    @Override
    public UserDTO getProjectOwnerById(Long projectId) {
        ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listProjectOwnerById(projectId);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return null;
        } else {
            return responseEntity.getBody().stream().findFirst().orElse(null);
        }
    }

    @Override
    public Map<Long, UserDTO> listProjectOwnerById(Long projectId) {
        ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listProjectOwnerById(projectId);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.emptyMap();
        } else {
            return responseEntity.getBody().stream().collect(Collectors.toMap(UserDTO::getId, dto -> dto));
        }
    }

    @Override
    public List<UserDTO> listProjectOwnerUsers(Long projectId) {
        ResponseEntity<List<UserDTO>> responseEntity = baseFeignClient.listProjectOwnerById(projectId);
        if (!CollectionUtils.isEmpty(responseEntity.getBody())) {
            return responseEntity.getBody();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public AppServiceDTO queryAppServiceById(Long projectId, Long appServiceId) {
        Set<Long> ids = new HashSet<>();
        ids.add(appServiceId);
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId, ids, false, true, "");
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return null;
        } else {
            List<AppServiceDTO> list = responseEntity.getBody().getContent();
            if (!CollectionUtils.isEmpty(list)) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public List<ExternalTenantVO> querySaasTenants(List<String> saasLevels) {
        ResponseEntity<List<ExternalTenantVO>> responseEntity = baseFeignClient.querySaasTenants(saasLevels);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.EMPTY_LIST;
        } else {
            List<ExternalTenantVO> externalTenantVOS = responseEntity.getBody();
            return externalTenantVOS;
        }
    }

    @Override
    public List<ProjectDTO> queryProjectByOrgId(Long tenantId) {
        ResponseEntity<List<ProjectDTO>> responseEntity = baseFeignClient.queryProjectByOrgId(tenantId);
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.EMPTY_LIST;
        } else {
            List<ProjectDTO> projectDTOS = responseEntity.getBody();
            return projectDTOS;
        }
    }

    @Override
    public List<ExternalTenantVO> queryRegisterTenant() {
        ResponseEntity<List<ExternalTenantVO>> responseEntity = baseFeignClient.queryRegisterTenant();
        if (responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())) {
            return Collections.EMPTY_LIST;
        } else {
            List<ExternalTenantVO> externalTenantVOS = responseEntity.getBody();
            return externalTenantVOS;
        }
    }

    @Override
    public ExternalTenantVO queryTenantByIdWithExternalInfo(Long organizationId) {
        ResponseEntity<ExternalTenantVO> responseEntity = baseFeignClient.queryTenantByIdWithExternalInfo(organizationId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        } else {
            return responseEntity.getBody();
        }
    }
}

