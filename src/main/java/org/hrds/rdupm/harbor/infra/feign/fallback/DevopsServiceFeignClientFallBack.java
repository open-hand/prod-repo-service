package org.hrds.rdupm.harbor.infra.feign.fallback;

import java.util.List;
import java.util.Set;

import feign.hystrix.FallbackFactory;
import io.choerodon.core.domain.Page;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.infra.feign.DevopsServiceFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/02 17:26
 */
@Component
public class DevopsServiceFeignClientFallBack implements FallbackFactory<DevopsServiceFeignClient> {


    @Override
    public DevopsServiceFeignClient create(Throwable throwable) {
        throwable.printStackTrace();
        return new DevopsServiceFeignClient() {
            @Override
            public ResponseEntity<Page<AppServiceDTO>> pageByOptions(Long projectId, Boolean doPage, Integer page, Integer size, String params) {
                return null;
            }

            @Override
            public ResponseEntity<List<AppServiceDTO>> listRepositoriesByActive(Long projectId) {
                return null;
            }

            @Override
            public ResponseEntity<Page<AppServiceDTO>> listAppServiceByIds(Set<Long> ids) {
                return null;
            }
        };
    }
}
