package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborRepositoryService;
import org.hrds.rdupm.harbor.app.service.HarborRobotService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.harbor.domain.entity.HarborRobot;
import org.hrds.rdupm.harbor.domain.repository.HarborRobotRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
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
 * 制品库-harbor机器人账户表 管理 API
 *
 * @author mofei.li@hand-china.com 2020-05-28 15:29:06
 */
@RestController("harborRobotController.v1")
@RequestMapping("/v1/{organizationId}/harbor-robots")
public class HarborRobotController extends BaseController {

    @Autowired
    private HarborRobotRepository harborRobotRepository;

    @Autowired
    private HarborRobotService harborRobotService;

    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;


}
