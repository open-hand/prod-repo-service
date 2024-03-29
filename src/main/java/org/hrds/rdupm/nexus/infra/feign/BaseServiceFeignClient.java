package org.hrds.rdupm.nexus.infra.feign;

import java.util.List;
import java.util.Set;

import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.infra.feign.fallback.BaseServiceFeignClientFallBackFactory;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author weisen.yang@hand-china.com 2020/3/31
 */
@FeignClient(value = "choerodon-iam", path = "/choerodon", fallbackFactory = BaseServiceFeignClientFallBackFactory.class)
public interface BaseServiceFeignClient {

    /**
     *按照项目Id查询项目的不可变信息
     * @param id
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}/immutable")
    ResponseEntity<String> immutableProjectInfoById(@PathVariable(name = "project_id") Long id);

    /***
     * 根据id批量查询用户信息列表
     * @param ids 用户ID数组
     * @param onlyEnabled 是否查询启用用户
     * @return
     */
    @PostMapping(value = "/v1/users/ids")
    List<UserDTO> listUsersByIds(@RequestBody Long[] ids, @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);


    /**
     * 查询快码信息
     *
     * @param code 快码code
     * @return List<LookupVO>
     */
    @GetMapping(value = "/v1/lookups/code/{code}")
    List<LookupVO> queryCodeValueByCode(@PathVariable(name = "code") String code);

    /***
     * 根据id集合查询项目
     * @param ids 项目Id
     * @return List<ProjectVO>
     */
    @PostMapping("/v1/projects/ids")
    List<ProjectVO> queryByIds(@RequestBody Set<Long> ids);

    /***
     * 查询组织下所有项目
     * @param organizationId 组织ID
     * @return List<ProjectVO>
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/projects/all")
    List<ProjectVO> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId);

    /**
     * 判断用户是不是项目所有者
     *
     * @param id
     * @param projectId
     * @return
     */
    @GetMapping(value = "/v1/users/{id}/projects/{project_id}/check_is_owner")
    Boolean checkIsProjectOwner(@PathVariable("id") Long id, @PathVariable("project_id") Long projectId);

    /**
     * 根据登陆名字查询用户
     * @param loginName
     * @return
     */
    @GetMapping(value = "/v1/users")
    UserDTO query(@RequestParam(name = "login_name") String loginName);
}
