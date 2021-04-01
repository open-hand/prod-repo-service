package org.hrds.rdupm.harbor.infra.operator;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.*;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborArtifactDTO;
import org.hrds.rdupm.harbor.domain.entity.v2.HarborBuildLogDTO;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.TypeUtil;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
            HarborProjectDTO harborProjectDTO = gson.fromJson(detailResponseEntity.getBody(), HarborProjectDTO.class);
            return harborProjectDTO == null ? 0 : harborProjectDTO.getRepoCount();
        } else {
            ResponseEntity<String> detailResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_SUMMARY, null, null, true, harborId);
            Map<String, Object> summaryMap = gson.fromJson(detailResponseEntity.getBody(), Map.class);
            Double repoCount = summaryMap == null || summaryMap.get("repo_count") == null ? 0L : (Double) summaryMap.get("repo_count");
            return Integer.parseInt(new java.text.DecimalFormat("0").format(repoCount));
        }
    }

    public List<HarborImageLog> listImageLogs(Map<String, Object> paramMap, HarborRepository harborRepository) {
        return listImageLogs(paramMap, harborRepository.getHarborId(), harborRepository.getCode());
    }

    public List<HarborImageLog> listCustomImageLogs(HarborCustomRepo harborCustomRepo) {
        //自定harbor仓库日志
        return listCustomImageLogs(harborCustomRepo.getHarborProjectId(), harborCustomRepo.getRepoName());
    }

    private List<HarborImageLog> listCustomImageLogs(Integer harborProjectId, String harborProjectCode) {
        ResponseEntity<String> responseEntity;
        List<HarborImageLog> logListResult = new ArrayList<>();
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborCustomConfiguration())) {
            int page = 1;
            int pageSize = 10;
            List<HarborImageLog> harborImageLogs = new ArrayList<>();
            do {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("operation", HarborConstants.HarborImageOperateEnum.PULL.getOperateType());
                paramMap.put("page", page);
                paramMap.put("page_size", pageSize);
                responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, harborProjectId);
                harborImageLogs = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
                }.getType());
                page++;
                pageSize = +10;
                if (!CollectionUtils.isEmpty(harborImageLogs)) {
                    logListResult.addAll(harborImageLogs);
                }
            } while (!CollectionUtils.isEmpty(harborImageLogs));
        } else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("operation", HarborConstants.HarborImageOperateEnum.PULL.getOperateType());
            paramMap.put("page", 0);
            paramMap.put("page_size", 0);
            responseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, harborProjectCode);
            logListResult = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
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


    public List<HarborImageLog> listImageLogs(Map<String, Object> paramMap, Long harborProjectId, String harborProjectCode) {
        ResponseEntity<String> responseEntity;
        List<HarborImageLog> logListResult = new ArrayList<>();
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            int page = 1;
            int pageSize = 10;
            List<HarborImageLog> harborImageLogs = new ArrayList<>();
            do {
                paramMap.put("page", page);
                paramMap.put("page_size", pageSize);
                responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, true, harborProjectId);
                harborImageLogs = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
                }.getType());
                page++;
                pageSize = +10;
                if (!CollectionUtils.isEmpty(harborImageLogs)) {
                    logListResult.addAll(harborImageLogs);
                }
            } while (!CollectionUtils.isEmpty(harborImageLogs));
        } else {
            paramMap.put("page", 0);
            paramMap.put("page_size", 0);
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_LOGS_PROJECT, paramMap, null, true, harborProjectCode);
            logListResult = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageLog>>() {
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
            harborImageTagVoList = gson.fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborImageTagVo>>() {
            }.getType());
            if (CollectionUtils.isEmpty(harborImageTagVoList)) {
                return new ArrayList<>();
            }
            harborImageTagVoList.forEach(dto -> {
                dto.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(dto.getSize())));
                dto.setPullTime(HarborConstants.DEFAULT_DATE.equals(dto.getPullTime()) ? null : dto.getPullTime());
                if (dto.getScanOverviewJson() != null) {
                    HarborImageTagVo.ScanOverview scanOverview = dto.new ScanOverview();
                    scanOverview.setScanStatus(TypeUtil.objToString(dto.getScanOverviewJson().get("scan_status")).toUpperCase());
                    scanOverview.setSeverity(getSecurity(TypeUtil.objTodouble(dto.getScanOverviewJson().get("severity"))));
                    if (dto.getScanOverviewJson().get("components") != null) {
                        Map<String, Object> imageMap = (Map<String, Object>) dto.getScanOverviewJson().get("components");
                        scanOverview.setTotal(Math.round(TypeUtil.objTodouble(imageMap.get("total"))));
                        HarborImageTagVo.Summary summary = dto.new Summary();
                        if (imageMap.get("summary") != null) {
                            List<Object> summaryMap = (List<Object>) imageMap.get("summary");
                            summaryMap.stream().forEach(t -> {
                                Map<String, Object> map = (Map<String, Object>) t;
                                setSecurity(TypeUtil.objTodouble(map.get("severity")), TypeUtil.objTodouble(map.get("count")), summary);
                            });
                        }
                        scanOverview.setSummary(summary);
                    }
                    if (dto.getScanOverviewJson().get("job_id") != null) {
                        double jobIdDouble = TypeUtil.objTodouble(dto.getScanOverviewJson().get("job_id"));
                        String url = String.format("%s/api/jobs/scan/%s/log", harborHttpClient.getHarborInfo().getBaseUrl(), Math.round(jobIdDouble));
                        scanOverview.setLogUrl(url);
                    }
                    dto.setScanOverview(scanOverview);
                }
                List<HarborImageTagVo.Tag> tags = new ArrayList<>();
                HarborImageTagVo.Tag tag = dto.new Tag();
                tag.setName(dto.getTagName());
                tag.setPullTime(dto.getPullTime());
                tag.setPushTime(dto.getPushTime());
                tags.add(tag);
                dto.setTags(tags);

                dto.setScanOverviewJson(null);
                dto.setExtraAttrs(null);
            });
        } else {
            paramMap.put("with_tag", "true");
            paramMap.put("with_scan_overview", "true");
            String[] strArr = repoName.split(BaseConstants.Symbol.SLASH);
            if (isCustom) {
                tagResponseEntity = harborHttpClient.customExchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, strArr[0], strArr[1]);
            } else {
                tagResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_IMAGE_TAG, paramMap, null, true, strArr[0], strArr[1]);
            }
            harborImageTagVoList = gson.fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborImageTagVo>>() {
            }.getType());
            if (CollectionUtils.isEmpty(harborImageTagVoList)) {
                return new ArrayList<>();
            }
            harborImageTagVoList.forEach(dto -> {
                dto.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(dto.getSize())));
                dto.setPullTime(HarborConstants.DEFAULT_DATE_V2.equals(dto.getPullTime()) ? null : dto.getPullTime());
                dto.setArchitecture(dto.getExtraAttrs().getArchitecture());
                dto.setOs(dto.getExtraAttrs().getOs());
                if (dto.getScanOverviewJson() != null) {
                    Map<String, Object> imageMap = (Map<String, Object>) dto.getScanOverviewJson().get("application/vnd.scanner.adapter.vuln.report.harbor+json; version=1.0");
                    HarborImageTagVo.ScanOverview scanOverview;
                    if (imageMap.get("summary") != null) {
                        String jsonString = gson.toJson(imageMap.get("summary"));
                        Map<String, Object> summaryMap = (Map<String, Object>) imageMap.get("summary");
                        scanOverview = gson.fromJson(jsonString, HarborImageTagVo.ScanOverview.class);
                        jsonString = gson.toJson(summaryMap.get("summary"));
                        HarborImageTagVo.Summary summary = gson.fromJson(jsonString, HarborImageTagVo.Summary.class);
                        scanOverview.setSummary(summary);
                    } else {
                        scanOverview = dto.new ScanOverview();
                    }
                    if (imageMap.get("report_id") != null) {
                        String reportId = TypeUtil.objToString(imageMap.get("report_id"));
                        String url = String.format("%s/api/v2.0/projects/%s/repositories/%s/artifacts/%s/scan/%s/log", harborHttpClient.getHarborInfo().getBaseUrl(), strArr[0], strArr[1], dto.getDigest(), reportId);
                        scanOverview.setLogUrl(url);
                    }
                    scanOverview.setScanStatus(TypeUtil.objToString(imageMap.get("scan_status")).toUpperCase());
                    scanOverview.setSeverity(TypeUtil.objToString(imageMap.get("severity")));
                    dto.setScanOverview(scanOverview);
                } else {
                    dto.setScanOverview(null);
                }
                dto.setScanOverviewJson(null);
                dto.setExtraAttrs(null);
            });
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
            List<HarborArtifactDTO> artifactDTOList = gson.fromJson(tagResponseEntity.getBody(), new TypeToken<List<HarborArtifactDTO>>() {
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
            harborImageVoList = gson.fromJson(responseEntity.getBody(), new com.google.gson.reflect.TypeToken<List<HarborImageVo>>() {
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


    public void scanImage(HarborImageScanVO imageScanVO) {
        imageScanVO.setRepoName(imageScanVO.getRepoName().replace("%2F", BaseConstants.Symbol.SLASH));
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN, null, null, true, imageScanVO.getRepoName(), imageScanVO.getTagName());
        } else {
            String[] strArr = imageScanVO.getRepoName().split(BaseConstants.Symbol.SLASH);
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN, null, null, true, strArr[0], strArr[1], imageScanVO.getDigest());
        }
    }

    public List<HarborImageScanResultVO> queryImageScanDetail(HarborImageScanVO imageScanVO) {
        imageScanVO.setRepoName(imageScanVO.getRepoName().replace("%2F", BaseConstants.Symbol.SLASH));
        ResponseEntity<String> responseEntity;
        List<HarborImageScanResultVO> imageScanResultVOS;
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN_DETAIL, null, null, true, imageScanVO.getRepoName(), imageScanVO.getTagName());
            imageScanResultVOS = gson.fromJson(responseEntity.getBody(), new TypeToken<List<HarborImageScanResultVO>>() {
            }.getType());
            if (CollectionUtils.isEmpty(imageScanResultVOS)) {
                imageScanResultVOS.forEach(t -> {
                    t.setLinks(Collections.singletonList(t.getLink()));
                    t.setSeverity(getSecurity(TypeUtil.objTodouble(t.getSeverityObject())));
                    t.setFixVersion(t.getFixedVersion());
                });
            }
        } else {
            String[] strArr = imageScanVO.getRepoName().split(BaseConstants.Symbol.SLASH);
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN_DETAIL, null, null, true, strArr[0], strArr[1], imageScanVO.getDigest());
            JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
            Map<String, Object> imageMap = (Map<String, Object>) jsonObject.get("application/vnd.scanner.adapter.vuln.report.harbor+json; version=1.0");
            String jsonString = gson.toJson(imageMap.get("vulnerabilities"));
            imageScanResultVOS = gson.fromJson(jsonString, new TypeToken<List<HarborImageScanResultVO>>() {
            }.getType());
            imageScanResultVOS.forEach(t -> {
                t.setSeverity(TypeUtil.objToString(t.getSeverityObject()));
                t.setSeverityObject(null);
            });
        }
        return imageScanResultVOS;
    }


    public HarborImageTagVo queryImageScanResult(HarborImageScanVO imageScanVO) {
        imageScanVO.setRepoName(imageScanVO.getRepoName().replace("%2F", BaseConstants.Symbol.SLASH));
        ResponseEntity<String> responseEntity;
        HarborImageTagVo harborImageTagVo;
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("detail", "true");
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN_RESULT, paramMap, null, true, imageScanVO.getRepoName(), imageScanVO.getTagName());
            harborImageTagVo = gson.fromJson(responseEntity.getBody(), HarborImageTagVo.class);
            harborImageTagVo.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(harborImageTagVo.getSize())));
            harborImageTagVo.setPullTime(HarborConstants.DEFAULT_DATE.equals(harborImageTagVo.getPullTime()) ? null : harborImageTagVo.getPullTime());
            if (harborImageTagVo.getScanOverviewJson() != null) {
                HarborImageTagVo.ScanOverview scanOverview = harborImageTagVo.new ScanOverview();
                scanOverview.setScanStatus(TypeUtil.objToString(harborImageTagVo.getScanOverviewJson().get("scan_status")));
                scanOverview.setSeverity(getSecurity(TypeUtil.objTodouble(harborImageTagVo.getScanOverviewJson().get("severity"))));
                Map<String, Object> imageMap = (Map<String, Object>) harborImageTagVo.getScanOverviewJson().get("components");
                scanOverview.setTotal(Math.round(TypeUtil.objTodouble(imageMap.get("total"))));
                HarborImageTagVo.Summary summary = harborImageTagVo.new Summary();
                if (imageMap.get("summary") != null) {
                    List<Object> summaryMap = (List<Object>) imageMap.get("summary");
                    summaryMap.stream().forEach(t -> {
                        Map<String, Object> map = (Map<String, Object>) t;
                        setSecurity(TypeUtil.objTodouble(map.get("severity")), TypeUtil.objTodouble(map.get("count")), summary);
                    });
                }
                scanOverview.setSummary(summary);
                harborImageTagVo.setScanOverview(scanOverview);
            }
            List<HarborImageTagVo.Tag> tags = new ArrayList<>();
            HarborImageTagVo.Tag tag = harborImageTagVo.new Tag();
            tag.setName(harborImageTagVo.getTagName());
            tag.setPullTime(harborImageTagVo.getPullTime());
            tag.setPushTime(harborImageTagVo.getPushTime());
            tags.add(tag);
            harborImageTagVo.setTags(tags);

            harborImageTagVo.setScanOverviewJson(null);
            harborImageTagVo.setExtraAttrs(null);
        } else {
            String[] strArr = imageScanVO.getRepoName().split(BaseConstants.Symbol.SLASH);
            paramMap.put("with_tag", "true");
            paramMap.put("with_scan_overview", "true");
            responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_SCAN_RESULT, paramMap, null, true, strArr[0], strArr[1], imageScanVO.getDigest());
            harborImageTagVo = gson.fromJson(responseEntity.getBody(), HarborImageTagVo.class);
            harborImageTagVo.setSizeDesc(HarborUtil.getTagSizeDesc(Long.valueOf(harborImageTagVo.getSize())));
            harborImageTagVo.setPullTime(HarborConstants.DEFAULT_DATE_V2.equals(harborImageTagVo.getPullTime()) ? null : harborImageTagVo.getPullTime());
            harborImageTagVo.setArchitecture(harborImageTagVo.getExtraAttrs().getArchitecture());
            harborImageTagVo.setOs(harborImageTagVo.getExtraAttrs().getOs());
            if (harborImageTagVo.getScanOverviewJson() != null) {
                Map<String, Object> imageMap = (Map<String, Object>) harborImageTagVo.getScanOverviewJson().get("application/vnd.scanner.adapter.vuln.report.harbor+json; version=1.0");
                HarborImageTagVo.ScanOverview scanOverview;
                if (imageMap.get("summary") != null) {
                    String jsonString = gson.toJson(imageMap.get("summary"));
                    Map<String, Object> summaryMap = (Map<String, Object>) imageMap.get("summary");
                    scanOverview = gson.fromJson(jsonString, HarborImageTagVo.ScanOverview.class);
                    jsonString = gson.toJson(summaryMap.get("summary"));
                    HarborImageTagVo.Summary summary = gson.fromJson(jsonString, HarborImageTagVo.Summary.class);
                    scanOverview.setSummary(summary);
                } else {
                    scanOverview = harborImageTagVo.new ScanOverview();
                }
                scanOverview.setScanStatus(TypeUtil.objToString(imageMap.get("scan_status")));
                scanOverview.setSeverity(TypeUtil.objToString(imageMap.get("severity")));
                harborImageTagVo.setScanOverview(scanOverview);
            } else {
                harborImageTagVo.setScanOverview(null);
            }
            harborImageTagVo.setScanOverviewJson(null);
            harborImageTagVo.setExtraAttrs(null);
        }
        return harborImageTagVo;
    }

    public Boolean scannerAvailable(Long projectId) {
        if (HarborUtil.isApiVersion1(harborHttpClient.getHarborInfo())) {
            ResponseEntity<String> response = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_SYSTEM_INFO, null, null, true);
            Map<String, Object> resultMap = JSONObject.parseObject(response.getBody(), Map.class);
            return resultMap.get("with_clair") != null && (Boolean) resultMap.get("with_clair");
        } else {
            Long harborProjectId = harborRepositoryRepository.getHarborRepositoryById(projectId).getHarborId();
            ResponseEntity<String> response = harborHttpClient.exchange(HarborConstants.HarborApiEnum.IMAGE_QUERY_SCANNER_STATUS, null, null, true, harborProjectId);
            Map<String, Object> resultMap = JSONObject.parseObject(response.getBody(), Map.class);
            if (resultMap == null || resultMap.isEmpty()) {
                return false;
            } else {
                return resultMap.get("health").equals("healthy");
            }
        }
    }

    private String getSecurity(Double securityNum) {
        int num = securityNum.intValue();
        String security;
        switch (num) {
            case 1:
                security = HarborConstants.SeverityLevel.UNKNOWN;
                break;
            case 2:
                security = HarborConstants.SeverityLevel.NEGLIGIBLE;
                break;
            case 3:
                security = HarborConstants.SeverityLevel.LOW;
                break;
            case 4:
                security = HarborConstants.SeverityLevel.MEDIUM;
                break;
            case 5:
                security = HarborConstants.SeverityLevel.HIGH;
                break;
            case 6:
                security = HarborConstants.SeverityLevel.CRITICAL;
                break;
            default:
                security = HarborConstants.SeverityLevel.UNKNOWN;
        }
        return security;
    }

    private void setSecurity(Double securityNum, Double securityValue, HarborImageTagVo.Summary summary) {
        int num = securityNum.intValue();
        Long value = Math.round(securityValue);
        switch (num) {
            case 2:
                summary.setUnknown(value);
                break;
            case 3:
                summary.setLow(value);
                break;
            case 4:
                summary.setMedium(value);
                break;
            case 5:
                summary.setHigh(value);
                break;
            case 6:
                summary.setCritical(value);
                break;
        }
    }


}
