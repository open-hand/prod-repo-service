package org.hrds.rdupm.common.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.api.vo.ProductLibraryDTO;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepoDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.operator.HarborClientOperator;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryQueryDTO;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/v1/product-library")
public class ProductLibraryController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductLibraryController.class);


    @Autowired
    private NexusRepositoryService nexusRepositoryService;
    @Autowired
    private HarborProjectService harborProjectService;
    @Autowired
    private HarborCustomRepoService harborCustomRepoService;

    @Autowired
    private HarborClientOperator harborClientOperator;

    @ApiOperation(value = "项目层--制品库列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/list/{projectId}")
    public ResponseEntity<List<ProductLibraryDTO>> listByProject(@PathVariable(value = "projectId") @ApiParam(value = "猪齿鱼项目ID") Long projectId) {

        // harbor
        List<HarborRepository> harborRepositoryList = null;
        try {
            harborRepositoryList = harborProjectService.listByProject(projectId, new HarborRepository());
        } catch (Exception e) {
            LOGGER.error("query harbor error", e);
            harborRepositoryList = new ArrayList<>();
        }

        //harbor-customize
        List<HarborCustomRepoDTO> harborCustomRepoDTOList = null;
        try {
            harborCustomRepoDTOList = harborCustomRepoService.listByProjectId(projectId);
        } catch (Exception e) {
            LOGGER.error("query harbor custom repo error", e);
            harborCustomRepoDTOList = new ArrayList<>();
        }

        // maven
        List<NexusRepositoryDTO> nexusRepositoryDTOList = null;
        try {
            NexusRepositoryQueryDTO query = new NexusRepositoryQueryDTO();
            query.setProjectId(projectId);
            query.setRepoType(NexusConstants.RepoType.MAVEN);
            nexusRepositoryDTOList = nexusRepositoryService.listRepoAll(query);
        } catch (Exception e) {
            LOGGER.error("query maven error", e);
            nexusRepositoryDTOList = new ArrayList<>();
        }

        // NPM
        List<NexusRepositoryDTO> nexusRepositoryNpmDTOList = null;
        try {
            NexusRepositoryQueryDTO query = new NexusRepositoryQueryDTO();
            query.setProjectId(projectId);
            query.setRepoType(NexusConstants.RepoType.NPM);
            nexusRepositoryNpmDTOList = nexusRepositoryService.listRepoAll(query);
        } catch (Exception e) {
            LOGGER.error("query npm error", e);
            nexusRepositoryNpmDTOList = new ArrayList<>();
        }

        // 返回
        List<ProductLibraryDTO> productLibraryDTOList = new ArrayList<>();
        harborRepositoryList.forEach(harborRepository -> {
            productLibraryDTOList.add(new ProductLibraryDTO(harborRepository));
        });
        harborCustomRepoDTOList.forEach(harborCustomRepoDTO -> {
            productLibraryDTOList.add(new ProductLibraryDTO(harborCustomRepoDTO));
        });
        nexusRepositoryDTOList.forEach(nexusRepositoryDTO -> {
            productLibraryDTOList.add(new ProductLibraryDTO(nexusRepositoryDTO, NexusConstants.RepoType.MAVEN));
        });
        nexusRepositoryNpmDTOList.forEach(nexusRepositoryDTO -> {
            productLibraryDTOList.add(new ProductLibraryDTO(nexusRepositoryDTO, NexusConstants.RepoType.NPM));
        });
        return Results.success(productLibraryDTOList);
    }
}
