package org.hrds.rdupm.harbor.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.config.SwaggerTags;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

	@ApiOperation(value = "saga测试-创建镜像仓库")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@PostMapping(value = "/create-saga/{projectId}")
	public ResponseEntity createSaga(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
								 @ApiParam(value = "镜像仓库Dto") @RequestBody HarborProjectVo harborProjectVo) {
		harborProjectService.createSaga(projectId,harborProjectVo);
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

	@ApiOperation(value = "删除镜像仓库")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@DeleteMapping(value = "/delete/{projectId}")
	public ResponseEntity delete(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		harborProjectService.delete(projectId);
		return Results.success();
	}

	@ApiOperation(value = "项目层-镜像仓库列表")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/list-project/{projectId}")
	public ResponseEntity<PageInfo<HarborRepository>> listByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
																	@ApiIgnore @SortDefault(value = HarborRepository.FIELD_PROJECT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		return Results.success(harborProjectService.listByProject(projectId,pageRequest));
	}

	@ApiOperation(value = "组织层-镜像仓库列表")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@GetMapping(value = "/list-org/{organizationId}")
	public ResponseEntity<PageInfo<HarborRepository>> listByOrg(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织ID") Long organizationId,
																@ApiIgnore @SortDefault(value = HarborRepository.FIELD_PROJECT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		return Results.success(harborProjectService.listByOrg(organizationId,pageRequest));
	}
}