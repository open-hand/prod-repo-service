package org.hrds.rdupm.harbor.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.config.SwaggerTags;
import org.hrds.rdupm.harbor.api.vo.HarborGuideVo;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 11:40 上午
 */
@RestController("HarborGuideController.v1")
@RequestMapping("/v1/harbor-guide")
public class HarborGuideController {

	@Autowired
	private HarborGuideService harborGuideService;

	@ApiOperation(value = "项目层--配置指引")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/project/{projectId}")
	public ResponseEntity<HarborGuideVo> getProjectGuid(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		return Results.success(harborGuideService.getProjectGuide(projectId));
	}

	@ApiOperation(value = "项目层/组织层--镜像版本拉取指引")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/tag")
	public ResponseEntity<HarborGuideVo> getTagGuide(@ApiParam(value = "仓库名称") @RequestParam String repoName,
													 @ApiParam(value = "版本号") @RequestParam String tagName) {
		return Results.success(harborGuideService.getTagGuide(repoName,tagName));
	}
}
