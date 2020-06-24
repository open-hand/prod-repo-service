package org.hrds.rdupm.init.service;

import java.util.List;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;
import org.hrds.rdupm.harbor.domain.repository.HarborCustomRepoRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.init.config.HarborInitConfiguration;
import org.hrds.rdupm.init.dto.DevopsConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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

	@Autowired
	private HarborRepositoryRepository harborRepositoryRepository;
	@Autowired
	private HarborInitConfiguration harborInitConfiguration;
	@Autowired
	private HarborCustomRepoRepository harborCustomRepoRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(HarborInitFixTask.class);


	@JobTask(maxRetryCount = 3,code = "fixHarborCustomRepo23.0",description = "Harbor-自定义数据修复-0.23.0")
	@TimedTask(name = "fixHarborCustomRepo23.0", description = "Harbor-自定义数据修复-0.23.0",
			oneExecution = true, repeatCount = 0, repeatInterval = 100, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
			params = {})
	public void initHarborCustomRepoNoAnyId(Map<String, Object> map) {
		harborInitService.initHarborCustomRepoNoAnyId();
	}

	private JdbcTemplate getDefaultJdbcTemplate(){
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(harborInitConfiguration.getDefaultRepoUrl());
		mysqlDataSource.setUser(harborInitConfiguration.getDefaultRepoUsername());
		mysqlDataSource.setPassword(harborInitConfiguration.getDefaultRepoPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
		return jdbcTemplate;
	}

	private JdbcTemplate getCustomJdbcTemplate(){
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(harborInitConfiguration.getCustomRepoUrl());
		mysqlDataSource.setUser(harborInitConfiguration.getCustomRepoUsername());
		mysqlDataSource.setPassword(harborInitConfiguration.getCustomRepoPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
		return jdbcTemplate;
	}
}
