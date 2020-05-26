package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.app.service.NexusComponentService;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusComponentQuery;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerAssetUpload;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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
		componentQuery.setFormat(NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT);
		return Results.success(nexusComponentService.listComponents(organizationId, null, false,componentQuery, pageRequest));
	}

	@ApiOperation(value = "组织层-npm包列表查询")
	@Permission(level = ResourceLevel.ORGANIZATION)
	@GetMapping("/{organizationId}/npm")
	public ResponseEntity<Page<NexusServerComponentInfo>> listNpmComponents(@ApiParam(value = "组织ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
																			NexusComponentQuery componentQuery,
																			@ApiIgnore PageRequest pageRequest) {
		componentQuery.setFormat(NexusApiConstants.NexusRepoFormat.NPM_FORMAT);
		return Results.success(nexusComponentService.listComponents(organizationId, null, false, componentQuery, pageRequest));
	}

}
