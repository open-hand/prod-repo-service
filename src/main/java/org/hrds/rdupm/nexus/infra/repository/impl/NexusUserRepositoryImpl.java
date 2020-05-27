package org.hrds.rdupm.nexus.infra.repository.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.StringUtils;
import org.hrds.rdupm.nexus.infra.mapper.NexusUserMapper;
import org.hrds.rdupm.nexus.infra.util.PageConvertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.domain.repository.NexusUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 制品库_nexus仓库默认用户信息表 资源库实现
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
@Component
public class NexusUserRepositoryImpl extends BaseRepositoryImpl<NexusUser> implements NexusUserRepository {

//	@Autowired
//	private NexusUserMapper nexusUserMapper;
//
//	@Override
//	public PageInfo<NexusUser> listUser(NexusUser nexusUser, PageRequest pageRequest) {
//		Page<NexusUser> page =  PageHelper.doPageAndSort(pageRequest, () -> nexusUserMapper.selectList(nexusUser));
//		page.getContent().forEach(user -> {
//			user.setOtherRepositoryName(nexusUserMapper.getOtherRepositoryNames(user.getNeUserId()));
//		});
//		return PageConvertUtils.convert(page);
//	}
//
//	@Override
//	public PageInfo<NexusUser> listUserPro(NexusUser nexusUser, PageRequest pageRequest) {
//		Page<NexusUser> page =  PageHelper.doPageAndSort(pageRequest, () -> nexusUserMapper.selectListPro(nexusUser));
//		page.getContent().forEach(user -> {
//			user.setOtherRepositoryName(nexusUserMapper.getOtherRepositoryNames(user.getNeUserId()));
//			user.setDefaultRepositoryNames(nexusUserMapper.getDefaultRepositoryNames(user.getNeUserId()));
//			user.setNeRepositoryName(StringUtils.join(user.getDefaultRepositoryNames(), ","));
//			user.setEditFlag(user.getIsDefault().equals(1));
//		});
//		return PageConvertUtils.convert(page);
//	}
//
//	@Override
//	public List<String> getOtherRepositoryNames(String neUserId) {
//		return nexusUserMapper.getOtherRepositoryNames(neUserId);
//	}
//
//	@Override
//	public NexusUser selectByUserId(Long userId) {
//		return nexusUserMapper.selectByUserId(userId);
//	}

}
