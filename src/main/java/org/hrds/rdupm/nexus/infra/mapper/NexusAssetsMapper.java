package org.hrds.rdupm.nexus.infra.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;


import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库_nexus权限表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-05-26 22:55:13
 */
public interface NexusAssetsMapper extends BaseMapper<NexusAssets> {


    void batchDelete(@Param("componentIds") List<String> componentIds);
}
