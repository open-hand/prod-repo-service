package org.hrds.rdupm.nexus.app.job;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.enums.TriggerTypeEnum;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 制品库_nexus权限 定时任务
 * @author weisen.yang@hand-china.com 2020-05-26
 */
@Component
public class ExpiredNexusAuthJob {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredNexusAuthJob.class);

    @Autowired
    private NexusAuthService nexusAuthService;

    @JobTask(maxRetryCount = 3, code = "expiredNexusAuth", description = "Nexus移除过期权限")
    @TimedTask(name = "expiredNexusAuth", description = "Nexus移除过期权限",
            triggerType = TriggerTypeEnum.CRON_TRIGGER,
            cronExpression = "0 0 0/1 * * ?",
            params = {})
    public void expiredNexusAuth(Map<String, Object> map) {
        logger.info("Nexus移除过期权限，定时任务开始执行");
        nexusAuthService.expiredBatchNexusAuth();
        logger.info("Nexus移除过期权限，定时任务执行完毕");
    }

}
