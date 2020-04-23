package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborGuide;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborGuideService;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
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
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/project/{harborId}")
	public ResponseEntity<PageInfo<HarborImageVo>> getByProject(@PathVariable(value = "harborId") @ApiParam(value = "镜像仓库ID") Long harborId,
																@ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName,
																@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.getByProject(harborId,imageName,pageRequest));
	}

	@ApiOperation(value = "组织层--镜像列表")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@GetMapping(value = "/org/{organizationId}")
	public ResponseEntity<PageInfo<HarborImageVo>> getByOrg(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织ID") Long organizationId,
														@ApiParam(value = "镜像库编码") @RequestParam(required = false) String projectCode,
														@ApiParam(value = "镜像库名称") @RequestParam(required = false) String projectName,
														@ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName,
														@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.getByOrg(organizationId,projectCode,projectName,imageName,pageRequest));
	}

	@ApiOperation(value = "删除镜像")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping(value = "/delete")
	public ResponseEntity delete(@ApiParam(value = "镜像仓库编码") @RequestParam String projectCode,
								 @ApiParam(value = "镜像名称") @RequestParam String imageName) {
		harborImageService.delete(projectCode,imageName);
		return Results.success();
	}

	@ApiOperation(value = "更新镜像描述")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping(value = "/update/description")
	public ResponseEntity updateDesc(@ApiParam(value = "镜像仓库编码") @RequestParam String projectCode,
								 @ApiParam(value = "镜像名称") @RequestParam String imageName,
								 @ApiParam(value = "镜像描述") @RequestParam String description) {
		harborImageService.updateDesc(projectCode,imageName,description);
		return Results.success();
	}
}
