package org.hrds.rdupm.nexus.app.service;

import java.util.Date;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;

/**
 * 制品库_nexus日志表应用服务
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusLogService {
    /**
     * 组织层-操作日志列表
     * @param organizationId
     * @param repoType
     * @param projectId
     * @param neRepositoryName
     * @param realName
     * @param operateType
     * @param startDate
     * @param endDate
     * @param pageRequest
     * @return Page<NexusLog>
     */
    Page<NexusLog> listLog(Long organizationId, String repoType, Long projectId,
                           String neRepositoryName, String realName, String operateType,
                           Date startDate, Date endDate, Long repositoryId, PageRequest pageRequest);

}
