package org.hrds.rdupm.nexus.app.service.impl;

import groovy.sql.Sql;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.api.dto.C7nNexusComponentDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusRepoDTO;
import org.hrds.rdupm.nexus.api.dto.C7nNexusServerDTO;
import org.hrds.rdupm.nexus.app.service.NexusChoerodonService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 制品库_nexus 猪齿鱼接口应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020/7/2
 */
@Service
public class NexusChoerodonServiceImpl implements NexusChoerodonService {

    private static final String BACK_SLASH = "/";
    private static final String DOR = ".";
    private static final String HYPHEN = "-";

    @Autowired
    private NexusServerConfigRepository nexusServerConfigRepository;
    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusServerConfigService nexusServerConfigService;
    @Autowired
    private NexusClient nexusClient;

    @Override
    public List<C7nNexusServerDTO> getNexusServerByProject(Long organizationId, Long projectId) {
        // 默认nexus服务信息查询
        NexusServerConfig query = new NexusServerConfig();
        query.setDefaultFlag(BaseConstants.Flag.YES);
        NexusServerConfig defaultConfig = nexusServerConfigRepository.selectOne(query);
        defaultConfig.setProjectId(projectId);

        // 项目下，自定义的nexus服务信息
        List<NexusServerConfig> nexusServerConfigList = nexusServerConfigRepository.queryList(organizationId, projectId);

        List<C7nNexusServerDTO> result = new ArrayList<>();
        C7nNexusServerDTO c7nDefault = new C7nNexusServerDTO();
        BeanUtils.copyProperties(defaultConfig, c7nDefault);
        result.add(c7nDefault);

        nexusServerConfigList.forEach(serverConfig -> {
            C7nNexusServerDTO c7nNexusServerDTO = new C7nNexusServerDTO();
            BeanUtils.copyProperties(serverConfig, c7nNexusServerDTO);
            result.add(c7nNexusServerDTO);
        });
        return result;
    }

    @Override
    public List<C7nNexusRepoDTO> getRepoByConfig(Long organizationId, Long projectId, Long configId, String repoType) {
        List<C7nNexusRepoDTO> result = new ArrayList<>();

        Sqls sqls = Sqls.custom().andEqualTo(NexusRepository.FIELD_CONFIG_ID, configId)
                .andEqualTo(NexusRepository.FIELD_PROJECT_ID, projectId)
                .andEqualTo(NexusRepository.FIELD_ORGANIZATION_ID, organizationId)
                .andEqualTo(NexusRepository.FIELD_REPO_TYPE, repoType);
        List<NexusRepository> nexusRepositoryList = nexusRepositoryRepository.selectByCondition(Condition.builder(NexusRepository.class).where(sqls).build());

        nexusRepositoryList.forEach(nexusRepository -> {
            C7nNexusRepoDTO c7nNexusRepoDTO = new C7nNexusRepoDTO();
            BeanUtils.copyProperties(nexusRepository, c7nNexusRepoDTO);
            result.add(c7nNexusRepoDTO);
        });
        return result;
    }

    @Override
    public List<C7nNexusComponentDTO> listMavenComponents(Long organizationId, Long projectId, Long repositoryId, String repoType, String groupId, String artifactId, String versionRegular) {

        Sqls sqls = Sqls.custom().andEqualTo(NexusRepository.FIELD_PROJECT_ID, projectId)
                .andEqualTo(NexusRepository.FIELD_ORGANIZATION_ID, organizationId)
                .andEqualTo(NexusRepository.FIELD_REPOSITORY_ID, repositoryId);
        NexusRepository nexusRepository = nexusRepositoryRepository.selectByCondition(Condition.builder(NexusRepository.class).where(sqls).build()).stream().findFirst().orElse(null);
        if (nexusRepository == null) {
            return new ArrayList<>();
        }

        nexusServerConfigService.setNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());

        NexusComponentQuery componentQuery = new NexusComponentQuery();
        componentQuery.setRepositoryName(nexusRepository.getNeRepositoryName());
        componentQuery.setGroup(groupId);
        componentQuery.setName(artifactId);


        NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        if (nexusServerRepository == null) {
            return new ArrayList<>();
        }

        List<C7nNexusComponentDTO> result = new ArrayList<>();

        if (repoType.equals(NexusConstants.RepoType.MAVEN)) {
            // MAVEN
            List<NexusServerComponentInfo> componentList = nexusClient.getComponentsApi().searchMavenComponentInfo(componentQuery);
            for (NexusServerComponentInfo componentInfo : componentList) {
                C7nNexusComponentDTO componentDTO = new C7nNexusComponentDTO();
                BeanUtils.copyProperties(componentInfo, componentDTO);

                NexusServerComponent serverComponent = componentInfo.getComponents().stream().max(Comparator.comparing(NexusServerComponent::getVersion)).orElse(null);
                if (serverComponent == null) {
                    // release版本没有，下级
                    componentDTO.setDownloadUrl(nexusServerRepository.getUrl() + BACK_SLASH + componentInfo.getPath() + BACK_SLASH
                            + componentInfo.getName() + HYPHEN + componentInfo.getVersion() + DOR + componentInfo.getExtension());
                } else {
                    componentDTO.setDownloadUrl(nexusServerRepository.getUrl() + BACK_SLASH + serverComponent.getPath() + BACK_SLASH
                            + serverComponent.getName() + HYPHEN + serverComponent.getVersion() + DOR + componentInfo.getExtension());
                }
                result.add(componentDTO);
            }
            result = result.stream().sorted(Comparator.comparing(C7nNexusComponentDTO::getGroup).thenComparing(C7nNexusComponentDTO::getName).thenComparing(C7nNexusComponentDTO::getVersion).reversed()).collect(Collectors.toList());
        } else if (repoType.equals(NexusConstants.RepoType.NPM)) {
            // npm
            List<NexusServerComponent> componentList = nexusClient.getComponentsApi().searchNpmComponent(componentQuery);
            for (NexusServerComponent nexusServerComponent : componentList) {
                C7nNexusComponentDTO componentDTO = new C7nNexusComponentDTO();
                BeanUtils.copyProperties(nexusServerComponent, componentDTO);
                result.add(componentDTO);
            }
            result = result.stream().sorted(Comparator.comparing(C7nNexusComponentDTO::getName).thenComparing(C7nNexusComponentDTO::getVersion).reversed()).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

        // 版本匹配
        if (StringUtils.isNotBlank(versionRegular)) {
            result = result.stream().filter(c7nNexusComponentDTO -> Pattern.matches(versionRegular, c7nNexusComponentDTO.getVersion())).collect(Collectors.toList());
        }
        nexusClient.removeNexusServerInfo();
        return result;
    }

    @Override
    public List<String> listMavenGroup(Long organizationId, Long projectId, Long repositoryId, String groupId) {
        NexusRepository nexusRepository = this.validExist(organizationId, projectId, repositoryId);
        if (nexusRepository == null) {
            return new ArrayList<>();
        }
        // 组件查询
        List<NexusServerComponent> componentList = this.listComponentList(nexusRepository.getNeRepositoryName(), groupId, null);
        List<String> result = componentList.stream().map(NexusServerComponent::getGroup).distinct().collect(Collectors.toList());

        nexusClient.removeNexusServerInfo();
        return result;
    }

    @Override
    public List<String> listMavenArtifactId(Long organizationId, Long projectId, Long repositoryId, String artifactId) {
        NexusRepository nexusRepository = this.validExist(organizationId, projectId, repositoryId);
        if (nexusRepository == null) {
            return new ArrayList<>();
        }
        // 组件查询
        List<NexusServerComponent> componentList = this.listComponentList(nexusRepository.getNeRepositoryName(), null, artifactId);
        List<String> result = componentList.stream().map(NexusServerComponent::getName).distinct().collect(Collectors.toList());

        nexusClient.removeNexusServerInfo();

        return result;
    }

    @Override
    public List<String> listMavenGroupArtifactId(Long organizationId, Long projectId, Long repositoryId, String groupId) {
        NexusRepository nexusRepository = this.validExist(organizationId, projectId, repositoryId);
        if (nexusRepository == null) {
            return new ArrayList<>();
        }
        // 组件查询
        List<NexusServerComponent> componentList = this.listComponentList(nexusRepository.getNeRepositoryName(), groupId, null);

        // 精确匹配过滤数据
        List<String> result = componentList.stream().filter(nexusServerComponent -> nexusServerComponent.getGroup().equals(groupId))
                .map(NexusServerComponent::getName).distinct().collect(Collectors.toList());

        nexusClient.removeNexusServerInfo();

        return result;
    }

    private List<NexusServerComponent> listComponentList(String neRepositoryName, String groupId, String artifactId) {
        NexusComponentQuery componentQuery = new NexusComponentQuery();
        componentQuery.setRepositoryName(neRepositoryName);
        if (groupId != null) {
            componentQuery.setGroup(groupId);
        }
        if (artifactId != null) {
            componentQuery.setName(artifactId);
        }
        return nexusClient.getComponentsApi().searchComponentScript(componentQuery);
    }

    private NexusRepository validExist(Long organizationId, Long projectId, Long repositoryId) {
        // 数据库表查询
        Sqls sqls = Sqls.custom().andEqualTo(NexusRepository.FIELD_PROJECT_ID, projectId)
                .andEqualTo(NexusRepository.FIELD_ORGANIZATION_ID, organizationId)
                .andEqualTo(NexusRepository.FIELD_REPOSITORY_ID, repositoryId);
        NexusRepository nexusRepository = nexusRepositoryRepository.selectByCondition(Condition.builder(NexusRepository.class).where(sqls).build()).stream().findFirst().orElse(null);
        if (nexusRepository == null) {
            return null;
        }

        nexusServerConfigService.setNexusInfoByRepositoryId(nexusClient, nexusRepository.getRepositoryId());
        // 仓库查询
        NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(nexusRepository.getNeRepositoryName());
        if (nexusServerRepository == null || !nexusServerRepository.getFormat().equals(NexusApiConstants.NexusRepoFormat.MAVEN_FORMAT)) {
            return null;
        }
        return nexusRepository;
    }
}
