package org.hrds.rdupm.harbor.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.app.service.HarborImageTagService;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 1:44 下午
 */
@Service
public class HarborImageTagServiceImpl implements HarborImageTagService {

	@Autowired
	private HarborHttpClient harborHttpClient;

	@Override
	public PageInfo<HarborImageTagVo> list(String repoName, String tagName, PageRequest pageRequest) {
		ResponseEntity<String> tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG,null,null,false,repoName);
		List<HarborImageTagVo> harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(),new TypeToken<List<HarborImageTagVo>>(){}.getType());
		if(StringUtils.isNotEmpty(tagName)){
			harborImageTagVoList = harborImageTagVoList.stream().filter(dto->dto.getTagName().equals(tagName)).collect(Collectors.toList());
		}

		PageInfo<HarborImageTagVo> pageInfo = PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),harborImageTagVoList);
		pageInfo.getList().stream().forEach(dto->{
			dto.setSizeDesc(HarborUtil.getTagSizeDesc(dto.getSize()));
		});
		return pageInfo;
	}

	@Override
	public String buildLog(String repoName, String tagName) {
		StringBuffer sb = new StringBuffer();

		Gson gson = new Gson();
		ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_IMAGE_BUILD_LOG,null,null,false,repoName,tagName);
		Map<String,Object> map = gson.fromJson(responseEntity.getBody(),Map.class);
		String config = (String) map.get("config");
		Map<String,Object> configMap = gson.fromJson(config,Map.class);
		List<Map<String,Object>> historyList = (List<Map<String, Object>>) configMap.get("history");
		for(Map<String,Object> history : historyList){
			sb.append(history.get("created")).append("  ").append(history.get("created_by")).append("\n");
		}
		return sb.toString();
	}

	@Override
	public void delete(String repoName, String tagName) {
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE_TAG,null,null,true,repoName,tagName);
	}

	@Override
	public void copyTag(HarborImageReTag harborImageReTag) {
		String srcImage = harborImageReTag.getSrcRepoName() + BaseConstants.Symbol.COLON + harborImageReTag.getDigest();
		String destRepoName = harborImageReTag.getDestProjectCode() + BaseConstants.Symbol.SLASH + harborImageReTag.getDestImageName();
		Map<String,Object> bodyMap = new HashMap<>(3);
		bodyMap.put("override",true);
		bodyMap.put("tag",harborImageReTag.getDestImageTagName());
		bodyMap.put("src_image",srcImage);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.COPY_IMAGE_TAG,null,bodyMap,true,destRepoName);
	}
}
