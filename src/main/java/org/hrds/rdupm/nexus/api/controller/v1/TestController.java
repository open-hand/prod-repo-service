package org.hrds.rdupm.nexus.api.controller.v1;

import org.hrds.rdupm.nexus.app.job.NexusCapacityTask;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/10/4
 */
@RestController("testController.v1")
@RequestMapping("/v1/test")
public class TestController {

    @Autowired
    private NexusCapacityTask nexusCapacityTask;

    @GetMapping("/test")
    @Permission(permissionPublic = true)
    public void test() {
        nexusCapacityTask.harborCapacitylimit(null);
    }
}
