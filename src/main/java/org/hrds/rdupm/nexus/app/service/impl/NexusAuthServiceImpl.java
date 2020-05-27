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
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.BaseFeignClient;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.nexus.api.dto.NexusRepositoryDTO;
import org.hrds.rdupm.nexus.app.eventhandler.constants.NexusSagaConstants;
import org.hrds.rdupm.nexus.app.service.NexusAuthService;
import org.hrds.rdupm.nexus.app.service.NexusServerConfigService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRole;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRoleRepository;
import org.hrds.rdupm.nexus.infra.annotation.NexusOperateLog;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.vo.ProjectVO;
import org.hrds.rdupm.nexus.infra.mapper.NexusAuthMapper;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
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
    private C7nBaseService c7nBaseService;

    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;
    @Autowired
    private NexusAuthRepository nexusAuthRepository;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private ProdUserRepository prodUserRepository;
    @Autowired
    private ProdUserService prodUserService;
    @Autowired
    private NexusRoleRepository nexusRoleRepository;
    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusServerConfigService configService;


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

        // 项目名称查询
        Set<Long> projectIdSet = dataList.stream().map(NexusAuth::getProjectId).collect(Collectors.toSet());
        Map<Long, ProjectDTO> projectDtoMap = c7nBaseService.queryProjectByIds(projectIdSet);

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

            // 项目名
            ProjectDTO projectDTO = projectDtoMap.get(nexusAuth.getProjectId());
            if (projectDTO != null) {
                nexusAuth.setProjectName(projectDTO.getName());
            }
        });

        return authPage;
    }

    @Override
    @ExcelExport(NexusAuth.class)
    public Page<NexusAuth> export(PageRequest pageRequest, NexusAuth nexusAuth, ExportParam exportParam, HttpServletResponse response) {
        return this.pageList(pageRequest, nexusAuth);
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_CREATE, content = "%s 分配 %s 【%s】仓库的权限角色为 【%s】,过期日期为【%s】")
    @Saga(code = NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE, description = "nexus分配权限", inputSchemaClass = List.class)
    @Transactional(rollbackFor = Exception.class)
    public void create(Long projectId, List<NexusAuth> nexusAuthList) {
        if (CollectionUtils.isEmpty(nexusAuthList)){
            return;
        }
        List<Long> repositoryIds = nexusAuthList.stream().map(NexusAuth::getRepositoryId).distinct().collect(Collectors.toList());
        if (repositoryIds.size() > 1) {
            throw new CommonException(NexusMessageConstants.NEXUS_AUTH_REPOSITORY_ID_IS_NOT_UNIQUE);
        }
        Long repositoryId = repositoryIds.get(0);
        NexusRepository nexusRepository = nexusRepositoryRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId).stream().findFirst().orElse(null);
        if (nexusRepository == null){
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        // 仓库角色查询
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, repositoryId).stream().findFirst().orElse(null);

        //校验是否已分配权限
        List<NexusAuth> existList = nexusAuthRepository.select(NexusAuth.FIELD_REPOSITORY_ID, repositoryId);
        Map<Long, NexusAuth> nexusAuthMap = existList.stream().collect(Collectors.toMap(NexusAuth::getUserId, dto->dto));

        //设置loginName、realName
        ResponseEntity<List<UserDTO>> userDtoResponseEntity = baseFeignClient.listUsersByIds(nexusAuthList.stream().map(NexusAuth::getUserId).distinct().toArray(Long[]::new),true);
        Map<Long,UserDTO> userDtoMap = userDtoResponseEntity == null ? new HashMap<>(2) : Objects.requireNonNull(userDtoResponseEntity.getBody()).stream().collect(Collectors.toMap(UserDTO::getId, dto->dto));

        List<ProdUser> prodUserList = new ArrayList<>();

        nexusAuthList.forEach(nexusAuth -> {
            UserDTO userDTO = userDtoMap.get(nexusAuth.getUserId());
            nexusAuth.setLoginName(userDTO == null ? null : userDTO.getLoginName());
            nexusAuth.setRealName(userDTO == null ? null : userDTO.getRealName());

            if (nexusAuth.getLoginName() == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }
            if (nexusAuthMap.get(nexusAuth.getUserId()) != null) {
                throw new CommonException(NexusMessageConstants.NEXUS_AUTH_ALREADY_EXIST, nexusAuth.getRealName());
            }

            nexusAuth.setProjectId(projectId);
            nexusAuth.setOrganizationId(nexusRepository.getOrganizationId());
            // 设置角色
            nexusAuth.setNeRoleIdByRoleCode(nexusRole);

            String password = RandomStringUtils.randomAlphanumeric(BaseConstants.Digital.EIGHT);
            ProdUser prodUser = new ProdUser(nexusAuth.getUserId(), nexusAuth.getLoginName(), password,0);
            prodUserList.add(prodUser);
        });


        producer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(NexusSagaConstants.NexusAuthCreate.NEXUS_AUTH_CREATE)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("createMavenAuth")
                        .withSourceId(projectId),
                startSagaBuilder -> {
                    nexusAuthRepository.batchInsert(nexusAuthList);
                    prodUserService.saveMultiUser(prodUserList);
                    startSagaBuilder.withPayloadAndSerialize(nexusAuthList).withSourceId(projectId);
                });
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_CREATE, content = "%s 更新 %s 【%s】仓库的权限角色为 【%s】,过期日期为【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void update(NexusAuth nexusAuth) {
        // 设置并返回当前nexus服务信息
        configService.setNexusInfo(nexusClient);

        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if (existAuth == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 仓库角色查询
        NexusRole nexusRole = nexusRoleRepository.select(NexusRole.FIELD_REPOSITORY_ID, existAuth.getRepositoryId()).stream().findFirst().orElse(null);
        nexusAuth.setNeRoleIdByRoleCode(nexusRole);
        nexusAuthRepository.updateOptional(nexusAuth, NexusAuth.FIELD_ROLE_CODE, NexusAuth.FIELD_END_DATE, NexusAuth.FIELD_NE_ROLE_ID);

        List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
        if (CollectionUtils.isNotEmpty(existUserList)) {
            // 更新用户
            NexusServerUser nexusServerUser = existUserList.get(0);
            // 添加新角色
            nexusServerUser.getRoles().add(nexusAuth.getNeRoleId());
            // 删除旧角色
            nexusServerUser.getRoles().remove(existAuth.getNeRoleId());
            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        } else {
            throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_EXIST);
        }

        nexusClient.removeNexusServerInfo();
    }

    @Override
    @NexusOperateLog(operateType = NexusConstants.LogOperateType.AUTH_CREATE, content = "%s 删除 %s 【%s】仓库的的权限角色 【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void delete(NexusAuth nexusAuth) {
        // 设置并返回当前nexus服务信息
        configService.setNexusInfo(nexusClient);

        NexusAuth existAuth = nexusAuthRepository.selectByPrimaryKey(nexusAuth);
        if(existAuth == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        nexusAuthRepository.deleteByPrimaryKey(nexusAuth);


        List<NexusServerUser> existUserList = nexusClient.getNexusUserApi().getUsers(nexusAuth.getLoginName());
        if (CollectionUtils.isNotEmpty(existUserList)) {
            // 更新用户
            NexusServerUser nexusServerUser = existUserList.get(0);
            // 删除旧角色
            nexusServerUser.getRoles().remove(existAuth.getNeRoleId());
            nexusClient.getNexusUserApi().updateUser(nexusServerUser);
        } else {
            throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_EXIST);
        }
        nexusClient.removeNexusServerInfo();
    }

}
