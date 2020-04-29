package org.hrds.rdupm.harbor.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.domain.entity.HarborLog;

/**
 * 制品库-harbor日志表应用服务
 *
 * @author xiuhong.chen@hand-china.com 2020-04-29 14:54:57
 */
public interface HarborLogService {

	/***
	 * 查询权限操作日志列表
	 * @param pageRequest
	 * @param harborLog
	 * @return
	 */
	PageInfo<HarborLog> listAuthLog(PageRequest pageRequest, HarborLog harborLog);
}
