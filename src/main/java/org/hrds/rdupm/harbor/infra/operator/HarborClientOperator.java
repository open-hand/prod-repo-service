package org.hrds.rdupm.harbor.infra.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborArtifactDTO;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborBuildLogDTO;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/3/18
 * @Modified By:
 */
@Component
public class HarborClientOperator {
    private static Gson gson = new Gson();
    @Autowired
    private HarborHttpClient harborHttpClient;
    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    public Integer getRepoCountByHarborId(Long harborId) {
        //获得镜像数
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.DETAIL_PROJECT, null, null, true, harborId);
            HarborProjectDTO harborProjectDTO = new Gson().fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
            return harborProjectDTO == null ? 0 : harborProjectDTO.getRepoCount();
        } else {
            ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_SUMMARY, null, null, true, harborId);
            Map<String, Object> summaryMap = new Gson().fromJson(detailResponseEntity.getBody(), Map.class);
            Double repoCount = summaryMap == null || summaryMap.get("repo_count") == null ? 0L : (Double) summaryMap.get("repo_count");
            return Integer.parseInt(new java.text.DecimalFormat("0").format(repoCount));
        }
    }

    public List<HarborImageLog> listImageLogs(Map<String, Object> paramMap, HarborRepository harborRepository) {
        ResponseEntity<String> responseEntity;
        List<HarborImageLog> logListResult;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, true, harborRepository.getHarborId());
            logListResult = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
            }.getType());
        } else {
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, true, harborRepository.getCode());
            logListResult = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
            }.getType());
            if (logListResult != null) {
                logListResult = logListResult.stream().map(t -> {
                    if (t.getResource().contains(":")) {
                        String[] strings = t.getResource().split(":");
                        t.setRepoName(strings[0]);
                        t.setTagName(strings[1]);
                    }
                    return t;
                }).collect(Collectors.toList());
            }
        }
        return logListResult;
    }

    public List<HarborImageTagVo> listImageTags(String repoName) {
        return listImageTags(repoName, false);
    }

    public List<HarborImageTagVo> listImageTags(String repoName, Boolean isCustom) {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("detail", "true");
        ResponseEntity<String> tagResponseEntity;
        List<HarborImageTagVo> harborImageTagVoList;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            if (isCustom) {
                tagResponseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, repoName);
            } else {
                tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, repoName);
            }
            harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborImageTagVo>>() {
            }.getType());
            if (CollectionUtils.isEmpty(harborImageTagVoList)) {
                return new ArrayList<>();
            }
            harborImageTagVoList.forEach(dto -> {
                dto.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(dto.getSize())));
                dto.setPullTime(HarborConstants.DEFAULT_DATE.equals(dto.getPullTime()) ? null : dto.getPullTime());
            });
        } else {
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            if (isCustom) {
                tagResponseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, strArr[0], strArr[1]);
            } else {
                tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, strArr[0], strArr[1]);
            }
            harborImageTagVoList = new Gson().fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborImageTagVo>>() {
            }.getType());
            ResponseEntity<String> response = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO, null, null, true);
            Map<String, Object> systemMap = JSONObject.parseObject(response.getBody(), Map.class);
            if (systemMap == null) {
                throw new CommonException("error.get.system.version");
            }
            String dockerVersion = systemMap.get("harbor_version").toString();
            harborImageTagVoList.forEach(dto -> {
                dto.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(dto.getSize())));
                dto.setPullTime(HarborConstants.DEFAULT_DATE_V2.equals(dto.getPullTime()) ? null : dto.getPullTime());
                dto.setArchitecture(dto.getExtraAttrs().getArchitecture());
                dto.setOs(dto.getExtraAttrs().getOs());
                dto.setDockerVersion(dockerVersion);
                if (!CollectionUtils.isEmpty(dto.getTags())) {
//                    List<String> tags = dto.getTags().stream().map(HarborImageTagVo.Tag::getName).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(dto.getTags())) {
                        dto.setTagName(dto.getTags().get(0).getName());
                    }
                }
            });
            if (CollectionUtils.isEmpty(harborImageTagVoList)) {
                return new ArrayList<>();
            }
        }
        return harborImageTagVoList;
    }

    public List<HarborBuildLogDTO> listBuildLogs(String repoName, String tagName, String digest) {
        ResponseEntity<String> responseEntity;
        List<HarborBuildLogDTO> buildLogDTOList;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_IMAGE_BUILD_LOG, null, null, true, repoName, tagName);
            Map<String, Object> map = gson.fromJson(responseEntity.getBody(), Map.class);
            String config = (String) map.get("config");
            Map<String, Object> configMap = gson.fromJson(config, Map.class);
            List<Map<String, Object>> historyList = (List<Map<String, Object>>) configMap.get("history");
            buildLogDTOList = new ArrayList<>();
            for (Map<String, Object> history : historyList) {
                buildLogDTOList.add(new HarborBuildLogDTO(history.get("created").toString(), history.get("created_by").toString()));
            }
        } else {
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_IMAGE_BUILD_LOG, null, null, true, strArr[0], strArr[1], digest);
            buildLogDTOList = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborBuildLogDTO>>() {
            }.getType());
        }
        return buildLogDTOList;
    }

    public void deleteImageByTag(String repoName, String tagName) {
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE_TAG, null, null, true, repoName, tagName);
        } else {
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            ResponseEntity<String> tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, null, null, true, strArr[0], strArr[1]);
            List<HarborArtifactDTO> artifactDTOList = new Gson().fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborArtifactDTO>>() {
            }.getType());
            if (CollectionUtils.isEmpty(artifactDTOList)) {
                return;
            }
            artifactDTOList.forEach(t -> {
                if (CollectionUtils.isEmpty(t.getTags())) {
                    return;
                }
                t.getTags().forEach(tag -> {
                    if (tag.getTagName().equals(tagName)) {
                        harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE_TAG, null, null, true, strArr[0], strArr[1], t.getDigest());
                    }
                });
            });
        }
    }

    public void deleteImage(String repoName) {
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE, null, null, false, repoName);
        } else {
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_IMAGE, null, null, true, strArr[0], strArr[1]);
        }
    }

    public void copyTag(HarborImageReTag harborImageReTag) {
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            String srcImage = harborImageReTag.getSrcRepoName() + BaseConstants.Symbol.COLON + harborImageReTag.getDigest();
            String destRepoName = harborImageReTag.getDestProjectCode() + BaseConstants.Symbol.SLASH + harborImageReTag.getDestImageName();
            Map<String, Object> bodyMap = new HashMap<>(3);
            bodyMap.put("override", true);
            bodyMap.put("tag", harborImageReTag.getDestImageTagName());
            bodyMap.put("src_image", srcImage);
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.COPY_IMAGE_TAG, null, bodyMap, true, destRepoName);
        } else {
            Map<String, Object> paramsMap = new HashMap<>(1);
            paramsMap.put("from", String.format("%s@%s", harborImageReTag.getSrcRepoName(), harborImageReTag.getDigest()));
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.COPY_IMAGE_TAG, paramsMap, null, true, harborImageReTag.getDestProjectCode(), harborImageReTag.getDestImageName());
        }
    }

    public List<HarborImageVo> listImages(Long harborId, Integer page, Integer pageSize, String imageName) {
        return listImages(harborId, page, pageSize, imageName, false);
    }

    public List<HarborImageVo> listImages(Long harborId, Integer page, Integer pageSize, String imageName, Boolean isCustom) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("project_id", harborId);
        paramMap.put("page", page);
        paramMap.put("page_size", pageSize);
        ResponseEntity<String> responseEntity;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            if (isCustom) {
                responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null, true);
            } else {
                responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null, true);
            }
        } else {
            String harborProjectName = harborRepositoryRepository.getHarborRepositoryByHarborId(harborId).getCode();
            if (isCustom) {
                responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null, true, harborProjectName);
            } else {
                responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE, paramMap, null, true, harborProjectName);
            }
        }
        List<HarborImageVo> harborImageVoList = new ArrayList<>();
        if (responseEntity != null && !StringUtils.isEmpty(responseEntity.getBody())) {
            harborImageVoList = new Gson().fromJson(responseEntity.getBody(), new com.google.gson.reflect.TypeToken<List<HarborImageVo>>() {
            }.getType());
            harborImageVoList.forEach(dto -> {
                if (dto.getRepoName().contains(BaseConstants.Symbol.SLASH)) {
                    dto.setImageName(dto.getRepoName().split(BaseConstants.Symbol.SLASH)[1]);
                } else {
                    dto.setImageName("");
                }
                if (dto.getArtifactCount() != null) {
                    dto.setTagsCount(dto.getArtifactCount());
                }
            });
            if (StringUtils.isNotEmpty(imageName)) {
                harborImageVoList = harborImageVoList.stream().filter(t -> t.getImageName().contains(imageName)).collect(Collectors.toList());
            }
        }
        return harborImageVoList;
    }

    public void updateImageDesc(HarborImageVo harborImageVo) {
        String repoName = harborImageVo.getRepoName();
        Map<String, String> bodyMap = new HashMap<>(1);
        bodyMap.put("description", harborImageVo.getDescription());
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_IMAGE_DESC, null, bodyMap, true, repoName);
        } else {
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_IMAGE_DESC, null, bodyMap, true, strArr[0], strArr[1]);
        }
    }


}
