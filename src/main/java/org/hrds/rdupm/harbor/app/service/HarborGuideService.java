package org.hrds.rdupm.harbor.app.service;

import org.hrds.rdupm.harbor.api.vo.HarborGuide;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 2:41 下午
 */
public interface HarborGuideService {
	/***
	 * 项目层-获取配置指引
	 * @param projectId
	 * @return
	 */
	HarborGuide getByProject(Long projectId);
}
