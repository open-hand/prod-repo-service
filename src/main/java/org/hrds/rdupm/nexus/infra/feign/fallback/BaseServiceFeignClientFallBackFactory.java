package org.hrds.rdupm.nexus.infra.feign.fallback;

import feign.hystrix.FallbackFactory;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/3/31
 */
@Component
public class BaseServiceFeignClientFallBackFactory implements FallbackFactory<BaseServiceFeignClient>  {
	@Override
	public BaseServiceFeignClient create(Throwable throwable) {
		return new BaseServiceFeignClient() {

			@Override
			public List<LookupVO> queryCodeValueByCode(@PathVariable(name = "code") String code) {
				return new ArrayList<>();
			}
		};
	}
}
