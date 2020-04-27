package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;

/**
 * 制品库-harbor权限表应用服务
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
public interface HarborAuthService {

	/***
	 * 保存用户权限
	 * @param projectId
	 * @param dtoList
	 */
	void save(Long projectId,List<HarborAuth> dtoList);

	/***
	 * 更新权限
	 * @param harborAuth
	 */
	void update(HarborAuth harborAuth);

	/***
	 * 查询权限列表
	 * @param pageRequest
	 * @param harborAuth
	 * @return
	 */
	PageInfo<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth);

	/***
	 * 删除权限
	 * @param harborAuth
	 */
	void delete(HarborAuth harborAuth);
}
