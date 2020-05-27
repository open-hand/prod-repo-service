package org.hrds.rdupm.nexus.app.service.impl;

import com.google.common.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.mapper.NexusAuthMapper;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.RollbackException;
import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 制品库_nexus权限表应用服务默认实现
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@Service
public class NexusAuthServiceImpl implements NexusAuthService {

    @Autowired
    private NexusAuthMapper nexusAuthMapper;

    @Autowired
    private BaseFeignClient baseFeignClient;
    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private TransactionalProducer producer;


    @Override
    public Page<NexusAuth> pageList(PageRequest pageRequest, NexusAuth nexusAuth) {
        Page<NexusAuth> authPage = PageHelper.doPageAndSort(pageRequest, () -> nexusAuthMapper.list(nexusAuth));
        List<NexusAuth> dataList = authPage.getContent();
        if(CollectionUtils.isEmpty(dataList)){
            return authPage;
        }

        //查询成员角色
        Map<Long,List<NexusAuth>> dataListMap = dataList.stream().collect(Collectors.groupingBy(NexusAuth::getProjectId));
        Map<Long, UserWithGitlabIdDTO> userDtoMap = new HashMap<>(16);
        for(Map.Entry<Long,List<NexusAuth>> entry : dataListMap.entrySet()){
            Long projectId = entry.getKey();
            List<NexusAuth> list = entry.getValue();
            Set<Long> userIdSet = list.stream().map(NexusAuth::getUserId).collect(Collectors.toSet());
            ResponseEntity<List<UserWithGitlabIdDTO>> responseEntity = baseFeignClient.listUsersWithRolesAndGitlabUserIdByIds(projectId,userIdSet);
            userDtoMap.putAll(Objects.requireNonNull(responseEntity.getBody()).stream().collect(Collectors.toMap(UserWithGitlabIdDTO::getId, dto->dto)));
        }

        dataList.forEach(auth->{
            UserWithGitlabIdDTO userDto = userDtoMap.get(auth.getUserId());
            if(userDto != null){
                auth.setRealName(userDto.getRealName());
                auth.setUserImageUrl(userDto.getImageUrl());

                List<RoleDTO> roleDTOList = userDto.getRoles();
                if(CollectionUtils.isNotEmpty(roleDTOList)){
                    StringBuilder memberRole = new StringBuilder();
                    for(RoleDTO roleDTO : roleDTOList){
                        memberRole.append(roleDTO.getName()).append(" ");
                    }
                    auth.setMemberRole(memberRole.toString());
                }
            }
        });
        return authPage;
    }

    @Override
    // TODO 日志
    // @OperateLog(operateType = HarborConstants.ASSIGN_AUTH,content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
    @Saga(code = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE, description = "nexus分配权限", inputSchemaClass = List.class)
    public void create(Long projectId, List<NexusAuth> nexusAuthList) {
        if(CollectionUtils.isEmpty(nexusAuthList)){
            return;
        }
        List<Long> repositoryIds = nexusAuthList.stream().map(NexusAuth::getRepositoryId).collect(Collectors.toList());
        if (repositoryIds.size() > 1) {
            throw new CommonException(NexusMessageConstants.NEXUS_AUTH_REPOSITORY_ID_IS_NOT_UNIQUE);
        }
        Long repositoryId = repositoryIds.get(0);
        NexusRepository nexusRepository = nexusRepositoryRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId).stream().findFirst().orElse(null);
        if(nexusRepository == null){
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        //校验是否已分配权限
        List<NexusAuth> existList = nexusAuthRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId);
        Map<Long, NexusAuth> nexusAuthMap = existList.stream().collect(Collectors.toMap(NexusAuth::getUserId, dto->dto));

        //设置loginName、realName
        ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(nexusAuthList.stream().map(NexusAuth::getUserId).distinct().toArray(Long[]::new),true);
        Map<Long,UserDTO> userDtoMap = userDtoResponseEntity == null ? new HashMap<>(2) : Objects.requireNonNull(userDtoResponseEntity.getBody()).stream().collect(Collectors.toMap(UserDTO::getId, dto->dto));

        nexusAuthList.forEach(nexusAuth -> {
            UserDTO userDTO = userDtoMap.get(nexusAuth.getUserId());
            nexusAuth.setLoginName(userDTO == null ? null : userDTO.getLoginName());
            nexusAuth.setRealName(userDTO == null ? null : userDTO.getRealName());

            if (nexusAuth.getLoginName() == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }
            if(nexusAuthMap.get(nexusAuth.getUserId()) != null) {
                throw new CommonException(NexusMessageConstants.NEXUS_AUTH_ALREADY_EXIST, nexusAuth.getRealName());
            }

            nexusAuth.setProjectId(projectId);
            nexusAuth.setOrganizationId(nexusRepository.getOrganizationId());
            // TODO neRoleId
            nexusAuth.setNeRoleId(nexusAuth.getLoginName() + "-role");

        });
        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("createMavenAuth")
                        .withSourceId(projectId),
                startSagaBuilder -> {
                    nexusAuthRepository.batchInsert(nexusAuthList);
                    startSagaBuilder.withPayloadAndSerialize(nexusAuthList).withSourceId(projectId);
                });
    }

    @Override
    // TODO 日志
    // @OperateLog(operateType = HarborConstants.UPDATE_AUTH,content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void update(NexusAuth nexusAuth) {
        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if(existAuth == null){
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        nexusAuthRepository.updateOptional(nexusAuth, NexusAuth.FIELD_ROLE_CODE, NexusAuth.FIELD_END_DATE);
        // TODO nexus更新
    }

    @Override
    // TODO 日志
    // @OperateLog(operateType = HarborConstants.UPDATE_AUTH,content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void delete(NexusAuth nexusAuth) {
        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if(existAuth == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        nexusAuthRepository.deleteByPrimaryKey(nexusAuth);
        // TODO nexus删除
    }
}
