package org.hrds.rdupm.nexus.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryCreateDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryQueryDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 平台层 制品库_nexus仓库信息表 管理 API
 *
 * @author like.zhang@hand-china.com
 */
@RestController("nexusRepositorySiteController.v1")
@RequestMapping("/v1/nexus-repositorys/site")
public class NexusRepositorySiteController extends BaseController {

    @Autowired
    private NexusRepositoryService nexusRepositoryService;

    @ApiOperation(value = "平台层-nexus仓库列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/repo")
    public ResponseEntity<Page<NexusRepositoryDTO>> listNexusRepo(@Encrypt NexusRepositoryQueryDTO queryDTO,
                                                                  @ApiIgnore PageRequest pageRequest) {
        return Results.success(nexusRepositoryService.listNexusRepo(pageRequest, queryDTO));
    }

    @ApiOperation(value = "平台层-nexus仓库分配")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/repo-distribute")
    public ResponseEntity<NexusRepositoryCreateDTO> repoDistribute(@RequestBody NexusRepositoryCreateDTO nexusRepoCreateDTO) {
        validObject(nexusRepoCreateDTO);
        return Results.success(nexusRepositoryService.repoDistribute(nexusRepoCreateDTO));
    }

}
