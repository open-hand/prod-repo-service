package org.hrds.rdupm.harbor.infra.feign;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.harbor.infra.feign.fallback.BaseFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * FeignDemo
 * @author chenxiuhong
 */
@FeignClient(value = "base-service", fallback = BaseFeignClientFallBack.class)
public interface BaseFeignClient {

	/***
	 * 根据用户名查询用户信息
	 * @param loginName 登录名
	 * @return
	 */
	@GetMapping(value = "/v1/users")
	ResponseEntity<UserDTO> query(@RequestParam(name = "login_name") String loginName);

	/***
	 * 根据id查询用户信息
	 * @param id 用户ID
	 * @return
	 */
	@GetMapping(value = "/v1/users/{id}/info")
	ResponseEntity<UserDTO> queryInfo(@PathVariable Long id) ;

	/***
	 * 根据id批量查询用户信息列表
	 * @param ids 用户ID数组
	 * @param onlyEnabled 是否查询启用用户
	 * @return
	 */
	@PostMapping(value = "/v1/users/ids")
	ResponseEntity<List<UserDTO>> listUsersByIds(@RequestBody Long[] ids, @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);

	/***
	 * 根据loginName批量查询用户信息列表
	 * @param loginNames 登录名数组
	 * @param onlyEnabled 是否查询启用用户
	 * @return
	 */
	@PostMapping(value = "/v1/users/login_names")
	ResponseEntity<List<UserDTO>> listUsersByLoginNames(@RequestBody String[] loginNames, @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);

	/***
	 * 根据项目ID获取团队成员,登录名或者用户名模糊
	 * @param projectId 项目ID
	 * @param param 参数
	 * @return
	 */
	@GetMapping(value = "/v1/projects/{project_id}/users/search_by_name")
	ResponseEntity<List<UserDTO>> listUsersByName(@PathVariable(name = "project_id") Long projectId, @RequestParam(required = false) String param);

	/***
	 * 查询组织下项目（最多20个）
	 * @param organizationId 组织ID
	 * @param name 项目名称，模糊查询
	 * @return
	 */
	@GetMapping("/v1/organizations/{organization_id}/projects/with_limit")
	ResponseEntity<List<ProjectDTO>> listProjectsWithLimit(@PathVariable(name = "organization_id") Long organizationId, @RequestParam(required = false) String name) ;

	/***
	 * 查询组织下所有项目
	 * @param organizationId 组织ID
	 * @return
	 */
	@GetMapping(value = "/v1/organizations/{organization_id}/projects/all")
	ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId);

	/***
	 * 按照项目Id查询项目
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/v1/projects/{project_id}")
	ResponseEntity<ProjectDTO> query(@PathVariable(name = "project_id") Long id);

	/***
	 * 根据id集合查询项目
	 * @param ids
	 * @return
	 */
	@PostMapping("/v1/projects/ids")
	ResponseEntity<List<ProjectDTO>> queryByIds(@RequestBody Set<Long> ids);

	/***
	 * 根据项目id查询项目下的项目所有者
	 * @param projectId
	 * @return
	 */
	@GetMapping("/v1/projects/{project_id}/owner/list")
	ResponseEntity<List<UserDTO>> listProjectOwnerById(@PathVariable(name = "project_id") Long projectId);

	/***
	 * 根据多个id查询用户（包括用户信息以及所分配的项目角色信息以及GitlabUserId）
	 */
	@PostMapping(value = "/v1/projects/{project_id}/users/list_by_ids")
	ResponseEntity<List<UserWithGitlabIdDTO>> listUsersWithRolesAndGitlabUserIdByIds(
			@PathVariable(name = "project_id") Long projectId,
			@ApiParam(value = "多个用户id", required = true) @RequestBody Set<Long> userIds);

}
