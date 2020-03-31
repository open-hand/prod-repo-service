package org.hrds.rdupm.nexus.infra.feign;

import org.hrds.rdupm.nexus.infra.feign.fallback.BaseServiceFeignClientFallBackFactory;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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

}
