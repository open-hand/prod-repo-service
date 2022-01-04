package org.hrds.rdupm.harbor.app.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;

/**
 * 文档库-用户表应用服务
 *
 * @author xiuhong.chen@hand-china.com 2020-02-18 10:35:56
 */
public interface C7nBaseService {

	Map<String, UserDTO> listUsersByLoginNames(Set<String> userNameSet);

	Map<Long, ProjectDTO> queryProjectByIds(Set<Long> projectIdSet);

	ProjectDTO queryProjectById(Long projectId);

	Map<Long, UserDTO> listUsersByIds(Set<Long> userIdSet);

	Map<Long, UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIds(Long projectId, Set<Long> userIdSet);

	UserDTO listUserById(Long userId);

	UserDTO queryByLoginName(String loginName);

	List<UserDTO> listProjectUsersByIdName(Long projectId, String name);

	UserDTO getProjectOwnerById(Long projectId);

	Map<Long, UserDTO> listProjectOwnerById(Long projectId);


	List<UserDTO> listProjectOwnerUsers(Long projectId);

	AppServiceDTO queryAppServiceById(Long projectId, Long appServiceId);

	List<ExternalTenantVO> querySaasTenants(List<String> saasLevels);


	List<ProjectDTO> queryProjectByOrgId(Long tenantId);

	List<ExternalTenantVO> queryRegisterTenant();

	ExternalTenantVO queryTenantByIdWithExternalInfo(Long organizationId);
}
