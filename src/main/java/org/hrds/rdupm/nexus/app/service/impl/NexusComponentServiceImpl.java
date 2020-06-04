package org.hrds.rdupm.nexus.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.nexus.api.dto.NexusComponentGuideDTO;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusComponentService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus 包信息应用服务默认实现
 * @author weisen.yang@hand-china.com 2020/4/2
 */
@Component
public class NexusComponentServiceImpl implements NexusComponentService {
	private static final Logger logger = LoggerFactory.getLogger(NexusComponentServiceImpl.class);


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

	@Override
	public Page<NexusServerComponentInfo> listComponents(Long organizationId, Long projectId, Boolean deleteFlag,
														 NexusComponentQuery componentQuery, PageRequest pageRequest) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

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

			List<NexusRepository> repositoryList = nexusRepositoryRepository.listRepositoryByProject(query);
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

	private void setUserInfoComponentInfo (List<NexusServerComponentInfo> componentInfoList) {
		Set<String> loginNameList = new HashSet<>();
		componentInfoList.forEach(componentInfo -> {
			loginNameList.addAll(componentInfo.getComponents().stream().map(NexusServerComponent::getCreatedBy).collect(Collectors.toSet()));
		});

		Condition condition = new Condition(ProdUser.class);
		condition.createCriteria().andIn(ProdUser.FIELD_LOGIN_NAME, loginNameList);
		List<ProdUser> prodUserList = prodUserRepository.selectByCondition(condition);
		Map<String, ProdUser> prodUserMap = prodUserList.stream().collect(Collectors.toMap(ProdUser::getLoginName, k -> k));

		if (CollectionUtils.isNotEmpty(prodUserList)) {
			Set<Long> userIdSet = prodUserList.stream().map(ProdUser::getUserId).collect(Collectors.toSet());
			Map<Long, UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
			componentInfoList.forEach(componentInfo -> {
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

	private void setUserInfoComponent(List<NexusServerComponent> componentList) {
		Set<String> loginNameList = new HashSet<>();
		componentList.forEach(component -> {
			loginNameList.add(component.getCreatedBy());
		});
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



	@Override
	public Page<NexusServerComponent> listComponentsVersion(Long organizationId, Long projectId, Boolean deleteFlag,
															NexusComponentQuery componentQuery, PageRequest pageRequest) {
		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

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
	public void deleteComponents(Long organizationId, Long projectId, String repositoryName, List<String> componentIds) {
		NexusRepository query = new NexusRepository();
		query.setProjectId(projectId);
		query.setNeRepositoryName(repositoryName);
		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		if (nexusRepository == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_NOT_DELETE_COMPONENT);
		}
		// 校验
		List<String> validateRoleCode = new ArrayList<>();
		validateRoleCode.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
		validateRoleCode.add(NexusConstants.NexusRoleEnum.DEVELOPER.getRoleCode());
		nexusAuthService.validateRoleAuth(nexusRepository.getRepositoryId(), validateRoleCode);


		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);
		if (CollectionUtils.isNotEmpty(componentIds)) {
			NexusComponentDeleteParam deleteParam = new NexusComponentDeleteParam();
			deleteParam.setRepositoryName(repositoryName);
			deleteParam.setComponents(componentIds);
			nexusClient.getComponentsApi().deleteComponentScript(deleteParam);
		}
		// remove配置信息
		nexusClient.removeNexusServerInfo();
	}

	@Override
	public void componentsUpload(Long organizationId, Long projectId,
								 NexusServerComponentUpload componentUpload,
								 MultipartFile assetJar, MultipartFile assetPom) {
		// 设置并返回当前nexus服务信息
		configService.setCurrentNexusInfo(nexusClient);
		try (
				InputStream assetJarStream = assetJar != null ? assetJar.getInputStream() : null;
				InputStream assetPomStream = assetPom != null ? assetPom.getInputStream() : null
		) {
			List<NexusServerAssetUpload> assetUploadList = new ArrayList<>();
			if (assetJarStream != null) {
				NexusServerAssetUpload assetUpload = new NexusServerAssetUpload();
				assetUpload.setAssetName(new InputStreamResource(assetJarStream));
				assetUpload.setExtension(NexusServerAssetUpload.JAR);
				assetUploadList.add(assetUpload);
			}
			if (assetPomStream != null) {
				NexusServerAssetUpload assetUpload = new NexusServerAssetUpload();
				assetUpload.setAssetName(new InputStreamResource(assetPomStream));
				assetUpload.setExtension(NexusServerAssetUpload.POM);
				assetUploadList.add(assetUpload);
			}
			componentUpload.setAssetUploads(assetUploadList);
			nexusClient.getComponentsApi().createMavenComponent(componentUpload);
		} catch (IOException e) {
			logger.error("上传jar包错误", e);
			throw new CommonException(e.getMessage());
		} finally {
			// remove配置信息
			nexusClient.removeNexusServerInfo();
		}

	}



	@Override
	public void npmComponentsUpload(Long organizationId, Long projectId, String repositoryName, MultipartFile assetTgz) {
		// 设置并返回当前nexus服务信息
		configService.setCurrentNexusInfo(nexusClient);
		try (
				InputStream assetTgzStream = assetTgz != null ? assetTgz.getInputStream() : null;
		) {

			if (assetTgzStream != null) {
				InputStreamResource streamResource = new InputStreamResource(assetTgzStream);
				nexusClient.getComponentsApi().createNpmComponent(repositoryName, streamResource);
			}
		} catch (IOException e) {
			logger.error("上传jar包错误", e);
			throw new CommonException(e.getMessage());
		} finally {
			// remove配置信息
			nexusClient.removeNexusServerInfo();
		}

	}

	@Override
	public NexusComponentGuideDTO componentGuide(NexusServerComponentInfo componentInfo) {

		// 设置并返回当前nexus服务信息
		configService.setNexusInfo(nexusClient);

		NexusRepository query = new NexusRepository();
		query.setNeRepositoryName(componentInfo.getRepository());
		NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
		NexusUser nexusUser = null;
		if (nexusRepository != null) {
			NexusUser queryUser = new NexusUser();
			queryUser.setRepositoryId(nexusRepository.getRepositoryId());
			nexusUser = nexusUserRepository.selectOne(queryUser);
		}
		NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(componentInfo.getRepository());
		if (nexusServerRepository == null) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		}
		// 返回数据
		NexusComponentGuideDTO componentGuideDTO = new NexusComponentGuideDTO();

		componentGuideDTO.handleDepGuideValue(componentInfo);

		// 设置拉取配置信息
		componentGuideDTO.handlePullGuideValue(nexusServerRepository, nexusRepository, nexusUser);
		// remove配置信息
		nexusClient.removeNexusServerInfo();
		return componentGuideDTO;
	}
}
