package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.app.service.NexusComponentService;
import org.hrds.rdupm.nexus.client.nexus.model.NexusComponentQuery;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponent;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 组织层 制品库_nexus 包信息 管理 API
 * @author weisen.yang@hand-china.com 2020/4/2
 */
@RestController("nexusComponentOrgController.v1")
@RequestMapping("/v1/nexus-components/organizations")
public class NexusComponentOrgController extends BaseController {
	@Autowired
	private NexusComponentService nexusComponentService;

	@ApiOperation(value = "组织层-包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}")
	public ResponseEntity<Page<NexusServerComponentInfo>> listComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			 NexusComponentQuery componentQuery,
																			 @ApiIgnore PageRequest pageRequest) {
		AssertUtils.notNull(componentQuery.getRepositoryId(), "repositoryId is not null");
		componentQuery.setRepoType(NexusConstants.RepoType.MAVEN);
		return Results.success(nexusComponentService.listComponents(organizationId, null, false,componentQuery, pageRequest));
	}

	@ApiOperation(value = "组织层-npm包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/npm")
	public ResponseEntity<Page<NexusServerComponentInfo>> listNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			NexusComponentQuery componentQuery,
																			@ApiIgnore PageRequest pageRequest) {
		AssertUtils.notNull(componentQuery.getRepositoryId(), "repositoryId is not null");
		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponents(organizationId, null, false, componentQuery, pageRequest));
	}

	@ApiOperation(value = "组织层-npm包列表-版本查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/npm/version")
	public ResponseEntity<Page<NexusServerComponent>> listNpmComponentsVersion(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			   NexusComponentQuery componentQuery,
																			   @ApiIgnore PageRequest pageRequest) {
		componentQuery.setRepoType(NexusConstants.RepoType.NPM);
		return Results.success(nexusComponentService.listComponentsVersion(organizationId, null, true, componentQuery, pageRequest));
	}

	@ApiOperation(value = "组织层-配置指引信息，查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/guide")
	public ResponseEntity<NexusComponentGuideDTO> componentGuide(@ApiParam(value = "仓库Id", required = true) @RequestParam(name = "repositoryId" ) Long repositoryId,
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
}
