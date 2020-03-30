package org.hrds.rdupm.nexus.api.controller.v1;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiParam;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.api.dto.ProdRepositoryDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.model.RepositoryMavenRequest;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;

/**
 * 制品库_nexus仓库信息表 管理 API
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:43:00
 */
@RestController("nexusRepositoryController.v1")
@RequestMapping("/v1/{organizationId}/nexus-repositorys/project/{projectId}")
public class NexusRepositoryController extends BaseController {

    @Autowired
    private NexusRepositoryRepository nexusRepository;
    @Autowired
    private NexusRepositoryService nexusRepositoryService;

    @ApiOperation(value = "maven 制品仓库创建")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/prod")
    public ResponseEntity<ProdRepositoryDTO> createProdRepo(@ApiParam(value = "租户ID", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                            @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                            @RequestBody ProdRepositoryDTO prodRepositoryDTO) {
        validObject(prodRepositoryDTO);
        if (prodRepositoryDTO.getNexusRepoFlag() != null && prodRepositoryDTO.getNexusRepoFlag()) {
            validObject(prodRepositoryDTO.getProdMavenDTO());
        }
        if (!NexusConstants.ProdRepoType.MAVEN.equals(prodRepositoryDTO.getType())) {
            throw new CommonException("制品仓库类型错误");
        }
        return Results.success(nexusRepositoryService.createProdRepo(tenantId, prodRepositoryDTO));
    }

    @ApiOperation(value = "maven仓库创建")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/maven/repo")
    public ResponseEntity<NexusRepositoryCreateDTO> createMavenRepo(@ApiParam(value = "租户ID", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                                    @ApiParam(value = "项目Id", required = true) @PathVariable(name = "projectId") Long projectId,
                                                                    @RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        nexusRepoCreateDTO.validParam();
        return Results.success(nexusRepositoryService.createMavenRepo(tenantId, projectId,nexusRepoCreateDTO));
    }
}
