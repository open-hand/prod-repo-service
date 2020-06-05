package org.hrds.rdupm.harbor.infra.feign;


import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.infra.feign.fallback.DevopsServiceFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/02 17:25
 */
@FeignClient(value = "devops-service",path = "/choerodon", fallback = DevopsServiceFeignClientFallBack.class)
public interface DevopsServiceFeignClient {
    /***
     * 根据项目ID分页查询应用服务
     *
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service/page_by_options")
    ResponseEntity<Page<AppServiceDTO>> pageByOptions(@PathVariable(value = "project_id") Long projectId,
                                                      @RequestParam(value = "doPage", required = false) Boolean doPage,
                                                      @RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size,
                                                      @RequestBody(required = false) String params);

    /**
     * 获取所有已经启用的服务
     *
     * @param projectId 猪齿鱼项目ID
     * @return
     */
    @GetMapping("/v1/projects/{project_id}/app_service/list_by_active")
    ResponseEntity<List<AppServiceDTO>> listRepositoriesByActive(@PathVariable(value = "project_id") Long projectId);


    /**
     * 批量查询应用服务
     *
     * @param ids 应用服务Ids, 不能为空，也不能为空数组
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_app_service_ids")
    ResponseEntity<Page<AppServiceDTO>> listAppServiceByIds(@PathVariable(value = "project_id") Long projectId,
                                                            @RequestParam Set<Long> ids,
                                                            @RequestParam(value = "doPage", required = false) Boolean doPage,
                                                            @RequestParam(value = "with_version", required = false) Boolean withVersion,
                                                            @RequestBody(required = false) String params);

}
