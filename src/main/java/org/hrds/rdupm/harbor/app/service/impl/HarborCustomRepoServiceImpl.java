package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborRobotService;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
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
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
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

    @Autowired
    private HarborRobotService harborRobotService;

    @Autowired
    private HarborInfoConfiguration harborInfoConfiguration;

    private static Gson gson = new Gson();

    @Override
    public Boolean checkCustomRepo(HarborCustomRepo harborCustomRepo) {
        HarborCustomConfiguration harborCustomConfiguration = new HarborCustomConfiguration(harborCustomRepo.getRepoUrl(), harborCustomRepo.getLoginName(), harborCustomRepo.getPassword(), null);
        harborHttpClient.setHarborCustomConfiguration(harborCustomConfiguration);
        // 查询harbor系统版本
        String systemVersion = harborHttpClient.getSystemInfo(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO, HarborConstants.API_VERSION_1);
        harborCustomConfiguration.setVersion(systemVersion);
        harborCustomRepo.setApiVersion(systemVersion);

        //校验用户名密码
        ResponseEntity<String> currentUserResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.CURRENT_USER, null, null);
        LOGGER.debug("current user info：{}", currentUserResponse.getBody());
        //当返回是错误的响应码非json数据的时候，这里的解析就会出现错误
        User currentUser = new User();
        try {
            currentUser = new Gson().fromJson(currentUserResponse.getBody(), User.class);
        } catch (Exception e) {
            throw new CommonException("error.parse.repo.response", e);
        }
        //校验用户邮箱
        if (StringUtils.isNotEmpty(currentUser.getEmail()) && !currentUser.getEmail().equals(harborCustomRepo.getEmail())) {
            throw new CommonException("error.harbor.custom.repo.email.not.equal");
        }

        //校验harbor项目
        if (StringUtils.isNotBlank(harborCustomRepo.getRepoName())) {
            Map<String, Object> paramMap = new HashMap<>(1);
            paramMap.put("name", harborCustomRepo.getRepoName());
            ResponseEntity<String> listProjectResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_PROJECT, paramMap, null);
            if (listProjectResponse.getBody() == null) {
                throw new CommonException("error.harbor.custom.repo.no.match.project", harborCustomRepo.getRepoName());
            }

            List<HarborProjectDTO> harborProjectDTOList = new Gson().fromJson(listProjectResponse.getBody(), new TypeToken<List<HarborProjectDTO>>() {
            }.getType());
            if (CollectionUtils.isNotEmpty(harborProjectDTOList)) {
                List<HarborProjectDTO> matchProjectDTO = harborProjectDTOList.stream().filter(a -> a.getName().equals(harborCustomRepo.getRepoName())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(matchProjectDTO)) {
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
    public Boolean existProjectShareCustomRepo(Long projectId) {
        List<HarborCustomRepo> shareCustomRepo = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_SHARE, HarborConstants.TRUE)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isNotEmpty(shareCustomRepo)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<HarborCustomRepoDTO> listByProjectId(Long projectId) {
        List<HarborCustomRepoDTO> harborCustomRepoDTOList = new ArrayList<>();
        List<HarborCustomRepo> harborCustomRepos = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
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
        Sqls sql = Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_ORGANIZATION_ID, harborCustomRepo.getOrganizationId());
        if (!StringUtils.isEmpty(harborCustomRepo.getPublicFlag())) {
            sql.andEqualTo(HarborCustomRepo.FIELD_PUBLIC_FLAG, harborCustomRepo.getPublicFlag());
        }
        if (harborCustomRepo.getProjectId() != null) {
            sql.andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, harborCustomRepo.getProjectId());
        }
        Condition condition = Condition.builder(HarborCustomRepo.class).where(sql).build();
        Page<HarborCustomRepo> page = PageHelper.doPageAndSort(pageRequest, () -> harborCustomRepoRepository.selectByCondition(condition));
        processRepoList(page.getContent());
        return page;
    }

    @Override
    public List<AppServiceDTO> listAppServiceByCreate(Long projectId) {
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(projectId, false, 0, 0, "");
        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<AppServiceDTO> appServiceDTOS = responseEntity.getBody().getContent();
            List<HarborRepoService> relatedAppService = harborRepoServiceRepository.select(HarborRepoService.FIELD_PROJECT_ID, projectId);
            if (CollectionUtils.isNotEmpty(relatedAppService)) {
                //去除未启用的自定义仓库的关联关系
                relatedAppService = filterDisableCustomRepo(relatedAppService);
                Set<Long> relatedAppServiceIds = relatedAppService.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
                appServiceDTOS = appServiceDTOS.stream().filter(appServiceDTO -> !relatedAppServiceIds.contains(appServiceDTO.getId())).collect(Collectors.toList());
            }
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
        if (!harborCustomRepo.getEnabledFlag().equals(HarborConstants.Y)) {
            throw new CommonException("error.harbor.custom.repo.not.enabled");
        }
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(harborCustomRepo.getProjectId());
        harborCustomRepo.setProjectCode(projectDTO.getCode());
        List<HarborCustomRepo> harborCustomRepoList = new ArrayList<>();
        if (harborCustomRepo.getProjectShare().equals(HarborConstants.TRUE)) {
            ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(harborCustomRepo.getProjectId(), false, 0, 0, "");
            if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
                List<AppServiceDTO> appServiceDTOS = responseEntity.getBody().getContent();
                Set<Long> appServicesIds = appServiceDTOS.stream().map(AppServiceDTO::getId).collect(Collectors.toSet());
                harborCustomRepo.setAppServiceIds(appServicesIds);
            }
            harborCustomRepoList.add(harborCustomRepo);
        } else {
            List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                    .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, harborCustomRepo.getId()))
                    .build());
            if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
                Set<Long> appServicesIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
                harborCustomRepo.setAppServiceIds(appServicesIds);
            }
            harborCustomRepoList.add(harborCustomRepo);
        }
        processRepoList(harborCustomRepoList);
        return harborCustomRepoList.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        checkCustomRepo(harborCustomRepo);
        if (harborCustomRepo.getProjectShare().equals(HarborConstants.TRUE) && this.existProjectShareCustomRepo(projectId)) {
            throw new CommonException("error.harbor.custom.repo.share.exist");
        }
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        if (StringUtils.isBlank(harborCustomRepo.getPublicFlag()) || !StringUtils.equalsAny(harborCustomRepo.getPublicFlag(), HarborConstants.TRUE, HarborConstants.FALSE)) {
            harborCustomRepo.setPublicFlag(HarborConstants.FALSE);
        }
        harborCustomRepo.setEnabledFlag(HarborConstants.Y);
        harborCustomRepo.setProjectId(projectId);
        harborCustomRepo.setOrganizationId(projectDTO.getOrganizationId());
        harborCustomRepo.setPassword(DESEncryptUtil.encode(harborCustomRepo.getPassword()));
        harborCustomRepoRepository.insertSelective(harborCustomRepo);
        //创建时关联应用服务
        if (CollectionUtils.isNotEmpty(harborCustomRepo.getAppServiceIds())) {
            ResponseEntity<Page<AppServiceDTO>> pageResponseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId, harborCustomRepo.getAppServiceIds(), false, true, "");
            if (pageResponseEntity.getStatusCode().is2xxSuccessful() && CollectionUtils.isNotEmpty(Objects.requireNonNull(pageResponseEntity.getBody().getContent()))) {
                List<AppServiceDTO> appServiceDTOS = pageResponseEntity.getBody().getContent();
                if (appServiceDTOS.stream().map(AppServiceDTO::getId).collect(Collectors.toSet()).containsAll(harborCustomRepo.getAppServiceIds()) && appServiceDTOS.size() == harborCustomRepo.getAppServiceIds().size()) {
                    appServiceDTOS.stream().forEach(x -> {
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
        if (!dbRepo.getProjectShare().equals(HarborConstants.TRUE) && this.existProjectShareCustomRepo(projectId) && harborCustomRepo.getProjectShare().equals(HarborConstants.TRUE)) {
            throw new CommonException("error.harbor.custom.repo.share.exist");
        }
        if (dbRepo.getPassword().equals(harborCustomRepo.getPassword())) {
            harborCustomRepo.setPassword(DESEncryptUtil.decode(harborCustomRepo.getPassword()));
        }
        checkCustomRepo(harborCustomRepo);
        if (harborCustomRepo.getProjectShare().equals(HarborConstants.TRUE) && dbRepo.getProjectShare().equals(HarborConstants.FALSE)) {
            //失效原来的共享自定义仓库
            List<HarborCustomRepo> shareCustomRepos = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                            .andEqualTo(HarborCustomRepo.FIELD_PROJECT_SHARE, HarborConstants.TRUE)
                            .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                    .build());
            if (CollectionUtils.isNotEmpty(shareCustomRepos)) {
                HarborCustomRepo shareCustomRepo = shareCustomRepos.get(0);
                shareCustomRepo.setEnabledFlag(HarborConstants.N);
                harborCustomRepoRepository.updateOptional(shareCustomRepo, HarborCustomRepo.FIELD_ENABLED_FLAG);
            }
        }
        //原来的仓库设置为未启用
        dbRepo.setEnabledFlag(HarborConstants.N);
        harborCustomRepoRepository.updateOptional(dbRepo, HarborCustomRepo.FIELD_ENABLED_FLAG);
        //插入更新的仓库
        HarborUtil.resetDomain(harborCustomRepo);
        harborCustomRepo.setEnabledFlag(HarborConstants.Y);
        harborCustomRepo.setPassword(DESEncryptUtil.encode(harborCustomRepo.getPassword()));
        harborCustomRepoRepository.insertSelective(harborCustomRepo);
        //复制并插入仓库的关联关系
        List<HarborRepoService> existRelation = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.getId())
                        .andEqualTo(HarborRepoService.FIELD_PROJECT_ID, projectId)
                        .andIsNotNull(HarborRepoService.FIELD_APP_SERVICE_ID))
                .build());
        if (CollectionUtils.isNotEmpty(existRelation)) {
            existRelation.stream().forEach(harborRepoService -> {
                harborRepoService.setCustomRepoId(harborCustomRepo.getId());
                HarborUtil.resetDomain(harborRepoService);
                harborRepoServiceRepository.insertSelective(harborRepoService);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> customRepoServices = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
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
    public void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        Set<Long> allAppServiceIds = harborCustomRepo.getAppServiceIds();
        List<HarborCustomRepo> dbRepo = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_ID, harborCustomRepo.getId())
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isEmpty(dbRepo)) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> existRelation = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.get(0).getId())
                        .andEqualTo(HarborRepoService.FIELD_PROJECT_ID, projectId)
                        .andIsNotNull(HarborRepoService.FIELD_APP_SERVICE_ID))
                .build());
        Set<Long> existAppServiceIds = existRelation.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
        Set<Long> toRelateAppServiceIds = allAppServiceIds.stream().filter(x -> !existAppServiceIds.contains(x)).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(toRelateAppServiceIds)) {
            return;
        }
        List<AppServiceDTO> toRelatedAppServices = batchQueryAppServiceByIds(projectId, toRelateAppServiceIds, false, true, "");
        for (AppServiceDTO dto : toRelatedAppServices) {
            HarborRepoService repoService = new HarborRepoService();
            repoService.setProjectId(projectId);
            repoService.setAppServiceId(dto.getId());
            repoService.setCustomRepoId(harborCustomRepo.getId());
            repoService.setOrganizationId(projectDTO.getOrganizationId());
            harborRepoServiceRepository.insertSelective(repoService);
        }
    }

    @Override
    public List<AppServiceDTO> getNoRelatedAppService(Long repoId) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(repoId);
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> harborRepoServices = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_PROJECT_ID, dbRepo.getProjectId())
                        .andIsNotNull(HarborRepoService.FIELD_APP_SERVICE_ID))
                .build());
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(dbRepo.getProjectId(), false, 0, 0, "");
        List<AppServiceDTO> allAppServices = new ArrayList<>();
        if (responseEntity.getStatusCode().is2xxSuccessful() && Objects.nonNull(responseEntity.getBody())) {
            allAppServices = responseEntity.getBody().getContent();
        } else {
            throw new CommonException("error.feign.appService.page");
        }
        if (CollectionUtils.isNotEmpty(harborRepoServices)) {
            //去除已经关联应用服务
            harborRepoServices = filterDisableCustomRepo(harborRepoServices);
            Set<Long> ids = harborRepoServices.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
            allAppServices = allAppServices.stream().filter(appServiceDTO -> !ids.contains(appServiceDTO.getId())).collect(Collectors.toList());
        }
        return allAppServices;
    }

    @Override
    public Page<AppServiceDTO> pageRelatedServiceByProject(Long projectId, Long customRepoId, String appServiceName, String appServiceCode, PageRequest pageRequest) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(customRepoId);
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        String params = buildSearchParam(appServiceName, appServiceCode);
        ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(dbRepo.getProjectId(), false, 0, 0, params);
        if (CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), Collections.emptyList());
        }
        List<AppServiceDTO> appServiceDTOS = responseEntity.getBody().getContent();
        if (dbRepo.getProjectShare().equals(HarborConstants.TRUE)) {
            return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), appServiceDTOS);
        } else {
            List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.getId());
            if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
                Set<Long> appServiceIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
                List<AppServiceDTO> relatedAppServices = appServiceDTOS.stream().filter(appServiceDTO -> appServiceIds.contains(appServiceDTO.getId())).collect(Collectors.toList());
                return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), relatedAppServices);
            } else {
                return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), Collections.emptyList());
            }
        }

    }

    @Override
    public void deleteRelation(Long appServiceId, HarborCustomRepo harborCustomRepo) {
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, harborCustomRepo.getId()))
                .andWhere(Sqls.custom().andEqualTo(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId))
                .build());
        if (CollectionUtils.isEmpty(harborRepoServiceList)) {
            throw new CommonException("error.harbor.repo.service.relation.not.exist");
        }
        if (harborRepoServiceList.size() > 1) {
            throw new CommonException("error.harbor.repo.service.relation.duplicate");
        }
        harborRepoServiceRepository.delete(harborRepoServiceList.get(0));
    }

    @Override
    public Page<AppServiceDTO> pageRelatedServiceByOrg(Long organizationId, Long customRepoId, PageRequest pageRequest) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(customRepoId);
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        if (dbRepo.getProjectShare().equals(HarborConstants.TRUE)) {
            ResponseEntity<Page<AppServiceDTO>> responseEntity = devopsServiceFeignClient.pageByOptions(dbRepo.getProjectId(), false, 0, 0, "");
            if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
                List<AppServiceDTO> appServiceDTOS = responseEntity.getBody().getContent();
                return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), appServiceDTOS);
            } else {
                return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), Collections.emptyList());
            }
        } else {
            List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_CUSTOM_REPO_ID, dbRepo.getId());
            if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
                Set<Long> appServiceIds = harborRepoServiceList.stream().map(HarborRepoService::getAppServiceId).collect(Collectors.toSet());
                List<AppServiceDTO> relatedAppServices = batchQueryAppServiceByIds(dbRepo.getProjectId(), appServiceIds, false, true, "");
                Page<AppServiceDTO> page = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), relatedAppServices);
                return page;
            } else {
                return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), Collections.emptyList());
            }
        }
    }


    @Override
    public List<HarborCustomRepo> listAllCustomRepoByProject(Long projectId) {
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);
        List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_SHARE, HarborConstants.FALSE)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isNotEmpty(harborCustomRepoList)) {
            harborCustomRepoList.stream().forEach(x -> x.setProjectCode(projectDTO.getCode()));
            processRepoList(harborCustomRepoList);
            return harborCustomRepoList;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public HarborCustomRepo listRelatedCustomRepoOrDefaultByService(Long projectId, Long appServiceId) {
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.select(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId);
        if (CollectionUtils.isEmpty(harborRepoServiceList)) {
            return null;
        }
        Set<Long> customRepoIds = harborRepoServiceList.stream().map(HarborRepoService::getCustomRepoId).collect(Collectors.toSet());
        List<HarborCustomRepo> customRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andIn(HarborCustomRepo.FIELD_ID, customRepoIds)
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isNotEmpty(customRepoList)) {
            return customRepoList.get(0);
        } else {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelationByService(Long projectId, Long appServiceId, Long customRepoId) {
        List<HarborCustomRepo> customRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_ID, customRepoId)
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId))
                .build());
        if (CollectionUtils.isEmpty(customRepoList)) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        HarborCustomRepo customRepo = customRepoList.get(0);
        if (!customRepo.getEnabledFlag().equals(HarborConstants.Y)) {
            throw new CommonException("error.harbor.custom.repo.not.enabled");
        }
        List<HarborRepoService> existRelation = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId)
                        .andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, customRepoId))
                .build());
        if (CollectionUtils.isEmpty(existRelation)) {
            HarborRepoService repoService = new HarborRepoService();
            repoService.setProjectId(customRepo.getProjectId());
            repoService.setOrganizationId(customRepo.getOrganizationId());
            repoService.setCustomRepoId(customRepo.getId());
            repoService.setAppServiceId(appServiceId);
            harborRepoServiceRepository.insertSelective(repoService);
        }
        /*else {
            throw new CommonException("error.harbor.repo.service.relation.exist");
        }*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRelationByService(Long projectId, Long appServiceId, Long customRepoId) {
        List<HarborCustomRepo> customRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_ID, customRepoId))
                .build());
        if (CollectionUtils.isEmpty(customRepoList)) {
            throw new CommonException("error.harbor.custom.repo.not.exist");
        }
        List<HarborRepoService> existRelation = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId)
                        .andEqualTo(HarborRepoService.FIELD_CUSTOM_REPO_ID, customRepoId))
                .build());
        if (CollectionUtils.isEmpty(existRelation)) {
            throw new CommonException("error.harbor.repo.service.relation.not.exist");
        }
        harborRepoServiceRepository.batchDeleteByPrimaryKey(existRelation);
    }

    @Override
    public HarborRepoDTO getHarborRepoConfig(Long projectId, Long appServiceId) {
        //查找关联关系
        List<HarborRepoService> harborRepoServiceList = harborRepoServiceRepository.selectByCondition(Condition.builder(HarborRepoService.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborRepoService.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborRepoService.FIELD_APP_SERVICE_ID, appServiceId))
                .build());
        if (CollectionUtils.isNotEmpty(harborRepoServiceList)) {
            Set<Long> customIds = harborRepoServiceList.stream().map(HarborRepoService::getCustomRepoId).collect(Collectors.toSet());
            HarborRepoDTO harborRepoDTO = getCustomHarborConfig(projectId, customIds, appServiceId, HarborConstants.Y);
            return harborRepoDTO;
        }
        //查找共享仓库，否则返回默认仓库
        List<HarborCustomRepo> shareCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_SHARE, HarborConstants.TRUE)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isNotEmpty(shareCustomRepoList)) {
            HarborCustomRepo shareCustomRepo = shareCustomRepoList.get(0);
            shareCustomRepo.setPassword(DESEncryptUtil.decode(shareCustomRepo.getPassword()));
            shareCustomRepo.setPublicFlag(Boolean.parseBoolean(shareCustomRepo.getPublicFlag()) ? HarborConstants.FALSE : HarborConstants.TRUE);
            HarborRepoDTO harborRepoDTO = new HarborRepoDTO(appServiceId, projectId, shareCustomRepo);
            return harborRepoDTO;
        } else {
            HarborRepoDTO harborRepoDTO = getDefaultHarborConfig(projectId, null, appServiceId);
            return harborRepoDTO;
        }
    }

    @Override
    public HarborRepoDTO getHarborRepoConfigByRepoId(Long projectId, Long repoId, String repoType) {
        if (!StringUtils.equalsAny(repoType, HarborRepoDTO.CUSTOM_REPO, HarborRepoDTO.DEFAULT_REPO)) {
            throw new CommonException("error.harbor.config.repoType");
        }
        //fix 0624
        if (repoId == null || HarborRepoDTO.DEFAULT_REPO.equals(repoType)) {
            return getDefaultHarborConfig(projectId, repoId, null);
        } else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
            Set<Long> ids = new HashSet<Long>() {{
                add(repoId);
            }};
            return getCustomHarborConfig(projectId, ids, null, null);
        }
        return null;
    }

    @Override
    public HarborAllRepoDTO getAllHarborRepoConfigByProject(Long projectId) {
        HarborRepoConfigDTO harborDefaultRepoConfig = new HarborRepoConfigDTO();
        List<HarborRepoConfigDTO> harborCustomRepoConfigList = new ArrayList<>();

        List<HarborRepository> harborRepositoryList = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID, projectId);
        if (CollectionUtils.isNotEmpty(harborRepositoryList)) {
            HarborRepository harborRepository = harborRepositoryList.get(0);
            List<HarborRobot> harborRobotList = harborRobotService.getRobotByProjectId(projectId, null);
            harborRepository.setPublicFlag(Boolean.parseBoolean(harborRepository.getPublicFlag()) ? HarborConstants.FALSE : HarborConstants.TRUE);
            harborDefaultRepoConfig = new HarborRepoConfigDTO(harborRepository.getId(), harborInfoConfiguration.getBaseUrl(), harborRepository.getCode(), harborRepository.getPublicFlag(), harborRobotList);
        }

        List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isNotEmpty(harborCustomRepoList)) {
            harborCustomRepoList.stream().forEach(harborCustomRepo -> {
                harborCustomRepo.setPassword(DESEncryptUtil.decode(harborCustomRepo.getPassword()));
                harborCustomRepo.setPublicFlag(Boolean.parseBoolean(harborCustomRepo.getPublicFlag()) ? HarborConstants.FALSE : HarborConstants.TRUE);
                harborCustomRepoConfigList.add(new HarborRepoConfigDTO(harborCustomRepo.getId(), harborCustomRepo.getRepoUrl(), harborCustomRepo.getRepoName(), harborCustomRepo.getPublicFlag(), harborCustomRepo.getLoginName(), harborCustomRepo.getPassword(), harborCustomRepo.getEmail(), harborCustomRepo.getProjectShare()));
            });
        }
        return new HarborAllRepoDTO(projectId, harborDefaultRepoConfig, harborCustomRepoConfigList);
    }

    @Override
    public List<HarborImageVo> getImagesByRepoId(Long repoId, String imageName) {
        List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(HarborCustomRepo.FIELD_ID, repoId)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
                .build());
        if (CollectionUtils.isEmpty(harborCustomRepoList)) {
            return null;
        }
        HarborCustomRepo harborCustomRepo = harborCustomRepoList.get(0);
        getHarborProjectId(harborCustomRepo);
        if (null == harborCustomRepo.getHarborProjectId()) {
            return null;
        }

        List<HarborImageVo> harborImageVoList = getImageList(harborCustomRepo.getHarborProjectId(), imageName, harborCustomRepo.getRepoName());
        return harborImageVoList;
    }

    private void getHarborProjectId(HarborCustomRepo harborCustomRepo) {
        String password = DESEncryptUtil.decode(harborCustomRepo.getPassword());
        HarborCustomConfiguration harborCustomConfiguration = new HarborCustomConfiguration(harborCustomRepo.getRepoUrl(), harborCustomRepo.getLoginName(), password, harborCustomRepo.getApiVersion());
        harborHttpClient.setHarborCustomConfiguration(harborCustomConfiguration);

        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("name", harborCustomRepo.getRepoName());
        ResponseEntity<String> listProjectResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_PROJECT, paramMap, null);
        if (listProjectResponse.getBody() == null) {
            LOGGER.info("the response body of list project is null");
            return;
        }

        List<HarborProjectDTO> harborProjectDTOList = new Gson().fromJson(listProjectResponse.getBody(), new TypeToken<List<HarborProjectDTO>>() {
        }.getType());
        if (CollectionUtils.isEmpty(harborProjectDTOList)) {
            LOGGER.info("the harborProjectDTOList from json is null");
            return;
        }
        List<HarborProjectDTO> matchProjectDTO = harborProjectDTOList.stream().filter(a -> a.getName().equals(harborCustomRepo.getRepoName())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchProjectDTO)) {
            LOGGER.info("no match project from harborProjectDTOList ,the repoName is {}", harborCustomRepo.getRepoName());
            return;
        }
        harborCustomRepo.setHarborProjectId(matchProjectDTO.get(0).getHarborId());
    }

    private List<HarborImageVo> getImageList(Integer harborProjectId, String imageName, String repoName) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("project_id", harborProjectId);
        paramMap.put("q", imageName);
        ResponseEntity<String> responseEntity;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborCustomConfiguration())) {
            responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null);
        } else {
            responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null, getHarborProjectNameCustom(harborProjectId));
        }
        List<HarborImageVo> harborImageVoList = new ArrayList<>();
        if (responseEntity != null && !StringUtils.isEmpty(responseEntity.getBody())) {
            harborImageVoList = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageVo>>() {
            }.getType());
        }
        harborImageVoList.forEach(dto -> dto.setImageName(dto.getRepoName().substring(repoName.length() + 1)));
        return harborImageVoList;
    }

    private List<AppServiceDTO> batchQueryAppServiceByIds(Long projectId, Set<Long> ids, Boolean doPage, Boolean withVersion, String params) {
        ResponseEntity<Page<AppServiceDTO>> pageResponseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId, ids, doPage, withVersion, params);
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
    private void processRepoList(List<HarborCustomRepo> harborCustomRepoList) {
        if (CollectionUtils.isEmpty(harborCustomRepoList)) {
            return;
        }
        Set<Long> userIds = harborCustomRepoList.stream().map(x -> x.getCreatedBy()).collect(Collectors.toSet());
        Map<Long, UserDTO> userDTOMap = c7nBaseService.listUsersByIds(userIds);
        harborCustomRepoList.forEach(dto -> {
            UserDTO userDTO = userDTOMap.get(dto.getCreatedBy());
            if (null != userDTO) {
                dto.setCreatorImageUrl(userDTO.getImageUrl());
                dto.setCreatorLoginName(userDTO.getLoginName());
                dto.setCreatorRealName(userDTO.getRealName());
            }
        });
    }


    private String buildSearchParam(String appServiceName, String appServiceCode) {
        Map<String, Object> paramMap = new HashMap<>(2);
        if (StringUtils.isNotBlank(appServiceName)) {
            paramMap.put("name", appServiceName);
        }
        if (StringUtils.isNotBlank(appServiceCode)) {
            paramMap.put("code", appServiceCode);
        }
        String params = "";
        if (paramMap.size() > 0) {
            params = HarborUtil.castToSearchParam(paramMap);
        }
        return params;
    }

    private HarborRepoDTO getDefaultHarborConfig(Long projectId, Long repoId, Long appServiceId) {
        Sqls sql = Sqls.custom().andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectId);
        if (repoId != null) {
            sql.andEqualTo(HarborRepository.FIELD_ID, repoId);
        }
        List<HarborRepository> harborRepositoryList = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class)
                .andWhere(sql)
                .build());
        if (CollectionUtils.isEmpty(harborRepositoryList)) {
            return null;
        }
        HarborRepository harborRepository = harborRepositoryList.get(0);
        List<HarborRobot> harborRobotList = harborRobotService.getRobotByProjectId(projectId, null);
        if (CollectionUtils.isEmpty(harborRobotList)) {
            //TODO 创建机器人账户 throw new CommonException("error.harbor.robot.not.exist");
            harborRobotList = harborRobotService.generateRobotWhenNo(projectId);
        }
        harborRepository.setPublicFlag(Boolean.parseBoolean(harborRepository.getPublicFlag()) ? HarborConstants.FALSE : HarborConstants.TRUE);
        HarborRepoDTO harborRepoDTO = new HarborRepoDTO(appServiceId, projectId, harborRepository.getId(), harborInfoConfiguration.getBaseUrl(), harborRepository.getCode(), harborRepository.getPublicFlag(), harborRobotList);
        return harborRepoDTO;
    }


    private HarborRepoDTO getCustomHarborConfig(Long projectId, Set<Long> repoIds, Long appServiceId, String enabledFlag) {
        Sqls sql = Sqls.custom()
                //.andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
                .andIn(HarborCustomRepo.FIELD_ID, repoIds);
        if (StringUtils.equalsAny(enabledFlag, HarborConstants.Y, HarborConstants.N)) {
            sql.andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, enabledFlag);
        }
        List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(sql)
                .build());
        if (CollectionUtils.isEmpty(harborCustomRepoList)) {
            return null;
        }
        HarborCustomRepo harborCustomRepo = harborCustomRepoList.get(0);
        harborCustomRepo.setPassword(DESEncryptUtil.decode(harborCustomRepo.getPassword()));
        harborCustomRepo.setPublicFlag(Boolean.parseBoolean(harborCustomRepo.getPublicFlag()) ? HarborConstants.FALSE : HarborConstants.TRUE);
        HarborRepoDTO harborRepoDTO = new HarborRepoDTO(appServiceId, projectId, harborCustomRepo);
        return harborRepoDTO;
    }

    private List<HarborRepoService> filterDisableCustomRepo(List<HarborRepoService> repoServiceList) {
        //去除未启用的自定义仓库的关联关系
        Set<Long> customRepoServiceIds = repoServiceList.stream().map(HarborRepoService::getCustomRepoId).collect(Collectors.toSet());
        List<HarborCustomRepo> disableHarborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom()
                        .andIn(HarborCustomRepo.FIELD_ID, customRepoServiceIds)
                        .andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.N))
                .build());
        if (CollectionUtils.isNotEmpty(disableHarborCustomRepoList)) {
            Set<Long> disableCustomRepoServiceIds = disableHarborCustomRepoList.stream().map(HarborCustomRepo::getId).collect(Collectors.toSet());
            repoServiceList = repoServiceList.stream().filter(harborRepoService -> !disableCustomRepoServiceIds.contains(harborRepoService.getCustomRepoId())).collect(Collectors.toList());
        }
        return repoServiceList;
    }

    @Override
    public void batchSaveRelationByServiceIds(Long projectId, Long repoId, String repoType, List<Long> appServiceIds) {
        if (CollectionUtils.isEmpty(appServiceIds)) {
            if (HarborRepoDTO.DEFAULT_REPO.equals(repoType)) {
                //所有应用服务关联默认仓库，删除项目下所有应用服务与自定义服务的关联关系、设置所有自定义仓库projectShare=false
                harborRepoServiceRepository.deleteRelationByProjectId(projectId);
                harborRepoServiceRepository.updateProjectShareByProjectId(projectId, false, null);
            } else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
                //所有应用服务关联自定义仓库，删除项目与自定义服务的关联关系、设置当前自定义仓库projectShare=true，其他自定义仓库projectShare=false
                harborRepoServiceRepository.deleteRelationByProjectId(projectId);
                harborRepoServiceRepository.updateProjectShareByProjectId(projectId, false, null);
                harborRepoServiceRepository.updateProjectShareByProjectId(projectId, true, repoId);
            }
        } else {
            if (HarborRepoDTO.DEFAULT_REPO.equals(repoType)) {
                //部分应用服务关联默认仓库，删除应用服务与自定义服务的关联关系、设置所有自定义仓库projectShare=false
                harborRepoServiceRepository.deleteOtherRelationByService(projectId, appServiceIds, null);
            } else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
                //部分应用服务关联自定义仓库，删除这些应用服务与其他自定义服务的关联关系，创建应用服务与自定义服务的关联关系
                harborRepoServiceRepository.deleteOtherRelationByService(projectId, appServiceIds, repoId);
                appServiceIds.forEach(appServiceId -> saveRelationByService(projectId, appServiceId, repoId));
            }
        }
    }

    private String getHarborProjectNameCustom(Integer harborId) {
        ResponseEntity<String> detailResponseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT, null, null, true, harborId);
        HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
        if (harborProjectDTO == null) {
            throw new CommonException("error.get.harbor.project.detail");
        }
        return harborProjectDTO.getName();
    }
}
