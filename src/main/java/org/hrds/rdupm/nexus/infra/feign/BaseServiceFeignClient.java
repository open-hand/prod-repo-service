package org.hrds.rdupm.nexus.infra.feign;

import org.hrds.rdupm.nexus.infra.feign.fallback.BaseServiceFeignClientFallBackFactory;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

/**
 * @author weisen.yang@hand-china.com 2020/3/31
 */
@FeignClient(value = "base-service", fallbackFactory = BaseServiceFeignClientFallBackFactory.class)
public interface BaseServiceFeignClient {

	/**
	 * 查询快码信息
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

}
