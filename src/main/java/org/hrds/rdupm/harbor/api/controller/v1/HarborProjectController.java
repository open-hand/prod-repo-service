package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
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

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;

	@ApiOperation(value = "项目层--创建镜像仓库")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/create/{projectId}")
	public ResponseEntity createSaga(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
								 @ApiParam(value = "镜像仓库Dto") @RequestBody HarborProjectVo harborProjectVo) {
		harborProjectService.createSaga(projectId,harborProjectVo);
		return Results.success();
	}

	@ApiOperation(value = "项目层--查询镜像仓库明细")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/detail/{harborId}")
	public ResponseEntity<HarborProjectVo> detail(@Encrypt @PathVariable(value = "harborId") @ApiParam(value = "镜像仓库ID") Long harborId) {
		return Results.success(harborProjectService.detail(harborId));
	}

	@ApiOperation(value = "项目层--更新镜像仓库配置")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/update/{projectId}")
	public ResponseEntity updateSaga(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
								 @ApiParam(value = "镜像仓库Dto") @RequestBody HarborProjectVo harborProjectVo) {
		harborProjectService.updateSaga(projectId,harborProjectVo);
		return Results.success();
	}

	@ApiOperation(value = "项目层--删除镜像仓库")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping(value = "/delete/{projectId}")
	public ResponseEntity delete(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		harborProjectService.delete(projectId);
		return Results.success();
	}

	@ApiOperation(value = "项目层--镜像仓库列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list-project/{projectId}")
	public ResponseEntity<List<HarborRepository>> listByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		return Results.success(harborProjectService.listByProject(projectId,null));
	}

	@ApiOperation(value = "组织层--镜像仓库列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/list-org/{organizationId}")
	public ResponseEntity<Page<HarborRepository>> listByOrg(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织ID") Long organizationId,
																@ApiParam("镜像仓库编码") @RequestParam(required = false) String code,
																@ApiParam("镜像仓库名称") @RequestParam(required = false) String name,
																@ApiParam("访问级别") @RequestParam(required = false) String publicFlag,
																@ApiIgnore @SortDefault(value = HarborRepository.FIELD_PROJECT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
		HarborRepository harborRepository = new HarborRepository(code,name,publicFlag,organizationId);
		return Results.success(harborProjectService.listByOrg(harborRepository,pageRequest));
	}

	@ApiOperation(value = "组织层--修改访问级别")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/update/publicFlag/{projectId}")
	public ResponseEntity updatePublicFlag(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
																   @ApiParam("访问级别,字符串true或者false") @RequestParam String publicFlag) {
		harborProjectService.updatePublicFlag(projectId,publicFlag);
		return Results.success();
	}

	@ApiOperation(value = "组织层--查询组织下所有镜像仓库列表")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/all/{organizationId}")
	public ResponseEntity<List<HarborRepository>> listAll(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织ID") Long organizationId) {
		return Results.success(harborRepositoryRepository.select(HarborRepository.FIELD_ORGANIZATION_ID,organizationId));
	}

	@ApiOperation(value = "项目层--harbor仓库前做名称校验")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/check/name/{projectId}")
	public ResponseEntity<Boolean> checkName(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
											 @RequestParam(value = "repositoryName") String repositoryName) {
		return Results.success(harborProjectService.checkName(projectId,repositoryName));
	}


}
