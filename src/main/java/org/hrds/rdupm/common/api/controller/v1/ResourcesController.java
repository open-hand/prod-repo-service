package org.hrds.rdupm.common.api.controller.v1;

import java.util.List;
import org.hrds.rdupm.common.app.service.ResourceService;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.api.vo.ResourceVO;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/11/9
 */
@RestController("resourcesController.v1")
@RequestMapping("/v1/{organizationId}/repo/resources")
public class ResourcesController {


    @Autowired
    private ResourceService resourceService;


    @PostMapping("/by_project_ids")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<ResourceVO>> queryResourceLimitByProjectIds(@RequestBody List<Long> projectIds) {
        return Results.success(resourceService.listResourceByIds(projectIds));
    }
}
