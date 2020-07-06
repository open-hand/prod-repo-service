package org.hrds.rdupm.harbor.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;

/**
 * 制品库-猪齿鱼Harbor仓库应用服务
 *
 * @author mofei.li@hand-china.com 2020/07/06 9:45
 */

public interface HarborC7nRepoService {
    /**
     * 根据仓库ID获取镜像列表
     *
     * @param projectId
     * @param repoId
     * @param appServiceId
     * @param imageName
     * @param pageRequest
     * @return
     */
    Page<HarborImageVo> getImagesByRepoId(Long projectId, Long repoId, Long appServiceId, String imageName, PageRequest pageRequest);

}
