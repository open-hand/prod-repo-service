package org.hrds.rdupm.common.app.service;

import java.util.List;

import java.util.Map;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;

/**
 * 制品库-制品用户表应用服务
 *
 * @author xiuhong.chen@hand-china.com 2020-05-21 15:47:14
 */
public interface ProdUserService {

	/***
	 * 保存用户
	 * @param prodUserList
	 */
	void saveMultiUser(List<ProdUser> prodUserList);

	/***
	 * 保存一个用户
	 * @param prodUser
	 */
	ProdUser saveOneUser(ProdUser prodUser);

	/***
	 * 更新密码
	 * @param prodUser
	 */
	void updatePwd(ProdUser prodUser);

	Map<String, Map<Long, List<String>>> getUserRoleList(List<NexusRepository> nexusRepositories, Long projectId);
}
