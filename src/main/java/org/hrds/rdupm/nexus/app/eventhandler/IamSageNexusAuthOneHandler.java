package org.hrds.rdupm.nexus.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.api.vo.IamGroupMemberVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.sagahandler.IamSagaHandler;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusAuthSageService;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Transactional
    public String delete(String payload) {
        NexusRepository nexusRepository = JSONObject.parseObject(payload, NexusRepository.class);
        nexusAuthSageService.handlerRepoAuth(nexusRepository);
        return payload;
    }
}
