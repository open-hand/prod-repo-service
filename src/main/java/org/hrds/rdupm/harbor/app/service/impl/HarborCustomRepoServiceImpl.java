package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.infra.feign.DevopsServiceFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.config.HarborCustomConfiguration;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;
import org.hrds.rdupm.harbor.domain.entity.User;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepoServiceRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
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

            List<HarborProjectDTO> matchProjectDTO = harborProjectDTOList.stream().filter(a->a.getName().equals(harborCustomRepo.getRepoName())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(matchProjectDTO)){
                throw new CommonException("error.harbor.custom.repo.no.match.project", harborCustomRepo.getRepoName());
            }
            harborCustomRepo.setPublicFlag(matchProjectDTO.get(0).getMetadata().getPublicFlag());
        } else {
            throw new CommonException("error.harbor.custom.repo.repoName.empty");
        }
        return true;
    }

    @Override
    public List<HarborCustomRepo> listByProjectId(Long projectId) {
        //todo 密码加密 过滤显示
        List<HarborCustomRepo> harborCustomRepos = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
                .andWhere(Sqls.custom().andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId))
                .build());
        return harborCustomRepos;
    }

    @Override
    public HarborCustomRepo detailByRepoId(Long repoId) {
        
        return null;
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
        harborCustomRepoRepository.insertSelective(harborCustomRepo);
        //创建时关联应用服务
        if (CollectionUtils.isNotEmpty(harborCustomRepo.getAppServiceIds())) {
            ResponseEntity<Page<AppServiceDTO>> pageResponseEntity = devopsServiceFeignClient.listAppServiceByIds(projectId, harborCustomRepo.getAppServiceIds(), false, true, null);
            if (CollectionUtils.isNotEmpty(Objects.requireNonNull(pageResponseEntity.getBody().getContent()))) {
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
                    throw new CommonException("error.feign.appService.list.ids");
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProject(Long projectId, HarborCustomRepo harborCustomRepo) {
        HarborCustomRepo dbRepo = harborCustomRepoRepository.selectByPrimaryKey(harborCustomRepo.getId());
        if (dbRepo == null) {
            throw new CommonException("error.harbor.custom.repo.not.exists");
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
    public void relateServiceByProject(Long projectId, HarborCustomRepo harborCustomRepo, Set<Long> appServiceIds) {
        ProjectDTO projectDTO = c7nBaseService.queryProjectById(projectId);



    }

    @Override
    public Page<AppServiceDTO> pageRelatedService(Long projectId, HarborCustomRepo harborCustomRepo) {

        return null;
    }

    @Override
    public void createByOrg(Long organizationId, HarborCustomRepo harborCustomRepo) {
        checkCustomRepo(harborCustomRepo);
        if (StringUtils.isBlank(harborCustomRepo.getPublicFlag()) || !StringUtils.equalsAny(harborCustomRepo.getPublicFlag(), HarborConstants.TRUE, HarborConstants.FALSE)) {
            harborCustomRepo.setPublicFlag(HarborConstants.FALSE);
        }
        harborCustomRepoRepository.insertSelective(harborCustomRepo);

        HarborRepoService harborRepoService = new HarborRepoService();
        harborRepoService.setOrganizationId(organizationId);
        harborRepoService.setCustomRepoId(harborCustomRepo.getId());
        harborRepoServiceRepository.insertSelective(harborRepoService);

    }


}
