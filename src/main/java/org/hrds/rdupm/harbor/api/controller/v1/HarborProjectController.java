package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.Map;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 9:50 上午
 */
@RestController("HarborProjectController.v1")
@RequestMapping("/v1/harbor-project")
public class HarborProjectController extends BaseController {

	@Autowired
	private HarborProjectService harborProjectService;

	@ApiOperation(value = "创建镜像仓库")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping(value = "/create/{projectId}")
	public ResponseEntity create(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
								 @ApiParam(value = "镜像仓库Dto") @RequestBody HarborProjectVo harborProjectVo) {
		harborProjectService.create(projectId,harborProjectVo);
		return Results.success();
	}

	@ApiOperation(value = "查询镜像仓库明细")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/detail/{harborId}")
	public ResponseEntity<HarborProjectVo> detail(@PathVariable(value = "harborId") @ApiParam(value = "镜像仓库ID") Long harborId) {
		return Results.success(harborProjectService.detail(harborId));
	}

	@ApiOperation(value = "更新镜像仓库配置")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping(value = "/update/{projectId}")
	public ResponseEntity update(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
								 @ApiParam(value = "镜像仓库Dto") @RequestBody HarborProjectVo harborProjectVo) {
		harborProjectService.update(projectId,harborProjectVo);
		return Results.success();
	}

}
