package org.hrds.rdupm.harbor.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.harbor.api.vo.HarborAuthVo;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.app.service.HarborAuthService;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.User;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.infra.annotation.OperateLog;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.RoleDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserWithGitlabIdDTO;
import org.hrds.rdupm.harbor.infra.mapper.HarborAuthMapper;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 制品库-harbor权限表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
@Service
public class HarborAuthServiceImpl implements HarborAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HarborAuthServiceImpl.class);

    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private HarborHttpClient harborHttpClient;

    @Autowired
    private HarborAuthRepository repository;

    @Resource
    private HarborAuthMapper harborAuthMapper;

    @Resource
    private TransactionalProducer transactionalProducer;

    @Autowired
    private ProdUserService prodUserService;

    @Autowired
    private HarborInfoConfiguration harborInfoConfiguration;

    @Override
    @OperateLog(operateType = HarborConstants.ASSIGN_AUTH, content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
    @Saga(code = HarborConstants.HarborSagaCode.CREATE_AUTH, description = "分配权限", inputSchemaClass = List.class)
    public void save(Long projectId, List<HarborAuth> dtoList) {
        checkProjectAdmin(projectId);
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new CommonException("error.harbor.auth.param.empty");
        }
        HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(projectId);
        if (harborRepository == null) {
            throw new CommonException("error.harbor.project.not.exist");
        }

        //校验是否已分配权限
        List<HarborAuth> existList = repository.select(HarborAuth.FIELD_PROJECT_ID, projectId);
        Map<Long, HarborAuth> harborAuthMap = CollectionUtils.isEmpty(existList) ? new HashMap<>(1) : existList.stream().collect(Collectors.toMap(HarborAuth::getUserId, dto -> dto));

        //设置权限信息
        Set<Long> userIdSet = dtoList.stream().map(dto -> dto.getUserId()).collect(Collectors.toSet());
        Map<Long, UserDTO> userDtoMap = c7nBaseService.listUsersByIds(userIdSet);
        dtoList.forEach(dto -> {
            UserDTO userDTO = userDtoMap.get(dto.getUserId());
            dto.setLoginName(userDTO == null ? null : userDTO.getLoginName());
            dto.setRealName(userDTO == null ? null : userDTO.getRealName());
            if (harborAuthMap.get(dto.getUserId()) != null) {
                throw new CommonException("error.harbor.auth.already.exist");
            }

            dto.setProjectId(projectId);
            dto.setOrganizationId(harborRepository.getOrganizationId());
            dto.setHarborId(harborRepository.getHarborId());
            dto.setHarborRoleValue(dto.getHarborRoleValue());
            dto.setHarborAuthId(-1L);
        });

        transactionalProducer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(HarborConstants.HarborSagaCode.CREATE_AUTH)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("dockerRepo")
                        .withSourceId(projectId),
                startSagaBuilder -> {

                    //保存到数据库
                    Long harborId = dtoList.get(0).getHarborId();
                    ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH, null, null, false, harborId);
                    List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborAuthVo>>() {
                    }.getType());
                    Map<String, HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName, dto -> dto));
                    dtoList.stream().forEach(dto -> {
                        if (harborAuthVoMap.get(dto.getLoginName()) != null) {
                            throw new CommonException("error.harbor.auth.find.harborAuthId");
                        }
                    });
                    repository.batchInsert(dtoList);

                    startSagaBuilder.withPayloadAndSerialize(dtoList).withSourceId(projectId);
                });
    }

    @Override
    @OperateLog(operateType = HarborConstants.UPDATE_AUTH, content = "%s 更新 %s 权限角色为 【%s】,过期日期为【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void update(HarborAuth harborAuth) {
        checkProjectAdmin(harborAuth.getProjectId());
        HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(harborAuth.getProjectId());
        if (harborRepository == null) {
            throw new CommonException("error.harbor.project.not.exist");
        }
        if (HarborConstants.Y.equals(harborAuth.getLocked())) {
            throw new CommonException("error.harbor.auth.owner.not.update");
        }
        checkLastProjectAdmin(harborAuth, HarborConstants.UPDATE_AUTH);
        processHarborAuthId(harborAuth);
        Long harborId = harborRepository.getHarborId();
        harborAuth.setHarborRoleId(HarborConstants.HarborRoleEnum.getIdByValue(harborAuth.getHarborRoleValue()));
        repository.updateByPrimaryKey(harborAuth);

        Map<String, Object> bodyMap = new HashMap<>(2);
        bodyMap.put("role_id", harborAuth.getHarborRoleId());
        harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_ONE_AUTH, null, bodyMap, true, harborId, harborAuth.getHarborAuthId());
    }

    @Override
    public Page<HarborAuth> pageList(PageRequest pageRequest, HarborAuth harborAuth) {
        Page<HarborAuth> page = PageHelper.doPageAndSort(pageRequest, () -> harborAuthMapper.list(harborAuth));
        List<HarborAuth> dataList = page.getContent();
        if (CollectionUtils.isEmpty(dataList)) {
            return page;
        }

        //分项目查询成员角色
        Map<Long, List<HarborAuth>> dataListMap = dataList.stream().collect(Collectors.groupingBy(HarborAuth::getProjectId));
        Map<Long, UserWithGitlabIdDTO> userDtoMap = new HashMap<>(16);
        for (Map.Entry<Long, List<HarborAuth>> entry : dataListMap.entrySet()) {
            Long projectId = entry.getKey();
            List<HarborAuth> list = entry.getValue();
            Set<Long> userIdSet = list.stream().map(dto -> dto.getUserId()).collect(Collectors.toSet());
            Map<Long, UserWithGitlabIdDTO> map = c7nBaseService.listUsersWithRolesAndGitlabUserIdByIds(projectId, userIdSet);
            userDtoMap.putAll(map);
        }

        dataList.forEach(dto -> {
            dto.setHarborRoleMeaning(HarborConstants.HarborRoleEnum.getNameById(dto.getHarborRoleId()));
            dto.setHarborRoleValueById(dto.getHarborRoleId());
            UserWithGitlabIdDTO userDto = userDtoMap.get(dto.getUserId());
            if (userDto != null) {
                dto.setRealName(userDto.getRealName());
                dto.setUserImageUrl(userDto.getImageUrl());

                List<RoleDTO> roleDTOList = userDto.getRoles();
                if (CollectionUtils.isNotEmpty(roleDTOList)) {
                    StringBuffer memberRole = new StringBuffer();
                    for (RoleDTO roleDTO : roleDTOList) {
                        memberRole.append(roleDTO.getName()).append(" ");
                    }
                    dto.setMemberRole(memberRole.toString());
                }
            }
        });

        return page;
    }

    @Override
    @OperateLog(operateType = HarborConstants.REVOKE_AUTH, content = "%s 删除 %s 的权限角色 【%s】")
    @Transactional(rollbackFor = Exception.class)
    public void delete(HarborAuth harborAuth) {
        checkProjectAdmin(harborAuth.getProjectId());
        HarborRepository harborRepository = harborRepositoryRepository.getHarborRepositoryById(harborAuth.getProjectId());
        if (harborRepository == null) {
            throw new CommonException("error.harbor.project.not.exist");
        }
        if (HarborConstants.Y.equals(harborAuth.getLocked())) {
            throw new CommonException("error.harbor.auth.owner.not.delete");
        }
        checkLastProjectAdmin(harborAuth, HarborConstants.REVOKE_AUTH);

        processHarborAuthId(harborAuth);
        Long harborId = harborRepository.getHarborId();
        repository.deleteByPrimaryKey(harborAuth);
        if (harborAuth.getHarborAuthId() != -1) {
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.DELETE_ONE_AUTH, null, null, false, harborId, harborAuth.getHarborAuthId());
        }
    }

    private void processHarborAuthId(HarborAuth harborAuth) {
        if (harborAuth.getHarborAuthId() == null || harborAuth.getHarborAuthId().intValue() == -1) {
            HarborAuth dbAuth = repository.selectByCondition(Condition.builder(HarborAuth.class).where(Sqls.custom()
                    .andEqualTo(HarborAuth.FIELD_ORGANIZATION_ID, DetailsHelper.getUserDetails().getTenantId())
                    .andEqualTo(HarborAuth.FIELD_PROJECT_ID, harborAuth.getProjectId())
                    .andEqualTo(HarborAuth.FIELD_AUTH_ID, harborAuth.getAuthId())
            ).build()).stream().findFirst().orElse(null);
            if (!Objects.isNull(dbAuth)) {
                harborAuth.setObjectVersionNumber(dbAuth.getObjectVersionNumber());
                harborAuth.setHarborAuthId(dbAuth.getHarborAuthId());
            }
        }
    }

    @Override
    @ExcelExport(HarborAuth.class)
    public Page<HarborAuth> export(PageRequest pageRequest, HarborAuth harborAuth, ExportParam exportParam, HttpServletResponse response) {
        Page<HarborAuth> page = this.pageList(pageRequest, harborAuth);
        return page;
    }

    @Override
    @OperateLog(operateType = HarborConstants.ASSIGN_AUTH, content = "%s 分配 %s 权限角色为 【%s】,过期日期为【%s】")
    public void saveOwnerAuth(Long projectId, Long organizationId, Integer harborId, List<HarborAuth> dtoList) {
        dtoList.forEach(dto -> {
            dto.setProjectId(projectId);
            dto.setOrganizationId(organizationId);
            dto.setHarborRoleValue(dto.getHarborRoleValue());
            dto.setLocked(HarborConstants.Y);

            //获取harborAuthId，然后保存用户权限到数据库
            ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH, null, null, true, harborId);
            List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborAuthVo>>() {
            }.getType());
            Map<String, HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName, entity -> entity));
            if (harborAuthVoMap.get(dto.getLoginName()) != null) {
                dto.setHarborAuthId(harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId());
            } else {
                dto.setHarborAuthId(saveAndGetHarborAuthId(harborId, dto));
            }
            repository.insertSelective(dto);
        });
    }

    private Long saveAndGetHarborAuthId(Integer harborId, HarborAuth dto) {
        Map<String, Object> bodyMap = new HashMap<>(2);
        Map<String, Object> memberMap = new HashMap<>(1);
        memberMap.put("username", dto.getLoginName());
        bodyMap.put("role_id", dto.getHarborRoleId());
        bodyMap.put("member_user", memberMap);
        harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ONE_AUTH, null, bodyMap, true, harborId);

        ResponseEntity<String> responseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.LIST_AUTH, null, null, true, harborId);
        List<HarborAuthVo> harborAuthVoList = new Gson().fromJson(responseEntity.getBody(), new TypeToken<List<HarborAuthVo>>() {
        }.getType());
        Map<String, HarborAuthVo> harborAuthVoMap = CollectionUtils.isEmpty(harborAuthVoList) ? new HashMap<>(1) : harborAuthVoList.stream().collect(Collectors.toMap(HarborAuthVo::getEntityName, entity -> entity));
        if (harborAuthVoMap.get(dto.getLoginName()) != null) {
            return harborAuthVoMap.get(dto.getLoginName()).getHarborAuthId();
        }
        return -1L;
    }

    /***
     * 检查当前用户是否为仓库管理员,非仓库管理员不允许操作
     */
    @Override
    public void checkProjectAdmin(Long projectId) {
        String userName = DetailsHelper.getUserDetails() == null ? HarborConstants.ANONYMOUS : DetailsHelper.getUserDetails().getUsername();
        UserDTO userDTO = c7nBaseService.queryByLoginName(userName);
        boolean isRoot = false;
        if (!Objects.isNull(userDTO) && userDTO.getAdmin()) {
            isRoot = true;
        }
        if (isRoot || HarborConstants.ANONYMOUS.equals(userName)) {
            return;
        }

        Long userId = DetailsHelper.getUserDetails().getUserId();
        HarborAuth harborAuth = new HarborAuth();
        harborAuth.setProjectId(projectId);
        harborAuth.setUserId(userId);
        HarborAuth dto = repository.select(harborAuth).stream().findFirst().orElse(null);
        if (dto == null) {
            throw new CommonException("error.harbor.auth.null");
        }
        if (!dto.getHarborRoleId().equals(HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId())) {
            throw new CommonException("error.harbor.auth.not.projectAdmin");
        }
    }

    /***
     * 校验是否为最后一个仓库管理员，若是，则不允许更新或者删除
     */
    public void checkLastProjectAdmin(HarborAuth harborAuth, String operateType) {
        HarborAuth dbAuth = repository.selectByCondition(Condition.builder(HarborAuth.class).where(Sqls.custom()
                .andEqualTo(HarborAuth.FIELD_ORGANIZATION_ID, DetailsHelper.getUserDetails().getTenantId())
                .andEqualTo(HarborAuth.FIELD_AUTH_ID, harborAuth.getAuthId())
        ).build()).stream().findFirst().orElse(null);
        if (Objects.isNull(dbAuth)) {
            return;
        }
        //非仓库管理员角色更新删除，通过
        if (!HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId().equals(dbAuth.getHarborRoleId())) {
            return;
        }

        List<HarborAuth> harborAuthList = repository.selectByCondition(Condition.builder(HarborAuth.class).where(Sqls.custom()
                .andEqualTo(HarborAuth.FIELD_PROJECT_ID, harborAuth.getProjectId())
                .andEqualTo(HarborAuth.FIELD_HARBOR_ROLE_ID, HarborConstants.HarborRoleEnum.PROJECT_ADMIN.getRoleId())).build());

        //仓库管理员角色更新，若更新权限角色，则校验
        if (HarborConstants.UPDATE_AUTH.equals(operateType)) {
            //不更新权限角色，通过
            if (harborAuth.getHarborRoleId().equals(dbAuth.getHarborRoleId())) {
                return;
            }
            if (harborAuthList.size() < 2) {
                throw new CommonException("error.harbor.auth.update.last.projectAdmin");
            }
        }
        //仓库管理员删除，若是最后一个不允许删除
        if (HarborConstants.REVOKE_AUTH.equals(operateType) && harborAuthList.size() < 2) {
            throw new CommonException("error.harbor.auth.delete.last.projectAdmin");
        }
    }

    @Override
    public void saveHarborUser(UserDTO userDTO) {
        String loginName = userDTO.getLoginName();
        Long userId = userDTO.getId();
        String email = userDTO.getEmail();
        String realName = userDTO.getRealName();
        //如果使用admin账号创建，则使用当前项目的harbor管理员账号
        if (HarborConstants.ADMIN.equals(loginName)) {
            loginName = harborInfoConfiguration.getUsername();
        }
        //数据库插入制品库用户
        String password = HarborUtil.getPassword();
        if (loginName.equals(harborInfoConfiguration.getUsername())) {
            password = harborInfoConfiguration.getPassword();
        }
        ProdUser prodUser = new ProdUser(userId, loginName, password, 0);
        ProdUser dbProdUser = prodUserService.saveOneUser(prodUser);
        String newPassword = dbProdUser.getPwdUpdateFlag() == 1 ? DESEncryptUtil.decode(dbProdUser.getPassword()) : dbProdUser.getPassword();
        //若为管理员用户，则不创建
        if (loginName.equals(harborInfoConfiguration.getUsername())) {
            return;
        }
        //判断harbor中是否存在当前用户
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("username", loginName);
        ResponseEntity<String> userResponse = harborHttpClient.exchange(HarborConstants.HarborApiEnum.SELECT_USER_BY_USERNAME, paramMap, null, true);
        List<User> userList = JSONObject.parseArray(userResponse.getBody(), User.class);
        Map<String, User> userMap = CollectionUtils.isEmpty(userList) ? new HashMap<>(16) : userList.stream().collect(Collectors.toMap(User::getUsername, dto -> dto));
        //Harbor中新建用户
        if (userMap.get(loginName) == null) {
            User user = new User(loginName, email, newPassword, realName);
            harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_USER, null, user, true);
        } else {
            //更新Harbor中用户密码
            Map<String, Object> bodyMap = new HashMap<>(1);
            bodyMap.put("new_password", newPassword);
            try {
                harborHttpClient.exchange(HarborConstants.HarborApiEnum.CHANGE_PASSWORD, null, bodyMap, true, userMap.get(loginName).getUserId());
            } catch (Exception e) {
                LOGGER.error("error.change.psw", e);
            }
        }
    }
}
