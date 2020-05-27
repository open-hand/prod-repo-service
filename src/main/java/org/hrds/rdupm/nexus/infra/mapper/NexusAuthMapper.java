package org.hrds.rdupm.nexus.infra.mapper;

import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 制品库_nexus权限表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusAuthMapper extends BaseMapper<NexusAuth> {

    /**
     * 列表查询
     * @param nexusAuth 查询参数
     * @return List<NexusAuth>
     */
    List<NexusAuth> list(NexusAuth nexusAuth);
}
