package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborGuide;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@ApiOperation(value = "项目层-配置指引")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/project/{projectId}")
	public ResponseEntity<HarborGuide> getByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		return Results.success(harborGuideService.getByProject(projectId));
	}
}
