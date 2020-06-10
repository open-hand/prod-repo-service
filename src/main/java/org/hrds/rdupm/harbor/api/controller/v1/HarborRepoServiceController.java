package org.hrds.rdupm.harbor.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 制品库-harbor仓库服务关联表 管理 API
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@RestController("harborRepoServiceController.v1")
@RequestMapping("/v1/{organizationId}/harbor-repo-services")
public class HarborRepoServiceController extends BaseController {

    @Autowired
    private HarborRepoServiceRepository harborRepoServiceRepository;

}
