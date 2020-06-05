package org.hrds.rdupm.harbor.infra.feign.fallback;

import java.util.List;
import java.util.Set;

import feign.hystrix.FallbackFactory;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
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
public class DevopsServiceFeignClientFallBack implements DevopsServiceFeignClient {


    @Override
    public ResponseEntity<Page<AppServiceDTO>> pageByOptions(Long projectId, Boolean doPage, Integer page, Integer size, String params) {
        throw new CommonException("error.feign.appService.page");
    }

    @Override
    public ResponseEntity<List<AppServiceDTO>> listRepositoriesByActive(Long projectId) {
        throw new CommonException("error.feign.appService.list.active");
    }

    @Override
    public ResponseEntity<Page<AppServiceDTO>> listAppServiceByIds(Long projectId, Set<Long> ids, Boolean doPage, Boolean withVersion, String params) {
        throw new CommonException("error.feign.appService.list.ids");
    }
}
