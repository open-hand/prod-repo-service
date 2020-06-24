package org.hrds.rdupm.nexus.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusAuthSageService;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除用户角色，同步删除制品用户权限
 *
 * @author weisen.yang@hand-china.com
 */
@Component
public class IamSageNexusAuthOneHandler {
    @Autowired
    private NexusAuthSageService nexusAuthSageService;

    @SagaTask(code = NexusSagaConstants.NexusAuthDeleteUserHandle.NEXUS_AUTH_DELETE_USER_HANDLE_AUTH, description = "制品库删除权限同步事件-nexus(maven与npm)", sagaCode = NexusSagaConstants.NexusAuthDeleteUserHandle.NEXUS_AUTH_DELETE_USER_HANDLE, maxRetryCount = 3, seq = 1)
    public String delete(String payload) {
        NexusRepository nexusRepository = JSONObject.parseObject(payload, NexusRepository.class);
        nexusAuthSageService.handlerRepoAuth(nexusRepository);
        return payload;
    }
}
