package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.app.service.NexusComponentService;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.util.XMLValidator;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 制品库_nexus 包信息 管理 API
 * @author weisen.yang@hand-china.com 2020/4/2
 */
@RestController("NexusComponentController.v1")
@RequestMapping("/v1/nexus-components")
public class NexusComponentController extends BaseController {
	@Autowired
	private NexusComponentService nexusComponentService;

	@ApiOperation(value = "项目层-maven 包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/project/{projectId}")
	public ResponseEntity<Page<NexusServerComponentInfo>> listComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																		 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
																		 NexusComponentQuery componentQuery,
																		 @ApiIgnore PageRequest pageRequest) {
		componentQuery.setRepoType(NexusConstants.RepoType.MAVEN);
		return Results.success(nexusComponentService.listComponents(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-npm 包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/project/{projectId}/npm")
	public ResponseEntity<Page<NexusServerComponentInfo>> listNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																		 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
																		 NexusComponentQuery componentQuery,
																		 @ApiIgnore PageRequest pageRequest) {
		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponents(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-npm包列表-版本查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/project/{projectId}/npm/version")
	public ResponseEntity<Page<NexusServerComponent>> listNpmComponentsVersion(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			   @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
																			   NexusComponentQuery componentQuery,
																			   @ApiIgnore PageRequest pageRequest) {
		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponentsVersion(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-maven 包删除")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping("/{organizationId}/project/{projectId}")
	public ResponseEntity<?> deleteComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
											  @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
											  @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) Long repositoryId,
											  @RequestBody List<String> componentIds) {
		nexusComponentService.deleteComponents(organizationId, projectId, repositoryId, componentIds);
		return Results.success();
	}

	@ApiOperation(value = "项目层-npm 包删除")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping("/{organizationId}/project/{projectId}/npm")
	public ResponseEntity<?> deleteNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
												 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
												 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) Long repositoryId,
												 @RequestBody List<String> componentIds) {
		nexusComponentService.deleteComponents(organizationId, projectId, repositoryId, componentIds);
		return Results.success();
	}


	@ApiOperation(value = "项目层-包上传")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping("/{organizationId}/project/{projectId}/upload")
	public ResponseEntity<?> componentsUpload(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
											  @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
											  NexusServerComponentUpload componentUpload,
											  @ApiParam(value = "jar文件") @RequestParam(name = "assetJar", required = false) MultipartFile assetJar,
											  @ApiParam(value = "pom文件") @RequestParam(name = "assetPom", required = false) MultipartFile assetPom) {
		// validObject(componentUpload);
		if (assetJar == null && assetPom == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SELECT_FILE);
		}
		this.validateFileType(assetJar, NexusServerAssetUpload.JAR);
		this.validateFileType(assetPom, NexusServerAssetUpload.XML);
		nexusComponentService.componentsUpload(organizationId, projectId, componentUpload, assetJar, assetPom);
		return Results.success();
	}

	@ApiOperation(value = "pom文件校验")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping("/{organizationId}/pom-validate")
	public ResponseEntity<?> pomValidate(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
										 @ApiParam(value = "pom文件", required = true) @RequestParam MultipartFile pomXml) {
		XMLValidator.validXMLDefault(pomXml);
		return Results.success();
	}

	@ApiOperation(value = "项目层-npm包上传")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping("/{organizationId}/project/{projectId}/npm/upload")
	public ResponseEntity<?> npmComponentsUpload(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
												 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
												 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) Long repositoryId,
												 @ApiParam(value = "jar文件") @RequestParam(name = "assetTgz", required = true) MultipartFile assetTgz) {
		if (assetTgz == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_SELECT_FILE);
		}
		this.validateFileType(assetTgz, NexusServerAssetUpload.TGZ);
		nexusComponentService.npmComponentsUpload(organizationId, projectId, repositoryId, assetTgz);
		return Results.success();
	}

	@ApiOperation(value = "配置指引信息，查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/guide")
	public ResponseEntity<NexusComponentGuideDTO> componentGuide(NexusServerComponentInfo componentInfo) {
		return Results.success(nexusComponentService.componentGuide(componentInfo));
	}



	private void validateFileType(MultipartFile file, String type){
		if (file != null) {
			String name = file.getOriginalFilename();
			String sourceType = name.substring(name.lastIndexOf(".")+1);
			if (!type.equals(sourceType)) {
				throw new CommonException(NexusMessageConstants.NEXUS_FILE_TYPE_ERROR);
			}
		}
	}

}
