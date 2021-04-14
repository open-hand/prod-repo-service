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
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.util.XMLValidator;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
																		 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt Long repositoryId,
																		 @ApiParam(value = "groupId") @RequestParam(name = "group", required = false) String group,
																		 @ApiParam(value = "artifactId") @RequestParam(name = "name", required = false) String name,
																		 @ApiParam(value = "版本") @RequestParam(name = "version", required = false) String version,
																		 @ApiIgnore PageRequest pageRequest) {
		NexusComponentQuery componentQuery = new NexusComponentQuery();
		componentQuery.setRepositoryId(repositoryId);
		componentQuery.setGroup(group);
		componentQuery.setName(name);
		componentQuery.setVersion(version);

		AssertUtils.notNull(componentQuery.getRepositoryId(), "repositoryId is not null");
		componentQuery.setRepoType(NexusConstants.RepoType.MAVEN);
		return Results.success(nexusComponentService.listComponents(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-npm 包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/project/{projectId}/npm")
	public ResponseEntity<Page<NexusServerComponentInfo>> listNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			@ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
																			@ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt Long repositoryId,
																			@ApiParam(value = "groupId") @RequestParam(name = "group", required = false) String group,
																			@ApiParam(value = "artifactId") @RequestParam(name = "name", required = false) String name,
																			@ApiParam(value = "版本") @RequestParam(name = "version", required = false) String version,
																			@ApiIgnore PageRequest pageRequest) {
		NexusComponentQuery componentQuery = new NexusComponentQuery();
		componentQuery.setRepositoryId(repositoryId);
		componentQuery.setGroup(group);
		componentQuery.setName(name);
		componentQuery.setVersion(version);

		AssertUtils.notNull(componentQuery.getRepositoryId(), "repositoryId is not null");
		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponents(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-npm包列表-版本查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/project/{projectId}/npm/version")
	public ResponseEntity<Page<NexusServerComponent>> listNpmComponentsVersion(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			   @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
																			   @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId") @Encrypt Long repositoryId,
																			   @ApiParam(value = "仓库名称", required = true) @RequestParam(name = "repositoryName") String repositoryName,
																			   @ApiParam(value = "groupId") @RequestParam(name = "group", required = false) String group,
																			   @ApiParam(value = "artifactId") @RequestParam(name = "name", required = false) String name,
																			   @ApiParam(value = "版本") @RequestParam(name = "version", required = false) String version,
																			   @ApiIgnore PageRequest pageRequest) {
		NexusComponentQuery componentQuery = new NexusComponentQuery();
		componentQuery.setRepositoryId(repositoryId);
		componentQuery.setGroup(group);
		componentQuery.setName(name);
		componentQuery.setVersion(version);
		componentQuery.setRepositoryName(repositoryName);

		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponentsVersion(organizationId, projectId, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "项目层-maven 包删除")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping("/{organizationId}/project/{projectId}")
	public ResponseEntity<?> deleteComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
											  @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
											  @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) @Encrypt Long repositoryId,
											  @RequestBody List<String> componentIds) {
		nexusComponentService.deleteComponents(organizationId, projectId, repositoryId, componentIds);
		return Results.success();
	}

	@ApiOperation(value = "项目层-npm 包删除")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@DeleteMapping("/{organizationId}/project/{projectId}/npm")
	public ResponseEntity<?> deleteNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
												 @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
												 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) @Encrypt Long repositoryId,
												 @RequestBody List<String> componentIds) {
		nexusComponentService.deleteComponents(organizationId, projectId, repositoryId, componentIds);
		return Results.success();
	}


	@ApiOperation(value = "项目层-包上传")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@PostMapping("/{organizationId}/project/{projectId}/upload")
	public ResponseEntity<?> componentsUpload(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
											  @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
											  @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) @Encrypt Long repositoryId,
											  @ApiParam(value = "仓库名称") @RequestParam(name = "repositoryName", required = false) String repositoryName,
											  @ApiParam(value = "groupId") @RequestParam(name = "groupId", required = false) String groupId,
											  @ApiParam(value = "artifactId") @RequestParam(name = "artifactId", required = false) String artifactId,
											  @ApiParam(value = "版本") @RequestParam(name = "version", required = false) String version,
//											  @ApiParam(value = "jar文件") @RequestParam(name = "assetJar", required = false) MultipartFile assetJar,
											  @ApiParam(value = "filePath") @RequestParam(name = "filePath", required = false) String filePath,
											  @ApiParam(value = "pom文件") @RequestParam(name = "assetPom", required = false) MultipartFile assetPom) {
		NexusServerComponentUpload componentUpload = new NexusServerComponentUpload();
		componentUpload.setRepositoryName(repositoryName);
		componentUpload.setRepositoryId(repositoryId);
		componentUpload.setGroupId(groupId);
		componentUpload.setArtifactId(artifactId);
		componentUpload.setVersion(version);

		// validObject(componentUpload);
//		if (assetJar == null && assetPom == null) {
//			throw new CommonException(NexusMessageConstants.NEXUS_SELECT_FILE);
//		}
//		this.validateFileType(assetJar, NexusServerAssetUpload.JAR);
		this.validateFileType(assetPom, NexusServerAssetUpload.XML);
		if (assetPom != null) {
			XMLValidator.validXMLDefault(assetPom);
		}
		nexusComponentService.componentsUpload(organizationId, projectId, componentUpload, filePath, assetPom);
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
												 @ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) @Encrypt Long repositoryId,
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
	public ResponseEntity<NexusComponentGuideDTO> componentGuide(@ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) @Encrypt Long repositoryId,
																 @ApiParam(value = "仓库名称") @RequestParam(name = "repository", required = false) String repository,
																 @ApiParam(value = "groupId") @RequestParam(name = "group", required = false) String group,
																 @ApiParam(value = "artifactId") @RequestParam(name = "name", required = false) String name,
																 @ApiParam(value = "版本") @RequestParam(name = "version", required = false) String version) {
		NexusServerComponentInfo componentInfo = new NexusServerComponentInfo();
		componentInfo.setRepositoryId(repositoryId);
		componentInfo.setRepository(repository);
		componentInfo.setGroup(group);
		componentInfo.setName(name);
		componentInfo.setVersion(version);
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
