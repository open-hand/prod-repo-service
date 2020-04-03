package org.hrds.rdupm.nexus.domain.repository;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;

import java.util.List;

/**
 * 制品库_nexus仓库默认用户信息表资源库
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusUserRepository extends BaseRepository<NexusUser> {

	/**
	 * 发布权限列表查询
	 * @param nexusUser 查询参数
	 * @param pageRequest 分页参数
	 * @return PageInfo<NexusUserDTO>
	 */
	PageInfo<NexusUser> listUser(NexusUser nexusUser, PageRequest pageRequest);

	/**
	 * 查询用户其它仓库
	 * @param neUserId 用户Id(nexus)
	 * @return 仓库名列表
	 */
	List<String> getOtherRepositoryNames(String neUserId);

	/**
	 * 查询默认用户信息
	 * @param userId 用户Id
	 * @return NexusUser
	 */
	NexusUser selectByUserId(Long userId);
}
