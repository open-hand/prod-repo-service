package org.hrds.rdupm.nexus.infra.feign.fallback;

import feign.hystrix.FallbackFactory;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author weisen.yang@hand-china.com 2020/3/31
 */
@Component
public class BaseServiceFeignClientFallBackFactory implements FallbackFactory<BaseServiceFeignClient>  {
	@Override
	public BaseServiceFeignClient create(Throwable throwable) {
		return new BaseServiceFeignClient() {
			@Override
			public List<UserDTO> listUsersByIds(@RequestBody Long[] ids, @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled) {
				return new ArrayList<>();
			}

			@Override
			public List<LookupVO> queryCodeValueByCode(@PathVariable(name = "code") String code) {
				return new ArrayList<>();
			}

			@Override
			public List<ProjectVO> queryByIds(@RequestBody Set<Long> ids) {
				return new ArrayList<>();
			}

			@Override
			public List<ProjectVO> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId) {
				return new ArrayList<>();
			}

			@Override
			public Boolean checkIsProjectOwner(Long id, Long projectId) {
				return Boolean.FALSE;
			}
		};
	}
}
