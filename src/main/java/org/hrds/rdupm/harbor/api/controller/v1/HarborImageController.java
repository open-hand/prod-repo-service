package org.hrds.rdupm.harbor.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.*;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 镜像controller
 *
 * @author chenxiuhong 2020/04/23 11:40 上午
 */
@RestController("HarborImageController.v1")
@RequestMapping("/v1/harbor-image")
public class HarborImageController {

	@Autowired
	private HarborImageService harborImageService;

	@ApiOperation(value = "项目层--镜像列表")
	@Permission(level = ResourceLevel.PROJECT)
	@GetMapping(value = "/list-project/{projectId}")
	public ResponseEntity<Page<HarborImageVo>> getByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
																@ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName,
																@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.getByProject(projectId,imageName,pageRequest));
	}

	@ApiOperation(value = "组织层--镜像列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list-org/{organizationId}")
	public ResponseEntity<Page<HarborImageVo>> getByOrg(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织ID") Long organizationId,
														@ApiParam(value = "镜像库编码") @RequestParam String code,
														@ApiParam(value = "镜像库名称") @RequestParam(required = false) String name,
														@ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName,
														@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.getByOrg(organizationId,code,name,imageName,pageRequest));
	}

	@ApiOperation(value = "删除镜像,必输字段{repoName 名称}")
	@Permission(level = ResourceLevel.PROJECT)
	@DeleteMapping(value = "/delete")
	public ResponseEntity delete(@RequestBody HarborImageVo harborImageVo) {
		harborImageService.delete(harborImageVo);
		return Results.success();
	}

	@ApiOperation(value = "更新镜像描述,必输字段{repoName 名称、description镜像描述}")
	@Permission(level = ResourceLevel.PROJECT)
	@PostMapping(value = "/update/description")
	public ResponseEntity updateDesc(@RequestBody HarborImageVo harborImageVo) {
		harborImageService.updateDesc(harborImageVo);
		return Results.success();
	}

}
