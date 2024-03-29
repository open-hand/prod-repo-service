package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import com.google.inject.internal.asm.$Attribute;
import java.io.File;
import java.util.function.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.api.vo.MavenComponentVO;
import org.hrds.rdupm.nexus.app.service.*;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hrds.rdupm.util.JsonHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus 包信息应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020/4/2
 */
@Component
public class NexusComponentServiceImpl implements NexusComponentService {
    private static final Logger logger = LoggerFactory.getLogger(NexusComponentServiceImpl.class);

    @Value("${nexus.choerodon.capacity.limit.base: 2}")
    private Integer nexusBaseCapacityLimit;

    /**
     * 企业版 一个项目限制5G
     */
    @Value("${nexus.choerodon.capacity.limit.business: 5}")
    private Integer nexusBusinessCapacityLimit;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusServerConfigService configService;
    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusUserRepository nexusUserRepository;
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;
    @Autowired
    private NexusAuthService nexusAuthService;
    @Autowired
    private C7nBaseService c7nBaseService;
    @Autowired
    private ProdUserRepository prodUserRepository;
    @Autowired
    private NexusProxyConfigProperties nexusProxyConfigProperties;
    @Autowired
    private NexusComponentHandService nexusComponentHandService;

    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;
    @Autowired
    private NexusRepositoryService nexusRepositoryService;

    @Override
    public Page<NexusServerComponentInfo> listComponents(Long organizationId, Long projectId, Boolean deleteFlag,
                                                         NexusComponentQuery componentQuery, PageRequest pageRequest) {
        NexusRepository queryExist = new NexusRepository();
        queryExist.setRepositoryId(componentQuery.getRepositoryId());
        queryExist.setOrganizationId(organizationId);
        NexusRepository exist = nexusRepositoryRepository.selectOne(queryExist);
        if (exist == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        componentQuery.setRepositoryName(exist.getNeRepositoryName());
        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, componentQuery.getRepositoryId());

        // 查询所有数据
        List<NexusServerComponentInfo> componentInfoList = new ArrayList<>();
        if (componentQuery.getRepoType().equals(NexusConstants.RepoType.MAVEN)) {
            componentInfoList = nexusClient.getComponentsApi().searchMavenComponentInfo(componentQuery);
        } else if (componentQuery.getRepoType().equals(NexusConstants.RepoType.NPM)) {
            componentInfoList = nexusClient.getComponentsApi().searchNpmComponentInfo(componentQuery);
        } else {
            return new Page<>();
        }

        // 分页
        Page<NexusServerComponentInfo> componentInfoPage = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), componentInfoList);

        if (deleteFlag && projectId != null) {
            NexusRepository query = new NexusRepository();
            query.setOrganizationId(organizationId);
            query.setRepoType(componentQuery.getRepoType());
            query.setProjectId(projectId);

            List<NexusRepository> repositoryList = nexusRepositoryRepository.listRepositoryByProject(query, serverConfig.getConfigId());
            Map<String, NexusRepository> repositoryMap = repositoryList.stream().collect(Collectors.toMap(NexusRepository::getNeRepositoryName, k -> k));
            List<String> proRepoList = repositoryList.stream().filter(nexusRepository -> Objects.equals(nexusRepository.getProjectId(), projectId)).map(NexusRepository::getNeRepositoryName).collect(Collectors.toList());

            // 项目名称查询
            Set<Long> projectIdSet = repositoryList.stream().map(NexusRepository::getProjectId).collect(Collectors.toSet());
            List<ProjectVO> projectVOList = baseServiceFeignClient.queryByIds(projectIdSet);
            Map<Long, ProjectVO> projectVOMap = projectVOList.stream().collect(Collectors.toMap(ProjectVO::getId, a -> a, (k1, k2) -> k1));


            componentInfoPage.getContent().forEach(nexusServerComponentInfo -> {
                NexusRepository nexusRepository = repositoryMap.get(nexusServerComponentInfo.getName());
                ProjectVO projectVO = nexusRepository == null ? null : projectVOMap.get(nexusRepository.getProjectId());
                if (projectVO != null) {
                    nexusServerComponentInfo.setProjectName(projectVO.getName());
                    nexusServerComponentInfo.setProjectImgUrl(projectVO.getImageUrl());
                }

                nexusServerComponentInfo.setDeleteFlag(proRepoList.contains(nexusServerComponentInfo.getRepository()));
                nexusServerComponentInfo.getComponents().forEach(nexusServerComponent -> {
                    nexusServerComponent.setDeleteFlag(nexusServerComponentInfo.getDeleteFlag());
                });

            });

        }
        this.setUserInfoComponentInfo(componentInfoPage.getContent());
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return componentInfoPage;
    }

    private void setUserInfoComponentInfo(List<NexusServerComponentInfo> componentInfoList) {
        Set<String> loginNameList = new HashSet<>();
        componentInfoList.forEach(componentInfo -> {
            loginNameList.add(componentInfo.getCreatedBy());
            loginNameList.addAll(componentInfo.getComponents().stream().map(NexusServerComponent::getCreatedBy).collect(Collectors.toSet()));
        });

        loginNameList.removeAll(Collections.singleton(null));

        if (CollectionUtils.isNotEmpty(loginNameList)) {
            Condition condition = new Condition(ProdUser.class);
            condition.createCriteria().andIn(ProdUser.FIELD_LOGIN_NAME, loginNameList);
            List<ProdUser> prodUserList = prodUserRepository.selectByCondition(condition);
            Map<String, ProdUser> prodUserMap = prodUserList.stream().collect(Collectors.toMap(ProdUser::getLoginName, k -> k));

            if (CollectionUtils.isNotEmpty(prodUserList)) {
                Set<Long> userIdSet = prodUserList.stream().map(ProdUser::getUserId).collect(Collectors.toSet());
                Map<Long, UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
                componentInfoList.forEach(componentInfo -> {

                    UserDTO componentInfoUserDTO = prodUserMap.get(componentInfo.getCreatedBy()) == null ? null : userDtoMap.get(prodUserMap.get(componentInfo.getCreatedBy()).getUserId());
                    if (componentInfoUserDTO != null) {
                        componentInfo.setCreatorImageUrl(componentInfoUserDTO.getImageUrl());
                        componentInfo.setCreatorLoginName(componentInfoUserDTO.getLoginName());
                        componentInfo.setCreatorRealName(componentInfoUserDTO.getRealName());
                    } else {
                        componentInfo.setCreatorLoginName(componentInfo.getCreatedBy());
                        componentInfo.setCreatorRealName(componentInfo.getCreatedBy());
                    }

                    componentInfo.getComponents().forEach(component -> {
                        UserDTO userDTO = prodUserMap.get(component.getCreatedBy()) == null ? null : userDtoMap.get(prodUserMap.get(component.getCreatedBy()).getUserId());
                        if (userDTO != null) {
                            component.setCreatorImageUrl(userDTO.getImageUrl());
                            component.setCreatorLoginName(userDTO.getLoginName());
                            component.setCreatorRealName(userDTO.getRealName());
                        } else {
                            component.setCreatorLoginName(component.getCreatedBy());
                            component.setCreatorRealName(component.getCreatedBy());
                        }
                    });
                });
            }
        }

    }

    private void setUserInfoComponent(List<NexusServerComponent> componentList) {
        Set<String> loginNameList = new HashSet<>();
        componentList.forEach(component -> {
            loginNameList.add(component.getCreatedBy());
        });
        if (CollectionUtils.isNotEmpty(loginNameList)) {
            Condition condition = new Condition(ProdUser.class);
            condition.createCriteria().andIn(ProdUser.FIELD_LOGIN_NAME, loginNameList);
            List<ProdUser> prodUserList = prodUserRepository.selectByCondition(condition);
            Map<String, ProdUser> prodUserMap = prodUserList.stream().collect(Collectors.toMap(ProdUser::getLoginName, k -> k));

            if (CollectionUtils.isNotEmpty(prodUserList)) {
                Set<Long> userIdSet = prodUserList.stream().map(ProdUser::getUserId).collect(Collectors.toSet());
                Map<Long, UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
                componentList.forEach(component -> {
                    UserDTO userDTO = prodUserMap.get(component.getCreatedBy()) == null ? null : userDtoMap.get(prodUserMap.get(component.getCreatedBy()).getUserId());
                    if (userDTO != null) {
                        component.setCreatorImageUrl(userDTO.getImageUrl());
                        component.setCreatorLoginName(userDTO.getLoginName());
                        component.setCreatorRealName(userDTO.getRealName());
                    } else {
                        component.setCreatorLoginName(component.getCreatedBy());
                        component.setCreatorRealName(component.getCreatedBy());
                    }
                });
            }
        }
    }


    @Override
    public Page<NexusServerComponent> listComponentsVersion(Long organizationId, Long projectId, Boolean deleteFlag,
                                                            NexusComponentQuery componentQuery, PageRequest pageRequest) {
        // 设置并返回当前nexus服务信息
        configService.setNexusInfoByRepositoryId(nexusClient, componentQuery.getRepositoryId());

        List<NexusServerComponentInfo> componentInfoList = nexusClient.getComponentsApi().searchNpmComponentInfo(componentQuery);
        Map<String, NexusServerComponentInfo> componentInfoMap = componentInfoList.stream().collect(Collectors.toMap(NexusServerComponentInfo::getName, k -> k));
        NexusServerComponentInfo componentInfo = componentInfoMap.get(componentQuery.getName());
        if (componentInfo == null || CollectionUtils.isEmpty(componentInfo.getComponents())) {
            return new Page<>();
        }
        componentInfo.getComponents().forEach(nexusServerComponent -> {
            nexusServerComponent.setDeleteFlag(true);
        });
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        Page<NexusServerComponent> componentPage = PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), componentInfo.getComponents());
        this.setUserInfoComponent(componentPage.getContent());
        return componentPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComponents(Long organizationId, Long projectId, Long repositoryId, List<String> componentIds) {

        NexusRepository nexusRepository = this.validateAuth(projectId, repositoryId);

        // 设置并返回当前nexus服务信息
        configService.setNexusInfoByRepositoryId(nexusClient, repositoryId);

        NexusServerRepository serverRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        if (serverRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (serverRepository.getType().equals(NexusApiConstants.RepositoryType.GROUP)) {
            throw new CommonException(NexusMessageConstants.NEXUS_GROUP_NOT_DELETE_COMPONENT);
        }
        if (serverRepository.getWritePolicy().equals(NexusApiConstants.WritePolicy.DENY)) {
            throw new CommonException(NexusMessageConstants.NEXUS_REPO_IS_READ_ONLY_NOT_DELETE);
        }

        if (CollectionUtils.isNotEmpty(componentIds)) {
            NexusComponentDeleteParam deleteParam = new NexusComponentDeleteParam();
            deleteParam.setRepositoryName(nexusRepository.getNeRepositoryName());
            deleteParam.setComponents(componentIds);
            nexusClient.getComponentsApi().deleteComponentScript(deleteParam);
        }
        //删除数据库的包
        if (CollectionUtils.isNotEmpty(componentIds)) {
            nexusAssetsMapper.batchDelete(componentIds);

        }
        // remove配置信息
        nexusClient.removeNexusServerInfo();
    }


    @Override
    public void componentsUpload(Long organizationId, Long projectId, NexusServerComponentUpload componentUpload, String filePath, MultipartFile assetPom) {
        NexusRepository nexusRepository = this.validateAuth(projectId, componentUpload.getRepositoryId());
        componentUpload.setRepositoryName(nexusRepository.getNeRepositoryName());

        checkRepositoryCapacityLimit(organizationId, componentUpload.getRepositoryId(), filePath);

        NexusServerConfig defaultNexusServerConfig = configService.setNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        NexusServerRepository serverRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        if (serverRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (serverRepository.getWritePolicy().equals(NexusApiConstants.WritePolicy.DENY)) {
            throw new CommonException(NexusMessageConstants.NEXUS_REPO_IS_READ_ONLY_NOT_UPLOAD);
        }
        // 设置并返回当前nexus服务信息
        NexusServer currentNexusServer = configService.setCurrentNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        File jarfilePath = null;
        //当只上传jar包的时候，filePath就为空
        if (StringUtils.isNotBlank(filePath)) {
            jarfilePath = new File(filePath);
        }
        InputStream assetPomStream = null;
        try {
            assetPomStream = assetPom != null ? assetPom.getInputStream() : null;
        } catch (IOException e) {
            logger.error("获取文件输入流失败", e);
        }
        nexusComponentHandService.uploadJar(componentUpload.getRepositoryId(), nexusClient, jarfilePath, componentUpload, currentNexusServer, assetPomStream);
    }

    private void checkRepositoryCapacityLimit(Long organizationId, Long repositoryId, String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        ExternalTenantVO externalTenantVO = c7nBaseService.queryTenantByIdWithExternalInfo(organizationId);
        if (Objects.isNull(externalTenantVO)) {
            throw new CommonException("tenant not exists");
        }
        File file = new File(filePath);
        long fileSize = file.length();
        logger.info(">>>>>>>>>上传的包的大小为:{}>>>>>>>>>>", fileSize);
        if ((externalTenantVO.getRegister() != null && externalTenantVO.getRegister())
                || StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.FREE.name())
                || StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.STANDARD.name())) {
            Long totalSize = nexusRepositoryService.queryNexusProjectCapacity(repositoryId);
            logger.info(">>>>>>>>>>>仓库的容量限制为{}>>>>>>>>>>>>>>>>", HarborUtil.getStorageLimit(nexusBaseCapacityLimit, HarborConstants.GB));
            logger.info(">>>>>>>>>>>已经使用的仓库的大小为{}>>>>>>>>>>>>>>>>", totalSize);
            if (totalSize + fileSize >= HarborUtil.getStorageLimit(nexusBaseCapacityLimit, HarborConstants.GB)) {
                throw new CommonException("Exceeded repository capacity limit");
            }
        }
        if (StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.SENIOR.name())) {
            Long totalSize = nexusRepositoryService.queryNexusProjectCapacity(repositoryId);
            if (totalSize + fileSize >= HarborUtil.getStorageLimit(nexusBusinessCapacityLimit, HarborConstants.GB)) {
                throw new CommonException("Exceeded repository capacity limit");
            }
        }
    }


    @Override
    public void npmComponentsUpload(Long organizationId, Long projectId, Long repositoryId, String filePath) {
        NexusRepository nexusRepository = this.validateAuth(projectId, repositoryId);

        configService.setNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        NexusServerRepository serverRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        if (serverRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (serverRepository.getWritePolicy().equals(NexusApiConstants.WritePolicy.DENY)) {
            throw new CommonException(NexusMessageConstants.NEXUS_REPO_IS_READ_ONLY_NOT_UPLOAD);
        }
        //
        checkRepositoryCapacityLimit(organizationId, repositoryId, filePath);
        // 设置并返回当前nexus服务信息
        NexusServer currentNexusServer = configService.setCurrentNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        File npmfilePath = new File(filePath);
        nexusComponentHandService.uploadNPM(repositoryId, nexusClient, nexusRepository, npmfilePath, currentNexusServer);
    }

    private NexusRepository validateAuth(Long projectId, Long repositoryId) {
        NexusRepository query = new NexusRepository();
        query.setProjectId(projectId);
        query.setRepositoryId(repositoryId);
        NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 校验
        List<String> validateRoleCode = new ArrayList<>();
        validateRoleCode.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
        validateRoleCode.add(NexusConstants.NexusRoleEnum.DEVELOPER.getRoleCode());
        nexusAuthService.validateRoleAuth(nexusRepository.getRepositoryId(), validateRoleCode);

        return nexusRepository;
    }

    @Override
    public NexusComponentGuideDTO componentGuide(NexusServerComponentInfo componentInfo) {
        NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(componentInfo.getRepositoryId());
        if (nexusRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        componentInfo.setRepository(nexusRepository.getNeRepositoryName());

        // 设置并返回当前nexus服务信息
        NexusServerConfig serverConfig = configService.setNexusInfoByRepositoryId(nexusClient, componentInfo.getRepositoryId());


        NexusUser queryUser = new NexusUser();
        queryUser.setRepositoryId(nexusRepository.getRepositoryId());
        NexusUser nexusUser = nexusUserRepository.selectOne(queryUser);

        NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(componentInfo.getRepository());
        if (nexusServerRepository == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 返回数据
        NexusComponentGuideDTO componentGuideDTO = new NexusComponentGuideDTO();

        componentGuideDTO.handleDepGuideValue(componentInfo);

        // 设置拉取配置信息
        componentGuideDTO.handlePullGuideValue(nexusServerRepository, nexusRepository, nexusUser, serverConfig, nexusProxyConfigProperties);
        // remove配置信息
        nexusClient.removeNexusServerInfo();
        return componentGuideDTO;
    }

    @Override
    public void batchDeleteComponents(Long organizationId, Long projectId, Long repositoryId, List<MavenComponentVO> mavenComponentVOS) {
        //按照仓库进行分组
        if (CollectionUtils.isEmpty(mavenComponentVOS)) {
            return;
        }
        mavenComponentVOS.forEach(mavenComponentVO -> {
            deleteComponents(organizationId, projectId, repositoryId, mavenComponentVO.getComponentIds());
        });
    }
}
