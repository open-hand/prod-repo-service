package org.hrds.rdupm.harbor.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;
import org.hrds.rdupm.harbor.app.service.HarborC7nRepoService;
import org.hrds.rdupm.harbor.app.service.HarborCustomRepoService;
import org.hrds.rdupm.harbor.app.service.HarborImageService;
import org.hrds.rdupm.harbor.app.service.HarborRepositoryService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Page<HarborImageVo> getImagesByRepoId(Long projectId, Long repoId, Long appServiceId, String imageName, PageRequest pageRequest) {
        return null;
    }
}
