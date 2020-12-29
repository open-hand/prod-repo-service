package org.hrds.rdupm.nexus.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.common.api.vo.ProductLibraryDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.infra.mapper.NexusAuthMapper;
import org.hrds.rdupm.util.KeyDecryptHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.repository.NexusAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 制品库_nexus权限表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
@Component
public class NexusAuthRepositoryImpl extends BaseRepositoryImpl<NexusAuth> implements NexusAuthRepository {

    @Autowired
    private NexusAuthMapper nexusAuthMapper;
    @Autowired
    private NexusRepositoryRepository nexusRepositoryRepository;

    @Override
    public  Map<String, Map<Long, List<String>>> getRoleList(List<Long> repositoryIds) {
        Map<String, Map<Long, List<String>>> resultMap = new HashMap<>(2);
        resultMap.put(ProductLibraryDTO.TYPE_MAVEN, new HashMap<Long, List<String>>());
        resultMap.put(ProductLibraryDTO.TYPE_NPM, new HashMap<Long, List<String>>());
        if (CollectionUtils.isNotEmpty(repositoryIds)) {
            repositoryIds.forEach(repositoryId -> {
                NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
                if (nexusRepository != null) {
                    Map<Long, List<String>> valueMap = resultMap.computeIfAbsent(nexusRepository.getRepoType(), k -> new HashMap<>(16));
                    List<String>  codeList = nexusAuthMapper.getRoleList(DetailsHelper.getUserDetails().getUserId(), repositoryId);
                    valueMap.put(repositoryId, codeList);
                }

            });
        }
        return resultMap;
    }

    @Override
    public Map<String, Map<Object, List<String>>> getUserRoleList(List<Long> repositoryIds) {
        Map<String, Map<Object, List<String>>> resultMap = new HashMap<>(2);
        resultMap.put(ProductLibraryDTO.TYPE_MAVEN, new HashMap<Object, List<String>>());
        resultMap.put(ProductLibraryDTO.TYPE_NPM, new HashMap<Object, List<String>>());
        if (CollectionUtils.isNotEmpty(repositoryIds)) {
            repositoryIds.forEach(repositoryId -> {
                NexusRepository nexusRepository = nexusRepositoryRepository.selectByPrimaryKey(repositoryId);
                if (nexusRepository != null) {
                    Map<Object, List<String>> valueMap = resultMap.computeIfAbsent(nexusRepository.getRepoType(), k -> new HashMap<>(16));
                    List<String>  codeList = nexusAuthMapper.getRoleList(DetailsHelper.getUserDetails().getUserId(), repositoryId);
                    //判断是否需要加密
                    if (DetailsHelper.getUserDetails().getApiEncryptFlag() == BaseConstants.Digital.ONE) {
                        valueMap.put(KeyDecryptHelper.encryptValueWithoutToken(repositoryId), codeList);
                    } else {
                        valueMap.put(repositoryId, codeList);
                    }
                }
            });
        }
        return resultMap;
    }
}
