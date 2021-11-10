package org.hrds.rdupm.common.app.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.app.service.ResourceService;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.nexus.api.vo.ResourceVO;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 2021/11/9
 */
@Component
public class ResourceServiceImpl implements ResourceService {

    private static final Long ONE_GB_TO_B = 1073741824L;

    @Autowired
    private HarborQuotaService harborQuotaService;

    @Autowired
    private HarborRepositoryMapper harborRepositoryMapper;

    @Autowired
    private HarborRepository harborRepository;

    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;


    @Override
    public List<ResourceVO> listResourceByIds(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.EMPTY_LIST;
        }
        List<ResourceVO> result = new ArrayList<>();
        projectIds.forEach(projectId -> {
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setProjectId(projectId);
            //根据项目id查询harbor的使用量
            HarborRepository harborRecord = new HarborRepository();
            harborRecord.setProjectId(projectId);
            HarborRepository repository = harborRepositoryMapper.selectOne(harborRecord);
            if (repository == null) {
                return;
            }
            //获取存储容量
            DecimalFormat df = new DecimalFormat("#.00");
            HarborQuotaVo harborQuotaVo = harborQuotaService.getProjectQuota(harborRepository.getProjectId());
            if (harborQuotaVo != null) {
                if (harborQuotaVo.getUsedStorage() < ONE_GB_TO_B) {
                    resourceVO.setCurrentHarborCapacity(df.format(Math.pow(harborQuotaVo.getUsedStorage(), 1.0 / 2.0)) + "MB");

                } else if (harborQuotaVo.getUsedStorage() >= ONE_GB_TO_B) {
                    resourceVO.setCurrentHarborCapacity(df.format(Math.pow(harborQuotaVo.getUsedStorage(), 1.0 / 3.0) + "GB"));
                }
            }
            NexusAssets assetRecord = new NexusAssets();
            assetRecord.setProjectId(projectId);
            List<NexusAssets> nexusAssets = nexusAssetsMapper.select(assetRecord);
            if (!CollectionUtils.isEmpty(nexusAssets)) {
                long count = nexusAssets.stream().map(NexusAssets::getSize).count();
                if (count < ONE_GB_TO_B) {
                    resourceVO.setCurrentNexusCapacity(df.format(Math.pow(count, 1.0 / 2.0)) + "MB");
                } else if (count >= ONE_GB_TO_B) {
                    resourceVO.setCurrentNexusCapacity(df.format(Math.pow(count, 1.0 / 3.0) + "GB"));
                }
            }
            result.add(resourceVO);

        });
        return result;
    }
}
