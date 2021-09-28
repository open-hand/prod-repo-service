package org.hrds.rdupm.nexus.app.job;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;

/**
 * Created by wangxiang on 2021/9/26
 */
@Component
public class NexusCapacityStatistics {
    private static final Logger LOGGER = LoggerFactory.getLogger(NexusCapacityStatistics.class);

    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;

    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;


    @JobTask(maxRetryCount = 3,
            code = "nexusProjectCapacityStatistics",
            description = "nexus项目容量统计",
            params = {@JobParam(name = "choerodonProjectId", description = "猪齿鱼项目id")})
    public void nexusProjectCapacityStatistics(Map<String, Object> param) {
        // <> 获取组织
        long choerodonProjectId = 0L;
        NexusServerConfig nexusServerConfig = new NexusServerConfig();
        nexusServerConfig.setDefaultFlag(BaseConstants.Digital.ONE);
        //查询组织下所有默认服务的仓库
        NexusServerConfig serverConfig = nexusServerConfigMapper.selectOne(nexusServerConfig);
        if (serverConfig == null) {
            return;
        }

        if (param.containsKey("choerodonProjectId") && Objects.nonNull(param.get("choerodonProjectId"))) {
            choerodonProjectId = Long.parseLong(param.get("choerodonProjectId").toString());
        }
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setConfigId(serverConfig.getConfigId());
        nexusRepository.setProjectId(choerodonProjectId);
        List<NexusRepository> nexusRepositoryList = nexusRepositoryMapper.select(nexusRepository);
        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            LOGGER.info("项目{}下无nexus仓库", choerodonProjectId);
            return;
        }
        nexusRepositoryList.forEach(nexusRepository1 -> {
            // TODO: 2021/9/26
        });

    }

}
