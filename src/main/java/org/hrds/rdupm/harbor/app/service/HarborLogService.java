package org.hrds.rdupm.harbor.app.service;

import java.util.Date;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageLog;
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

	/***
	 * 查询镜像操作日志列表
	 * @param pageRequest
	 * @param projectId
	 * @param imageName
	 * @param loginName
	 * @param tagName
	 * @param operateType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	PageInfo<HarborImageLog> listImageLog(PageRequest pageRequest, Long projectId, String imageName, String loginName, String tagName, String operateType, Date startDate, Date endDate);
}
