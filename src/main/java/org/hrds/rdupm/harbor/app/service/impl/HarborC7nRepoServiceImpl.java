package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborC7nRepoVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.*;
import org.hrds.rdupm.harbor.config.HarborCustomConfiguration;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.domain.entity.*;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        Integer totalSize = harborProjectDTO == null ? 0 : harborProjectDTO.getRepoCount();
        String repoName = harborProjectDTO == null ? null : harborProjectDTO.getName();

        Map<String,Object> paramMap = new HashMap<>(4);
        paramMap.put("project_id",harborId);
        paramMap.put("q",imageName);
        ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE,paramMap,null,true);
        List<HarborImageVo> harborImageVoList = new ArrayList<>();
        if(responseEntity != null && !StringUtils.isEmpty(responseEntity.getBody())){
            harborImageVoList = new Gson().fromJson(responseEntity.getBody(),new com.google.gson.reflect.TypeToken<List<HarborImageVo>>(){}.getType());
        }
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
			repoName = harborRepository==null?null:harborRepository.getCode();

			//获取机器人账号
			List<HarborRobot> harborRobotList = harborRobotService.getRobotByProjectId(harborRepository.getProjectId(), HarborConstants.HarborRobot.ACTION_PULL);
			if (CollectionUtils.isEmpty(harborRobotList)) {
				harborRobotList = harborRobotService.generateRobotWhenNo(harborRepository.getProjectId());
			}
			pullAccount = harborRobotList.get(0).getName();
			pullPassword = harborRobotList.get(0).getToken();
		} else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
			HarborCustomRepo harborCustomRepo = harborCustomRepoRepository.selectByPrimaryKey(repoId);
			repoName = harborCustomRepo==null?null:harborCustomRepo.getRepoName();

			pullAccount = harborCustomRepo.getLoginName();
			pullPassword = DESEncryptUtil.decode(harborCustomRepo.getPassword());
			HarborCustomConfiguration harborCustomConfiguration = new HarborCustomConfiguration(harborCustomRepo.getRepoUrl(), pullAccount, pullPassword);
			harborHttpClient.setHarborCustomConfiguration(harborCustomConfiguration);
		}
		if(repoName == null){
			throw new CommonException("error.c7n.repo.not.exist");
		}

		ResponseEntity<String> registryUrlResponse = null;
		ResponseEntity<String> tagResponseEntity = null;
		String paramName = repoName + BaseConstants.Symbol.SLASH + imageName;
		Map<String,Object> paramMap = new HashMap<>(1);
		paramMap.put("detail","true");

		if(HarborRepoDTO.DEFAULT_REPO.equals(repoType)){
			registryUrlResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO,null,null,true);
			tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG,paramMap,null,true,paramName);
		} else if (HarborRepoDTO.CUSTOM_REPO.equals(repoType)) {
			registryUrlResponse = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO,null,null);
			tagResponseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG,paramMap,null,paramName);
		}

		//获取registryUrl
		Map<String,Object> resultMap = JSONObject.parseObject(registryUrlResponse.getBody(),Map.class);
		String registryUrl = resultMap ==null ? null : resultMap.get("registry_url").toString();

		//获取镜像版本
		List<HarborC7nRepoImageTagVo.HarborC7nImageTagVo> harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(),new TypeToken<List<HarborC7nRepoImageTagVo.HarborC7nImageTagVo>>(){}.getType());
		if(StringUtils.isNotEmpty(tagName)){
			harborImageTagVoList = harborImageTagVoList.stream().filter(dto->dto.getTagName().contains(tagName)).collect(Collectors.toList());
		}
		if(CollectionUtils.isEmpty(harborImageTagVoList)){
			return null;
		}

		//处理镜像版本
		harborImageTagVoList = harborImageTagVoList.stream().sorted(Comparator.comparing(HarborC7nRepoImageTagVo.HarborC7nImageTagVo::getPushTime).reversed()).collect(Collectors.toList());
		harborImageTagVoList.forEach(dto->{
			String pullCmd = String.format("docker pull %s/%s:%s",registryUrl, paramName,dto.getTagName());
			dto.setPullCmd(pullCmd);
		});
		HarborC7nRepoImageTagVo harborC7nRepoImageTagVo = new HarborC7nRepoImageTagVo(repoType,registryUrl,pullAccount,pullPassword,harborImageTagVoList);
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


}
