package org.hrds.rdupm.init.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.init.service.HarborInitService;
import org.springframework.beans.factory.annotation.Autowired;
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

	@ApiOperation(value = "默认仓库初始化")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionPublic = true)
	@GetMapping(value = "/default-repo")
	public void defaultRepoInit() {
		harborInitService.defaultRepoInit();
	}

	@ApiOperation(value = "自定义仓库初始化")
	@Permission(level = ResourceLevel.ORGANIZATION,permissionPublic = true)
	@GetMapping(value = "/custom-repo")
	public void customRepoInit() {
		harborInitService.customRepoInit();
	}

}
