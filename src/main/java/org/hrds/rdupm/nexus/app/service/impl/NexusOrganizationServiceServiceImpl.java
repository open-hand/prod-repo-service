package org.hrds.rdupm.nexus.app.service.impl;

import org.hrds.rdupm.nexus.app.service.NexusOrganizationServiceService;
import org.hrds.rdupm.nexus.infra.mapper.NexusOrganizationServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 制品库-租户与nexus服务关系表(NexusOrganizationService)应用服务
 *
 * @author hao.wang08@hand-china.com
 * @since 2022-03-01 10:17:12
 */
@Service
public class NexusOrganizationServiceServiceImpl implements NexusOrganizationServiceService {
    @Autowired
    private NexusOrganizationServiceMapper nexusOrganizationServiceMapper;


}

