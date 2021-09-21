package org.hrds.rdupm.harbor.app.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.*;
import org.hrds.rdupm.harbor.app.service.*;
import org.hrds.rdupm.harbor.config.HarborCustomConfiguration;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.AppServiceDTO;
import org.hrds.rdupm.harbor.infra.operator.HarborClientOperator;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.util.ConvertUtil;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;

/**
 * 制品库-猪齿鱼Harbor仓库应用服务默认实现
 *
 * @author mofei.li@hand-china.com 2020/07/06 10:03
 */
@Service
public class HarborC7nRepoServiceImpl implements HarborC7nRepoService {

    @Autowired
    private HarborHttpClient harborHttpClient;
    @Autowired
    private HarborCustomRepoService harborCustomRepoService;
    @Autowired
    private HarborRepositoryService harborRepositoryService;
    @Autowired
    private HarborImageService harborImageService;
    @Autowired
	private HarborRepositoryRepository harborRepositoryRepository;
    @Autowired
	private HarborCustomRepoRepository harborCustomRepoRepository;
    @Autowired
	private HarborRobotService harborRobotService;
    @Autowired
	private HarborInfoConfiguration harborInfoConfiguration;
    @Autowired
	private C7nBaseService c7nBaseService;
    @Autowired
	private HarborClientOperator harborClientOperator;

    @Override
    public List<HarborImageVo> getImagesByRepoId(Long repoId, String repoType, String imageName) {
        if (!StringUtils.equalsAnyIgnoreCase(repoType, HarborConstants.HarborRepoType.CUSTOM_REPO, HarborConstants.HarborRepoType.DEFAULT_REPO)) {
            return null;
        }
        if (HarborConstants.HarborRepoType.CUSTOM_REPO.equalsIgnoreCase(repoType)) {
            return harborCustomRepoService.getImagesByRepoId(repoId, imageName);
        } else {
            return getDefaultRepoImagesByRepoId(repoId, imageName);
        }
    }

    private List<HarborImageVo> getDefaultRepoImagesByRepoId(Long repoId, String imageName) {
        Gson gson = new Gson();
        HarborRepository harborRepository = harborRepositoryRepository.selectByPrimaryKey(repoId);
        if(harborRepository == null){
        	return null;
        }
        Long harborId = harborRepository.getHarborId();

        //获得镜像数
        ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT,null,null,true,harborId);
        HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
        String repoName = harborProjectDTO == null ? null : harborProjectDTO.getName();

		List<HarborImageVo> harborImageVoList = harborClientOperator.listImages(harborId, null, null, imageName);
        harborImageVoList.forEach(dto->dto.setImageName(dto.getRepoName().substring(repoName.length()+1)));
        return harborImageVoList;
    }

	@Override
	public HarborC7nRepoImageTagVo listImageTag(String repoType, Long repoId, String imageName, String tagName) {
		if(repoId == null || StringUtils.isEmpty(repoType) || StringUtils.isEmpty(imageName)){
			return null;
		}
		//获取镜像仓库名称、配置连接信息
		String repoName = null;
		String pullAccount = null;
		String pullPassword = null;
		if(HarborRepoDTO.DEFAULT_REPO.equals(repoType)){
			HarborRepository harborRepository = harborRepositoryRepository.selectByPrimaryKey(repoId);
			AssertUtils.notNull(harborRepository, "error.default.harbor.repo.not.exist");
			repoName = harborRepository.getCode();

			//获取机器人账号
			List<HarborRobot> harborRobotList = harborRobotService.getRobotByProjectId(harborRepository.getProjectId(), HarborConstants.HarborRobot.ACTION_PULL);
			if (CollectionUtils.isEmpty(harborRobotList)) {
				harborRobotList = harborRobotService.generateRobotWhenNo(harborRepository.getProjectId());
			}
			pullAccount = harborRobotList.get(0).getName();
			pullPassword = harborRobotList.get(0).getToken();
		} else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
			HarborCustomRepo harborCustomRepo = harborCustomRepoRepository.selectByPrimaryKey(repoId);
			if (harborCustomRepo == null) {
				throw new CommonException("error.get.custom.repository");
			}
			repoName = harborCustomRepo.getRepoName();
			pullAccount = harborCustomRepo.getLoginName();
			pullPassword = DESEncryptUtil.decode(harborCustomRepo.getPassword());
			HarborCustomConfiguration harborCustomConfiguration = new HarborCustomConfiguration(harborCustomRepo.getRepoUrl(), pullAccount, pullPassword, harborCustomRepo.getApiVersion());
			harborHttpClient.setHarborCustomConfiguration(harborCustomConfiguration);
		}
		if(repoName == null){
			throw new CommonException("error.c7n.repo.not.exist");
		}

		ResponseEntity<String> registryUrlResponse = null;
		ResponseEntity<String> tagResponseEntity = null;
		String paramName = repoName + BaseConstants.Symbol.SLASH + imageName;
		List<HarborImageTagVo> harborImageTagVoList = new ArrayList<>();
		if (HarborRepoDTO.DEFAULT_REPO.equals(repoType)) {
			registryUrlResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO,null,null,true);
			// paramName=  dev-25-test-25-4/choerodon-register
			harborImageTagVoList = harborClientOperator.listImageTags(paramName);
		} else {
			registryUrlResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO,null,null);
			harborImageTagVoList = harborClientOperator.listImageTags(paramName, true);
		}

		//获取registryUrl
		Map<String,Object> resultMap = JSONObject.parseObject(registryUrlResponse.getBody(),Map.class);
		String registryUrl = resultMap ==null ? null : resultMap.get("registry_url").toString();

		//获取镜像版本
		if (StringUtils.isNotBlank(tagName)) {
			harborImageTagVoList = harborImageTagVoList.stream().filter(dto -> !StringUtils.isEmpty(dto.getTagName()) && dto.getTagName().contains(tagName)).collect(Collectors.toList());
		}
		//V2 过滤掉tag name 为null的镜像
		harborImageTagVoList = harborImageTagVoList.stream().filter(harborImageTagVo -> StringUtils.isNotBlank(harborImageTagVo.getTagName())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(harborImageTagVoList)) {
			return null;
		}
		//处理镜像版本
		harborImageTagVoList = harborImageTagVoList.stream().sorted(Comparator.comparing(HarborImageTagVo::getPushTime).reversed()).collect(Collectors.toList());
		List<HarborC7nImageTagVo> harborC7nImageTagVoList = ConvertUtil.convertList(harborImageTagVoList, HarborC7nImageTagVo.class);
		harborC7nImageTagVoList.forEach(dto -> {
			String pullCmd = String.format("docker pull %s/%s:%s", registryUrl, paramName, dto.getTagName());
			dto.setPullCmd(pullCmd);
		});
		HarborC7nRepoImageTagVo harborC7nRepoImageTagVo = new HarborC7nRepoImageTagVo(repoType, registryUrl, pullAccount, pullPassword, harborC7nImageTagVoList);
		return harborC7nRepoImageTagVo;
	}

	@Override
	public List<HarborC7nRepoVo> listImageRepo(Long projectId) {
		HarborRepository harborRepository = harborRepositoryRepository.select(HarborRepository.FIELD_PROJECT_ID, projectId).stream().findFirst().orElse(null);
		List<HarborCustomRepo> harborCustomRepoList = harborCustomRepoRepository.selectByCondition(Condition.builder(HarborCustomRepo.class)
				.andWhere(Sqls.custom()
						.andEqualTo(HarborCustomRepo.FIELD_PROJECT_ID, projectId)
						.andEqualTo(HarborCustomRepo.FIELD_ENABLED_FLAG, HarborConstants.Y))
				.build());
		List<HarborC7nRepoVo> list = new ArrayList<>();
		if(harborRepository != null){
			HarborC7nRepoVo harborC7nRepoVo = new HarborC7nRepoVo(harborRepository.getId(),harborRepository.getCode(),HarborRepoDTO.DEFAULT_REPO);
			list.add(harborC7nRepoVo);
		}
		if(CollectionUtils.isNotEmpty(harborCustomRepoList)){
			for(HarborCustomRepo harborCustomRepo : harborCustomRepoList){
				HarborC7nRepoVo harborC7nRepoVo = new HarborC7nRepoVo(harborCustomRepo.getId(),harborCustomRepo.getRepoName(),HarborRepoDTO.CUSTOM_REPO);
				list.add(harborC7nRepoVo);
			}
		}
		return list;
	}

	@Override
	public HarborC7nRepoImageTagVo listImageTagByAppServiceId(Long projectId, Long appServiceId) {
		HarborRepoDTO harborRepoDTO = harborCustomRepoService.getHarborRepoConfig(projectId,appServiceId);
		if(harborRepoDTO == null || harborRepoDTO.getHarborRepoConfig() == null){
			return null;
		}
		Long repoId = harborRepoDTO.getHarborRepoConfig().getRepoId();
		String repoType = harborRepoDTO.getRepoType();
		AppServiceDTO appServiceDTO = c7nBaseService.queryAppServiceById(projectId,appServiceId);
		String imageName = appServiceDTO == null ? null : appServiceDTO.getName();
		if(StringUtils.isEmpty(imageName)){
			return null;
		}
		return listImageTag(repoType,repoId,imageName,null);
	}


}
