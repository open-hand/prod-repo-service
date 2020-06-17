package org.hrds.rdupm.nexus.infra.repository.impl;

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
}
