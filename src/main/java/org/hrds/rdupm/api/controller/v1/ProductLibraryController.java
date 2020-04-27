package org.hrds.rdupm.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.api.vo.ProductLibraryDTO;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryQueryDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weisen.yang@hand-china.com 2020/4/27
 */
@RestController("ProductLibraryController.v1")
@RequestMapping("/v1/product_library")
public class ProductLibraryController extends BaseController {

	@Autowired
	private NexusRepositoryService nexusRepositoryService;
	@Autowired
	private HarborProjectService harborProjectService;

	@ApiOperation(value = "项目层-制品库库列表")
	@Permission(type = ResourceType.PROJECT, permissionPublic = true)
	@GetMapping(value = "/list/{projectId}")
	public ResponseEntity<List<ProductLibraryDTO>> listByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {

		// harbor
		List<HarborRepository> harborRepositoryList = harborProjectService.listByProject(projectId, new HarborRepository());
		// maven
		List<NexusRepositoryDTO> nexusRepositoryDTOList = nexusRepositoryService.listMavenRepoAll(new NexusRepositoryQueryDTO().setProjectId(projectId), NexusConstants.RepoQueryData.REPO_PROJECT);

		// 返回
		List<ProductLibraryDTO> productLibraryDTOList = new ArrayList<>();
		harborRepositoryList.forEach(harborRepository -> {
			productLibraryDTOList.add(new ProductLibraryDTO(harborRepository));
		});
		nexusRepositoryDTOList.forEach(nexusRepositoryDTO -> {
			productLibraryDTOList.add(new ProductLibraryDTO(nexusRepositoryDTO));
		});
		return Results.success(productLibraryDTOList);
	}
}
