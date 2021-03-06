package org.hrds.rdupm.harbor.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.harbor.api.vo.HarborImageScanResultVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageScanVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

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
	@Permission(level = ResourceLevel.ORGANIZATION)
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
														@ApiParam(value = "镜像库编码") @RequestParam(required = false) String code,
														@ApiParam(value = "镜像库名称") @RequestParam(required = false) String name,
														@ApiParam(value = "镜像名称") @RequestParam(required = false) String imageName,
														@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.getByOrg(organizationId,code,name,imageName,pageRequest));
	}

	@ApiOperation(value = "项目层/组织层--删除镜像")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping(value = "/delete")
	public ResponseEntity delete(@RequestBody @ApiParam("必输字段{repoName 名称}") HarborImageVo harborImageVo) {
		harborImageService.delete(harborImageVo);
		return Results.success();
	}

	@ApiOperation(value = "项目层--更新镜像描述")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/update/description")
	public ResponseEntity updateDesc(@RequestBody @ApiParam("必输字段{repoName 名称、description镜像描述}") HarborImageVo harborImageVo) {
		harborImageService.updateDesc(harborImageVo);
		return Results.success();
	}

	@ApiOperation(value = "项目层--镜像扫描")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/project/{projectId}/scan-images")
	public ResponseEntity scanImagesProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目Id") Long projectId,
									 @RequestBody List<HarborImageScanVO> imageScanVOList) {
		harborImageService.scanImages(imageScanVOList);
		return Results.success();
	}

	@ApiOperation(value = "组织层--镜像扫描")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/organization/{organizationId}/scan-images")
	public ResponseEntity scanImagesTenant(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织Id") Long organizationId,
										   @RequestBody List<HarborImageScanVO> imageScanVOList) {
		harborImageService.scanImages(imageScanVOList);
		return Results.success();
	}

	@ApiOperation(value = "项目层--镜像扫描结果详情")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/project/{projectId}/scan-images-detail")
	@CustomPageRequest
	public ResponseEntity<Page<HarborImageScanResultVO>> queryImageScanDetailProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目Id") Long projectId,
																					 @RequestBody HarborImageScanVO imageScanVO,
																					 @ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.queryImageScanDetail(imageScanVO, pageRequest));
	}

	@ApiOperation(value = "组织层--镜像扫描结果详情")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/organization/{organizationId}/scan-images-detail")
	@CustomPageRequest
	public ResponseEntity<Page<HarborImageScanResultVO>> queryImageScanDetailTenant(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织Id") Long organizationId,
																					@RequestBody HarborImageScanVO imageScanVO,
																					@ApiIgnore PageRequest pageRequest) {
		return Results.success(harborImageService.queryImageScanDetail(imageScanVO, pageRequest));
	}

	@ApiOperation(value = "组织层--查询单个扫描结果")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/organization/{organizationId}/scan-images-result")
	public ResponseEntity<HarborImageTagVo> queryImageScanResultTenant(@PathVariable(value = "organizationId") @ApiParam(value = "猪齿鱼组织Id") Long organizationId,
																	   @RequestBody HarborImageScanVO imageScanVO) {
		return Results.success(harborImageService.queryImageScanDetail(imageScanVO));
	}

	@ApiOperation(value = "项目层--查询单个扫描结果")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping(value = "/project/{projectId}/scan-images-result")
	public ResponseEntity<HarborImageTagVo> queryImageScanResultProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼组织Id") Long projectId,
																	   @RequestBody HarborImageScanVO imageScanVO) {
		return Results.success(harborImageService.queryImageScanDetail(imageScanVO));
	}

	@ApiOperation(value = "项目层/组织层--判断是否有可用扫描器")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping(value = "/scanner-status")
	public ResponseEntity<Boolean> scannerAvailable(@RequestParam(value = "projectId") @ApiParam(value = "项目Id") Long projectId) {
		return Results.success(harborImageService.scannerAvailable(projectId));
	}


}
