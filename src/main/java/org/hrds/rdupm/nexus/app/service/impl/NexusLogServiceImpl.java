package org.hrds.rdupm.nexus.app.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.nexus.app.service.NexusLogService;
import org.hrds.rdupm.nexus.domain.entity.NexusLog;
import org.hrds.rdupm.nexus.domain.repository.NexusLogRepository;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.mapper.NexusLogMapper;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
/**
 * 制品库_nexus日志表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@Service
public class NexusLogServiceImpl implements NexusLogService {

    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;
    @Autowired
    private NexusLogRepository nexusLogRepository;
    @Autowired
    private NexusLogMapper nexusLogMapper;

    @Override
    public Page<NexusLog> listLogByOrg(Long organizationId, String repoType, Long projectId, String neRepositoryName, String loginName, String operateType, Date startDate, Date endDate, PageRequest pageRequest) {
        List<NexusLog> nexusLogList = nexusLogMapper.listNpmLogByOrg(organizationId, repoType, projectId, neRepositoryName, loginName, operateType, startDate, endDate);

        Set<Long> projectIdSet = nexusLogList.stream().map(NexusLog::getProjectId).collect(Collectors.toSet());
        List<ProjectVO> projectVOList = baseServiceFeignClient.queryByIds(projectIdSet);
        Map<Long, List<ProjectVO>> projectDataMap = projectVOList.stream().collect(Collectors.groupingBy(ProjectVO::getId));
        for (NexusLog log : nexusLogList
             ) {
            List<ProjectVO> projectVOS = projectDataMap.get(log.getProjectId());
            if (CollectionUtils.isNotEmpty(projectVOS)) {
                ProjectVO projectVO = projectVOS.get(0);
                log.setProjectCode(projectVO.getCode());
                log.setProjectName(projectVO.getName());
                log.setProjectImageUrl(projectVO.getImageUrl());
            }
        }
        return PageConvertUtils.convert(pageRequest.getPage(),pageRequest.getSize(),nexusLogList);
    }
}
