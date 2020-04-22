package org.hrds.rdupm.nexus.app.service;

import java.util.List;

/**
 * 制品库_nexus仓库 初始化
 * @author weisen.yang@hand-china.com 2020/4/7
 */
public interface NexusInitService {
	/**
	 * 脚本初始化与更新
	 */
	void initScript();

	/**
	 * 匿名用户-拉取权限初始化：默认给予所有仓库拉取权限(参数未传时)
	 * @param repositoryNames 仓库名称
	 */
	void initAnonymous(List<String> repositoryNames);
}
