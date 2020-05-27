package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.export.vo.ExportParam;

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
	Page<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth);

	/***
	 * 删除权限
	 * @param harborAuth
	 */
	void delete(HarborAuth harborAuth);

	/***
	 * 导出权限
	 * @param pageRequest
	 * @param harborAuth
	 * @param exportParam
	 * @param response
	 * @return
	 */
	Page<HarborAuth> export(PageRequest pageRequest, HarborAuth harborAuth, ExportParam exportParam, HttpServletResponse response);

	/***
	 * 保存制品库创建者的权限
	 * @param projectId
	 * @param organizationId
	 * @param harborId
	 * @param dtoList
	 */
	void saveOwnerAuth(Long projectId, Long organizationId, Integer harborId, List<HarborAuth> dtoList);
}
