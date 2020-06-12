package org.hrds.rdupm.init.service;

import java.util.Map;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/06/12 11:42 上午
 */
@Component
public class HarborInitTask {
	@Autowired
	private HarborInitService harborInitService;

	@JobTask(maxRetryCount = 3,code = "initHarborDefaultRepo",description = "Harbor-关联默认仓库")
	@TimedTask(name = "initHarborDefaultRepo", description = "Harbor-关联默认仓库",
			oneExecution = true, repeatCount = 0, repeatInterval = 100, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
			params = {})
	public void initHarborDefaultRepo(Map<String, Object> map) {
		harborInitService.defaultRepoInit();
	}

	@JobTask(maxRetryCount = 3,code = "initHarborCustomRepo",description = "Harbor-导入自定义仓库数据")
	@TimedTask(name = "initHarborCustomRepo", description = "Harbor-导入自定义仓库数据",
			oneExecution = true, repeatCount = 0, repeatInterval = 100, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
			params = {})
	public void initHarborCustomRepo(Map<String, Object> map) {
		harborInitService.customRepoInit();
	}
}
