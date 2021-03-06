package org.hrds.rdupm.common.app.service.impl;


import java.util.*;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import javax.annotation.Resource;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.common.api.vo.ProductLibraryDTO;
import org.hrds.rdupm.common.app.service.ProdUserService;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.domain.repository.HarborAuthRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.sonatype.nexus.repository.tools.DeadBlobFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 制品库-制品用户表应用服务默认实现
 *
 * @author xiuhong.chen@hand-china.com 2020-05-21 15:47:14
 */
@Service
public class ProdUserServiceImpl implements ProdUserService {

    @Autowired
    private ProdUserRepository prodUserRepository;

    @Autowired
    private ProdUserService service;

    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    @Autowired
    private NexusAuthRepository nexusAuthRepository;

    @Autowired
    private HarborAuthRepository harborAuthRepository;


    @Resource
    private TransactionalProducer transactionalProducer;

    @Autowired
    private C7nBaseService c7nBaseService;

    /***
     * 最少八个字符，至少一个大写字母，一个小写字母和一个数字
     */
    public static Pattern PWD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\s\\S]{8,}$");

    @Override
    public void saveMultiUser(List<ProdUser> prodUserList) {
        if (CollectionUtils.isEmpty(prodUserList)) {
            return;
        }
        prodUserList.forEach(dto -> service.saveOneUser(dto));
    }

    /***
     * 若已经存在，则返回用户信息
     * @param prodUser
     * @return
     */
    @Override
    public ProdUser saveOneUser(ProdUser prodUser) {
        check(prodUser);
        if (StringUtils.isEmpty(prodUser.getPassword())) {
            String password = HarborUtil.getPassword();
            prodUser.setPassword(password);
        }
        List<ProdUser> prodUserList = prodUserRepository.select(ProdUser.FIELD_LOGIN_NAME, prodUser.getLoginName());
        if (CollectionUtils.isEmpty(prodUserList)) {
            prodUserRepository.insertSelective(prodUser);
            return prodUser;
        } else {
            return prodUserList.get(0);
        }
    }

    @Override
    @Saga(code = HarborConstants.HarborSagaCode.UPDATE_PWD, description = "更新密码", inputSchemaClass = ProdUser.class)
    public void updatePwd(ProdUser dto) {
        checkPwd(dto);
        if (!dto.getUserId().equals(DetailsHelper.getUserDetails().getUserId())) {
            throw new CommonException("error.user.not.current.user");
        }
        ProdUser existUser = prodUserRepository.select(ProdUser.FIELD_USER_ID, dto.getUserId()).stream().findFirst().orElse(null);
        if (existUser == null) {
            throw new CommonException("error.user.not.exist");
        }
        if (existUser.getPwdUpdateFlag().intValue() == 0 && !dto.getOldPassword().equals(existUser.getPassword())) {
            throw new CommonException("error.user.oldPwd.not.correct");
        }
        if (existUser.getPwdUpdateFlag().intValue() == 1 && !dto.getOldPassword().equals(DESEncryptUtil.decode(existUser.getPassword()))) {
            throw new CommonException("error.user.oldPwd.not.correct");
        }
        String password = dto.getPassword();

        //数据库更新密码
        String encryptPassword = DESEncryptUtil.encode(password);
        existUser.setPassword(encryptPassword);
        existUser.setPwdUpdateFlag(1);
        prodUserRepository.updateByPrimaryKeySelective(existUser);
        existUser.setCreationDate(null);
        existUser.setLastUpdateDate(null);

        transactionalProducer.apply(StartSagaBuilder.newBuilder()
                        .withSagaCode(HarborConstants.HarborSagaCode.UPDATE_PWD)
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("dockerRepo")
                        .withSourceId(existUser.getUserId()),
                startSagaBuilder -> startSagaBuilder.withPayloadAndSerialize(existUser).withSourceId(existUser.getUserId()));
    }

    @Override
    public Map<String, Map<Object, List<String>>> getUserRoleList(List<NexusRepository> nexusRepositories, Long projectId) {
        Map<String, Map<Object, List<String>>> resultMap = new HashMap<>(6);
        // DOCKER
        Map<Object, List<String>> dockerMap = new HashMap<>();
        List<String> dockerCode = harborAuthRepository.getHarborRoleList(projectId);
        dockerMap.put(projectId, dockerCode != null ? dockerCode : new ArrayList<>());
        resultMap.put(ProductLibraryDTO.TYPE_DOCKER, dockerMap);

        // MAVEN、NPM
        List<Long> repositoryIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(nexusRepositories)) {
            repositoryIds = nexusRepositories.stream().map(NexusRepository::getRepositoryId).collect(Collectors.toList());
        }
        Map<String, Map<Object, List<String>>> nexusMap = nexusAuthRepository.getUserRoleList(repositoryIds);
        nexusMap.forEach(resultMap::put);
        //查询该用户是否为项目所有者,或者为平台root
        // 平台root 应该看到分配仓库权限的按钮
        if (baseServiceFeignClient.checkIsProjectOwner(DetailsHelper.getUserDetails().getUserId(), projectId) || isRoot(DetailsHelper.getUserDetails() == null ? HarborConstants.ANONYMOUS : DetailsHelper.getUserDetails().getUsername())) {
            Map<Object, List<String>> longListMap = nexusMap.get(ProductLibraryDTO.TYPE_MAVEN);
            for (List<String> value : longListMap.values()) {
                value.clear();
                value.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
            }
            Map<Object, List<String>> listMap = nexusMap.get(ProductLibraryDTO.TYPE_NPM);
            for (List<String> value : listMap.values()) {
                value.clear();
                value.add(NexusConstants.NexusRoleEnum.PROJECT_ADMIN.getRoleCode());
            }
        }
        return resultMap;
    }

    private boolean isRoot(String loginName) {
        // 平台root 应该看到分配仓库权限的按钮
        UserDTO userDTO = c7nBaseService.queryByLoginName(loginName);
        boolean isRoot = false;
        if (!Objects.isNull(userDTO) && userDTO.getAdmin()) {
            isRoot = true;
        }
        return isRoot;
    }

    private void check(ProdUser prodUser) {
        AssertUtils.notNull(prodUser, "dto is not null");
        AssertUtils.notNull(prodUser.getUserId(), "userId is not null");
        AssertUtils.notNull(prodUser.getLoginName(), "loginName is not null");
    }

    private void checkPwd(ProdUser prodUser) {
        AssertUtils.notNull(prodUser, "dto is not null");
        AssertUtils.notNull(prodUser.getUserId(), "userId is not null");
        AssertUtils.notNull(prodUser.getOldPassword(), "loginName is not null");
        AssertUtils.notNull(prodUser.getPassword(), "loginName is not null");
        AssertUtils.notNull(prodUser.getRePassword(), "loginName is not null");
        if (!prodUser.getPassword().equals(prodUser.getRePassword())) {
            throw new CommonException("error.user.newPwd.not.same.rePwd");
        }
        if (prodUser.getPassword().equals(prodUser.getOldPassword())) {
            throw new CommonException("error.user.newPwd.same.oldPwd");
        }
        if (!PWD_PATTERN.matcher(prodUser.getPassword()).matches()) {
            throw new CommonException("error.user.pwd.pattern");
        }
    }

}
