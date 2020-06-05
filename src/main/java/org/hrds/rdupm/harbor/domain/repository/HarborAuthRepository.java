package org.hrds.rdupm.harbor.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;

/**
 * 制品库-harbor权限表资源库
 *
 * @author xiuhong.chen@hand-china.com 2020-04-27 16:12:54
 */
public interface HarborAuthRepository extends BaseRepository<HarborAuth> {

	List<String> getHarborRoleList(Long id);
}
