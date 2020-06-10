package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.feign.DevopsServiceFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.config.HarborCustomConfiguration;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 制品库-harbor自定义镜像仓库表应用服务默认实现
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
@Service
public class HarborCustomRepoServiceImpl implements HarborCustomRepoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HarborCustomRepoServiceImpl.class);

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private HarborHttpClient harborHttpClient;

    @Autowired
    private DevopsServiceFeignClient devopsServiceFeignClient;

    @Autowired
    private HarborCustomRepoRepository harborCustomRepoRepository;

    @Autowired
    private HarborRepoServiceRepository harborRepoServiceRepository;

    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    @Override
    public Boolean checkCustomRepo(HarborCustomRepo harborCustomRepo) {
        HarborCustomConfiguration harborCustomConfiguration = new HarborCustomConfiguration(harborCustomRepo.getRepoUrl(), harborCustomRepo.getLoginName(), harborCustomRepo.getPassword());
        harborHttpClient.setHarborCustomConfiguration(harborCustomConfiguration);
        //校验用户名密码
        ResponseEntity<String> currentUserResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.CURRENT_USER,null,null);
        LOGGER.debug("current user info：{}", currentUserResponse.getBody());
        User currentUser = new Gson().fromJson(currentUserResponse.getBody(), User.class);

        //校验用户邮箱
        if(StringUtils.isNotEmpty(currentUser.getEmail()) && !currentUser.getEmail().equals(harborCustomRepo.getEmail())) {
            throw new CommonException("error.harbor.custom.repo.email.not.equal");
        }

        //校验harbor项目
        if (StringUtils.isNotBlank(harborCustomRepo.getRepoName())) {
            Map<String,Object> paramMap = new HashMap<>(1);
            paramMap.put("name",harborCustomRepo.getRepoName());
            ResponseEntity<String> listProjectResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_PROJECT,paramMap,null);
            if (listProjectResponse.getBody() == null) {
                throw new CommonException("error.harbor.custom.repo.no.match.project", harborCustomRepo.getRepoName());
            }

            List<HarborProjectDTO> harborProjectDTOList = new Gson().fromJson(listProjectResponse.getBody(), new TypeToken<List<HarborProjectDTO>>(){}.getType());
            if (CollectionUtils.isNotEmpty(harborProjectDTOList)) {
                List<HarborProjectDTO> matchProjectDTO = harborProjectDTOList.stream().filter(a->a.getName().equals(harborCustomRepo.getRepoName())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(matchProjectDTO)){
                    throw new CommonException("error.harbor.custom.repo.no.match.project", harborCustomRepo.getRepoName());
                }
                harborCustomRepo.setPublicFlag(matchProjectDTO.get(0).getMetadata().getPublicFlag());
            } else {
                throw new CommonException("error.harbor.custom.repo.no.match.project", harborCustomRepo.getRepoName());
            }
        } else {
            throw new CommonException("error.harbor.custom.repo.repoName.empty");
        }
        return true;
    }

    @Override
    public List<HarborCustomRepoDTO> listByProjectId(Long projectId) {
        List<HarborCustomRepoDTO> harborCustomRepoDTOList = new ArrayList<>();
        List<HarborCustomRepo> harborCustomRepos = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId))
                .build());
        processRepoList(harborCustomRepos);
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        harborCustomRepos.forEach(harborCustomRepo -> {
            harborCustomRepo.setProjectCode(projectDTO.getCode());
            harborCustomRepoDTOList.add(new HarborCustomRepoDTO(harborCustomRepo));
        });
        return harborCustomRepoDTOList;
    }

    @Override
    public Page<HarborCustomRepo> listByOrg(HarborCustomRepo harborCustomRepo, PageRequest pageRequest) {
        Sqls sql = Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_ORGANIZATION_ID,harborCustomRepo.getOrganizationId());
        if (!StringUtils.isEmpty(harborCustomRepo.getPublicFlag())) {
            sql.andEqualTo(HarborCustomRepo.FIELD_PUBLIC_FLAG, harborCustomRepo.getPublicFlag());
        }
        if (harborCustomRepo.getProjectId() != null) {
            sql.andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, harborCustomRepo.getProjectId());
        }
        Condition condition = Condition.builder(HarborCustomRepo.class).where(sql).build();
        Page<HarborCustomRepo> page = PageHelper.doPageAndSort(pageRequest, ()->harborCustomRepoRepository.selectByCondition(condition));
        processRepoList(page.getContent());
        return page;
    }

    @Override
    public List<AppServiceDTO> listAllAppServiceByCreate(Long projectId) {
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(projectId, false, 0, 0, "");
        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<AppServiceDTO> appServiceDTOS = responseEntity.getBody().getContent();
            return appServiceDTOS;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public HarborCustomRepo detailByRepoId(Long repoId) {
        HarborCustomRepo harborCustomRepo = harborCustomRepoRepository.selectByPrimaryKey(repoId);
        if (null == harborCustomRepo) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(harborCustomRepo.getProjectId());
        harborCustomRepo.setProjectCode(projectDTO.getCode());
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, harborCustomRepo.getId()))
                .build());
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            Set<Long> appServicesIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
            harborCustomRepo.setAppServiceIds(appServicesIds);
        }
        List<HarborCustomRepo> harborCustomRepoList = new ArrayList<>();
        harborCustomRepoList.add(harborCustomRepo);
        processRepoList(harborCustomRepoList);
        return harborCustomRepoList.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        checkCustomRepo(harborCustomRepo);
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        if (StringUtils.isBlank(harborCustomRepo.getPublicFlag()) || !StringUtils.equalsAny(harborCustomRepo.getPublicFlag(), HarborConstants.TRUE, HarborConstants.FALSE)) {
            harborCustomRepo.setPublicFlag(HarborConstants.FALSE);
        }
        harborCustomRepo.setProjectId(projectId);
        harborCustomRepo.setOrganizationId(projectDTO.getOrganizationId());
        harborCustomRepoRepository.insertSelective(harborCustomRepo);
        //创建时关联应用服务
        if (CollectionUtils.isNotEmpty(harborCustomRepo.getAppServiceIds())) {
            ResponseEntity<Page<AppServiceDTO>> pageResponseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId, harborCustomRepo.getAppServiceIds(), false, true, null);
            if (pageResponseEntity.getStatusCode().is2xxSuccessful() &&  CollectionUtils.isNotEmpty(Objects.requireNonNull(pageResponseEntity.getBody().getContent()))) {
                List<AppServiceDTO> appServiceDTOS = pageResponseEntity.getBody().getContent();
                if (appServiceDTOS.stream().map(AppServiceDTO::getId).collect(Collectors.toSet()).containsAll(harborCustomRepo.getAppServiceIds()) && appServiceDTOS.size() == harborCustomRepo.getAppServiceIds().size()) {
                    appServiceDTOS.stream().forEach(x->{
                        HarborRepoService harborRepoService = new HarborRepoService();
                        harborRepoService.setCustomRepoId(harborCustomRepo.getId());
                        harborRepoService.setAppServiceId(x.getId());
                        harborRepoService.setProjectId(projectId);
                        harborRepoService.setOrganizationId(projectDTO.getOrganizationId());
                        harborRepoServiceRepository.insertSelective(harborRepoService);
                    });
                } else {
                    throw new CommonException("error.feign.appService.list.not.match");
                }
            } else {
                throw new CommonException("error.feign.appService.list.ids");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        checkCustomRepo(harborCustomRepo);
        harborCustomRepoRepository.updateByPrimaryKeySelective(harborCustomRepo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> customRepoServices = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborCustomRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, harborCustomRepo.getId()))
                .build());
        if (CollectionUtils.isNotEmpty(customRepoServices)) {
            harborRepoServiceRepository.batchDeleteByPrimaryKey(customRepoServices);
        }
        harborCustomRepoRepository.delete(dbRepo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, Set<Long> appServiceIds) {
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        List<AppServiceDTO> unrelatedAppServices = getNoRelatedAppService(harborCustomRepo.getId());
        List<AppServiceDTO> toRelatedAppServices = batchQueryAppServiceByIds(projectId, appServiceIds, false, true, null);
        if (unrelatedAppServices.size() >= toRelatedAppServices.size() && unrelatedAppServices.containsAll(toRelatedAppServices)) {
            for (AppServiceDTO dto : toRelatedAppServices) {
                HarborRepoService repoService = new HarborRepoService();
                repoService.setProjectId(projectId);
                repoService.setAppServiceId(dto.getId());
                repoService.setCustomRepoId(harborCustomRepo.getId());
                repoService.setOrganizationId(projectDTO.getOrganizationId());
                harborRepoServiceRepository.insertSelective(repoService);
            }
        } else {
            throw new CommonException("error.harbor.custom.repo.toRelate.appService.not.select");
        }
    }

    @Override
    public List<AppServiceDTO> getNoRelatedAppService(Long repoId) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(repoId);
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }

        List<HarborRepoService> harborRepoServices = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, repoId))
                .build());
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(dbRepo.getProjectId(), false, 0, 0, "");
        List<AppServiceDTO> allAppServices = new ArrayList<>();
        if (responseEntity.getStatusCode().is2xxSuccessful() && Objects.nonNull(responseEntity.getBody())) {
            allAppServices = responseEntity.getBody().getContent();
        } else {
            throw new CommonException("error.feign.appService.page");
        }
        if (CollectionUtils.isNotEmpty(harborRepoServices)){
            //已经关联应用服务
            Set<Long> ids = harborRepoServices.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
            List<AppServiceDTO> relatedAppServices = batchQueryAppServiceByIds(dbRepo.getProjectId(), ids, false, true, null);
            allAppServices.removeAll(relatedAppServices);
        }
        return allAppServices;
    }

    @Override
    public Page<AppServiceDTO> pageRelatedServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, PageRequest pageRequest) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.getId());
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            Set<Long> appServiceIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
            List<AppServiceDTO> relatedAppServices = batchQueryAppServiceByIds(projectId, appServiceIds, false, true, null);
            Page<AppServiceDTO> page = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), relatedAppServices);
            return page;
        } else {
            return null;
        }
    }

    @Override
    public void deleteRelation(Long appServiceId, HarborCustomRepo harborCustomRepo) {
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, harborCustomRepo.getId()))
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId))
                .build());
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            if (harborRepoServiceList.size() == 1) {
                harborRepoServiceRepository.deleteByPrimaryKey(harborRepoServiceList.get(0));
            } else {
                throw new CommonException("the relation is duplicate");
            }
        } else {
            throw new CommonException("error.harbor.repo.service.relation.not.exist");
        }
    }

    @Override
    public Page<AppServiceDTO> pageRelatedServiceByOrg(Long organizationId, HarborCustomRepo harborCustomRepo, PageRequest pageRequest) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.getId());
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            Set<Long> appServiceIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
            List<AppServiceDTO> relatedAppServices = batchQueryAppServiceByIds(harborCustomRepo.getProjectId(), appServiceIds, false, true, null);
            Page<AppServiceDTO> page = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), relatedAppServices);
            return page;
        } else {
            return null;
        }
    }

    //提供给猪齿鱼接口

    @Override
    public List<HarborCustomRepo> listAllCustomRepoByProject(Long projectId) {
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId))
                .build());
        if (CollectionUtils.isNotEmpty(harborCustomRepoList)) {
            harborCustomRepoList.stream().forEach(x->x.setProjectCode(projectDTO.getCode()));
            processRepoList(harborCustomRepoList);
            return harborCustomRepoList;
        } else {
            return null;
        }
    }

    @Override
    public HarborRepoDTO listRelatedCustomRepoOrDefaultByService(Long projectId, Long appServiceId) {
        Set<Long> ids = new HashSet<>(1);
        ids.add(appServiceId);
        HarborRepoDTO harborRepoDTO = new HarborRepoDTO();
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId);
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            Set<Long> repoIds = harborRepoServiceList.stream().map(HarborRepoService::getCustomRepoId).collect(Collectors.toSet());
            List<HarborCustomRepo> customRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                    .andWhere(Sqls.custom().andIn(HarborCustomRepo.FIELD_ID, repoIds))
                    .build());
            harborRepoDTO.setCustomRepository(customRepoList);
            return harborRepoDTO;
        } else {
            List<HarborRepository> repositoryList = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID, projectId);
            harborRepoDTO.setDefaultRepository(repositoryList.get(0));
            return harborRepoDTO;
        }
    }

    @Override
    public List<HarborCustomRepo> listUnRelatedCustomRepoByService(Long appServiceId) {
        return null;
    }

    private List<AppServiceDTO> batchQueryAppServiceByIds(Long projectId, Set<Long> ids, Boolean doPage, Boolean withVersion, String params){
        ResponseEntity<Page<AppServiceDTO>> pageResponseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId,ids, doPage, withVersion, params);
        if (pageResponseEntity.getStatusCode().is2xxSuccessful() && CollectionUtils.isNotEmpty(Objects.requireNonNull(pageResponseEntity.getBody().getContent()))) {
            List<AppServiceDTO> appServices = pageResponseEntity.getBody().getContent();
            if (appServices.size() == ids.size() && appServices.stream().map(AppServiceDTO::getId).collect(Collectors.toSet()).containsAll(ids)) {
                return appServices;
            } else {
                throw new CommonException("error.feign.appService.list.not.match");
            }
        } else {
            throw new CommonException("error.feign.appService.list.ids");
        }
    }

    /**
     * 处理自定义仓库列表，获取创建人图标/登录名/真实名称
     *
     * @param harborCustomRepoList 自定义仓库列表
     * @return
     */
    private void processRepoList(List<HarborCustomRepo> harborCustomRepoList){
        if (CollectionUtils.isEmpty(harborCustomRepoList)) {
            return;
        }
        Set<Long> userIds = harborCustomRepoList.stream().map(x->x.getCreatedBy()).collect(Collectors.toSet());
        Map<Long, UserDTO> userDTOMap = c7nBaseService.listUsersByIds(userIds);
        harborCustomRepoList.forEach(dto->{
            UserDTO userDTO = userDTOMap.get(dto.getCreatedBy());
            if (null != userDTO) {
                dto.setCreatorImageUrl(userDTO.getImageUrl());
                dto.setCreatorLoginName(userDTO.getLoginName());
                dto.setCreatorRealName(userDTO.getRealName());
            }
        });
    }


}
