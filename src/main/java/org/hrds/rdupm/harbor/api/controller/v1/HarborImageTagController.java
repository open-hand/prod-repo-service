package org.hrds.rdupm.harbor.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.config.SwaggerTags;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborC7nRepoService;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.app.service.HarborImageTagService;
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
@RestController("HarborImageTagController.v1")
@RequestMapping("/v1/harbor-image-tag")
public class HarborImageTagController {

	@Autowired
	private HarborImageTagService harborImageTagService;

	@ApiOperation(value = "项目层/组织层--镜像TAG列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list/{projectId}")
	public ResponseEntity<Page<HarborImageTagVo>> list(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
														   @ApiParam(value = "仓库名称") @RequestParam String repoName,
														   @ApiParam(value = "版本号") @RequestParam(required = false) String tagName,
														   @ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageTagService.list(projectId,repoName,tagName,pageRequest));
	}

	@ApiOperation(value = "项目层/组织层--镜像TAG构建日志")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/build/log")
	public ResponseEntity<String> buildLog(@ApiParam(value = "仓库名称") @RequestParam String repoName,
										   @ApiParam(value = "版本号") @RequestParam String tagName) {
		return Results.success(harborImageTagService.buildLog(repoName,tagName));
	}

	@ApiOperation(value = "项目层/组织层--删除镜像TAG")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping(value = "/delete")
	public ResponseEntity delete(@ApiParam(value = "仓库名称") @RequestParam String repoName,
								 @ApiParam(value = "版本号") @RequestParam String tagName) {
		harborImageTagService.delete(repoName,tagName);
		return Results.success();
	}

	@ApiOperation(value = "组织层--复制镜像TAG")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/copy")
	public ResponseEntity<String> copyTag(@RequestBody HarborImageReTag harborImageReTag){
		harborImageTagService.copyTag(harborImageReTag);
		return Results.success();
	}


}
