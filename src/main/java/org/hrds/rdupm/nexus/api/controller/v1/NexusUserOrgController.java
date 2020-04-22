package org.hrds.rdupm.nexus.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
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
    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @GetMapping("/{organizationId}")
    public ResponseEntity<PageInfo<NexusUser>> listUser(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                        NexusUser nexusUser,
                                                        @ApiIgnore PageRequest pageRequest) {
        nexusUser.setOrganizationId(organizationId);
        return Results.success(nexusUserRepository.listUser(nexusUser, pageRequest));
    }
}
