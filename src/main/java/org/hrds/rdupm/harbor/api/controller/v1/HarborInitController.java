package org.hrds.rdupm.harbor.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborGuideVo;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hrds.rdupm.harbor.app.service.HarborInitService;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 11:40 上午
 */
@RestController("HarborInitController.v1")
@RequestMapping("/v1/harbor-init")
public class HarborInitController {

	@Autowired
	private HarborInitService harborInitService;

	@ApiOperation(value = "初始化")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionPublic = true)
	@GetMapping(value = "/init")
	public void init() {
		harborInitService.init();
	}

}
