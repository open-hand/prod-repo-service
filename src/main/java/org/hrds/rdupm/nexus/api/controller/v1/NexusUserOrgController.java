package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.app.service.NexusUserService;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 组织层 制品库_nexus仓库默认用户信息表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@RestController("nexusUserOrgController.v1")
@RequestMapping("/v1/nexus-users/organizations")
public class NexusUserOrgController extends BaseController {

    @Autowired
    private NexusUserRepository nexusUserRepository;
    @Autowired
    private NexusUserService nexusUserService;

    @ApiOperation(value = "组织层-发布权限列表列表查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}")
    public ResponseEntity<Page<NexusUser>> listUser(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                        NexusUser nexusUser,
                                                        @ApiIgnore PageRequest pageRequest) {
        nexusUser.setOrganizationId(organizationId);
        return Results.success(nexusUserRepository.listUser(nexusUser, pageRequest));
    }
}
