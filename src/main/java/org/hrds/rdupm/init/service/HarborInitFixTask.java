package org.hrds.rdupm.init.service;

import java.util.Map;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description:0.23.0数据迁移修复脚本，服务ID、组织ID、项目ID都为空时初始化进去
 *
 * @author chenxiuhong 2020/06/12 11:42 上午
 */
@Component
public class HarborInitFixTask {
	@Autowired
	private HarborInitService harborInitService;

	private static final Logger LOGGER = LoggerFactory.getLogger(HarborInitFixTask.class);


	@JobTask(maxRetryCount = 3,code = "fixHarborCustomRepo23.0",description = "Harbor-自定义数据修复-0.23.0")
	@TimedTask(name = "fixHarborCustomRepo23.0", description = "Harbor-自定义数据修复-0.23.0",
			oneExecution = true, repeatCount = 0, repeatInterval = 100, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
			params = {})
	public void initHarborCustomRepoNoAnyId(Map<String, Object> map) {
		harborInitService.initHarborCustomRepoNoAnyId();
	}


	@JobTask(maxRetryCount = 3,code = "fixHarborUserAuth23.0",description = "Harbor-用户权限修复-0.23.0")
	@TimedTask(name = "fixHarborUserAuth23.0", description = "Harbor-用户权限修复-0.23.0",
			oneExecution = true, repeatCount = 0, repeatInterval = 100, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
			params = {})
	public void fixHarborUserAuth(Map<String, Object> map) {
		harborInitService.fixHarborUserAuth();
	}

}
