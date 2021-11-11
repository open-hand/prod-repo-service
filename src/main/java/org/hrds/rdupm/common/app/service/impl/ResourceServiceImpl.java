package org.hrds.rdupm.common.app.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.app.service.ResourceService;
import org.hrds.rdupm.harbor.api.vo.HarborQuotaVo;
import org.hrds.rdupm.harbor.app.service.HarborQuotaService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.mapper.HarborRepositoryMapper;
import org.hrds.rdupm.nexus.api.vo.ResourceVO;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hzero.core.base.BaseConstants;
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
    private NexusAssetsMapper nexusAssetsMapper;

    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;

    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;


    @Override
    public List<ResourceVO> listResourceByIds(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.EMPTY_LIST;
        }
        List<ResourceVO> result = new ArrayList<>();
        projectIds.forEach(projectId -> {
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setProjectId(projectId);
            resourceVO.setCurrentNexusCapacity(String.valueOf(0));
            resourceVO.setCurrentHarborCapacity(String.valueOf(0));
            //根据项目id查询harbor的使用量
            HarborRepository harborRecord = new HarborRepository();
            harborRecord.setProjectId(projectId);
            HarborRepository repository = harborRepositoryMapper.selectOne(harborRecord);
            if (repository == null) {
                return;
            }
            //获取存储容量
            HarborQuotaVo harborQuotaVo = harborQuotaService.getProjectQuota(repository.getProjectId());
            if (harborQuotaVo != null) {
                if (harborQuotaVo.getUsedStorage() == 0) {
                    resourceVO.setCurrentHarborCapacity(String.valueOf(0));
                } else if (harborQuotaVo.getUsedStorage() < ONE_GB_TO_B && harborQuotaVo.getUsedStorage() > 0) {
                    resourceVO.setCurrentHarborCapacity(String.format("%.2f", harborQuotaVo.getUsedStorage() / new BigDecimal(1024).pow(2).doubleValue()) + "MB");
                } else if (harborQuotaVo.getUsedStorage() >= ONE_GB_TO_B) {
                    resourceVO.setCurrentHarborCapacity(String.format("%.2f", harborQuotaVo.getUsedStorage() / new BigDecimal(1024).pow(3).doubleValue()) + "GB");
                }
            }
            //查询默认的仓库配置
            NexusServerConfig nexusServerConfig = new NexusServerConfig();
            nexusServerConfig.setDefaultFlag(BaseConstants.Flag.YES);
            nexusServerConfig.setEnableFlag(BaseConstants.Flag.YES);
            NexusServerConfig serverConfig = nexusServerConfigMapper.selectOne(nexusServerConfig);
            if (serverConfig != null) {
                NexusRepository nexusRepository = new NexusRepository();
                nexusRepository.setProjectId(projectId);
                nexusRepository.setConfigId(serverConfig.getConfigId());
                List<NexusRepository> nexusRepositories = nexusRepositoryMapper.select(nexusRepository);
                if (!CollectionUtils.isEmpty(nexusRepositories)) {
                    List<Long> repositoryIds = nexusRepositories.stream().map(NexusRepository::getRepositoryId).collect(Collectors.toList());
                    List<NexusAssets> nexusAssets = nexusAssetsMapper.selectAssetsByRepositoryIds(repositoryIds);
                    if (!CollectionUtils.isEmpty(nexusAssets)) {
                        long count = nexusAssets.stream().map(NexusAssets::getSize).reduce((aLong, aLong2) -> aLong + aLong2).get();
                        if (count == 0) {
                            resourceVO.setCurrentNexusCapacity(String.valueOf(0));
                        } else if (count < ONE_GB_TO_B && count > 0) {
                            resourceVO.setCurrentNexusCapacity(String.format("%.2f", count / new BigDecimal(1024).pow(2).doubleValue()) + "MB");
                        } else if (count >= ONE_GB_TO_B) {
                            resourceVO.setCurrentNexusCapacity(String.format("%.2f", count / new BigDecimal(1024).pow(3).longValue()) + "GB");
                        }
                    } else {
                        resourceVO.setCurrentNexusCapacity(String.valueOf(0));
                    }
                }
            }
            result.add(resourceVO);

        });
        return result;
    }
}
