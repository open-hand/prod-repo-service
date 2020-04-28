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
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
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
@RestController("HarborQuotaController.v1")
@RequestMapping("/v1/harbor-quota")
public class HarborQuotaController extends BaseController {

	@Autowired
	private HarborQuotaService harborQuotaService;

	@ApiOperation(value = "组织层-项目-修改资源配额")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@PostMapping(value = "/update-project/{projectId}")
	public ResponseEntity updateProjectQuota(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId,
									   @ApiParam("Artifact数量限制") @RequestParam Integer countLimit,
									   @ApiParam("存储容量限制-数值") @RequestParam Integer storageNum,
									   @ApiParam("存储容量限制-单位") @RequestParam String storageUnit) {
		harborQuotaService.updateProjectQuota(projectId,new HarborProjectVo(countLimit,storageNum,storageUnit));
		return Results.success();
	}

	@ApiOperation(value = "组织层-全局-修改资源配额")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@PostMapping(value = "/update-global")
	public ResponseEntity updateGlobalQuota(@ApiParam("Artifact数量限制") @RequestParam Integer countLimit,
											 @ApiParam("存储容量限制-数值") @RequestParam Integer storageNum,
											 @ApiParam("存储容量限制-单位") @RequestParam String storageUnit) {
		harborQuotaService.updateGlobalQuota(new HarborProjectVo(countLimit,storageNum,storageUnit));
		return Results.success();
	}

	@ApiOperation(value = "获取某项目资源配额")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@GetMapping(value = "/project/{projectId}")
	public ResponseEntity<HarborQuotaVo> getProjectQuota(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {
		return Results.success(harborQuotaService.getProjectQuota(projectId));
	}

	@ApiOperation(value = "获取全局资源配额")
	@Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
	@GetMapping(value = "/global")
	public ResponseEntity<HarborQuotaVo> getGlobalQuota() {
		return Results.success(harborQuotaService.getGlobalQuota());
	}
}
