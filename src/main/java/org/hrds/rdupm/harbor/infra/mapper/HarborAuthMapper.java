package org.hrds.rdupm.harbor.infra.mapper;

import java.util.List;

import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库-harbor权限表Mapper
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
public interface HarborAuthMapper extends BaseMapper<HarborAuth> {

	/***
	 * list
	 * @param harborAuth
	 * @return
	 */
	List<HarborAuth> list(HarborAuth harborAuth);

}
