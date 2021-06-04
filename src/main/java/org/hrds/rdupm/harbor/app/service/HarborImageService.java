package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import org.hrds.rdupm.harbor.api.vo.HarborImageScanResultVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageScanVO;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 3:07 下午
 */
public interface HarborImageService {

    /***
     * 项目层--获取镜像列表
     * @param projectId
     * @param imageName
     * @param pageRequest
     * @return
     */
    Page<HarborImageVo> getByProject(Long projectId, String imageName, PageRequest pageRequest);

    /***
     * 组织层--获取镜像列表
     * @param organizationId
     * @param projectCode
     * @param projectName
     * @param imageName
     * @param pageRequest
     * @return
     */
    Page<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest);

    /***
     * 删除镜像
     * @param harborImageVo
     */
    void delete(HarborImageVo harborImageVo);

    /***
     * 更新镜像描述
     * @param harborImageVo
     */
    void updateDesc(HarborImageVo harborImageVo);

    /***
     * 批量扫描镜像
     * @param imageScanVOList
     */
    void scanImages(List<HarborImageScanVO> imageScanVOList);

    /***
     * 批量获取扫描镜像结果
     * @param imageScanVO
     */
    Page<HarborImageScanResultVO> queryImageScanDetail(HarborImageScanVO imageScanVO, PageRequest pageRequest);

    /**
     * 获取单个扫描结果
     *
     * @param imageScanVO
     * @return
     */
    HarborImageTagVo queryImageScanDetail(HarborImageScanVO imageScanVO);

    /**
     * 判断是否有可用扫描器
     *
     * @param projectId
     * @return
     */
    Boolean scannerAvailable(Long projectId);

    List<HarborImageVo> getImageList(Long harborId, String imageName, PageRequest pageRequest, String projectCode);
}
