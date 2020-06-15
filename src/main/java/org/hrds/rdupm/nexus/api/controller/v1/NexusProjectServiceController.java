package org.hrds.rdupm.nexus.api.controller.v1;

import org.hrds.rdupm.nexus.app.service.NexusProjectServiceService;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusProjectServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 制品库-项目与nexus服务 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-06-10 20:33:59
 */
@RestController("nexusProjectServiceController.v1")
@RequestMapping("/v1/{organizationId}/nexus-project-services")
public class NexusProjectServiceController extends BaseController {

    @Autowired
    private NexusProjectServiceRepository nexusProjectServiceRepository;
    @Autowired
    private NexusProjectServiceService nexusProjectServiceService;

}
