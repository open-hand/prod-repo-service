package org.hrds.rdupm.nexus.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 制品库_nexus仓库默认用户信息表Mapper
 *
 * @author weisen.yang@hand-china.com 2020-03-27 11:42:59
 */
public interface NexusUserMapper extends BaseMapper<NexusUser> {

	/**
	 * 发布权限列表查询
	 * @param nexusUser 查询参数
	 * @return List<NexusUserDTO>
	 */
	List<NexusUser> selectList(NexusUser nexusUser);

	/**
	 * 发布权限列表查询-项目层
	 * @param nexusUser 查询参数
	 * @return List<NexusUserDTO>
	 */
	List<NexusUser> selectListPro(NexusUser nexusUser);

	/**
	 * 查询用户其它仓库
	 * @param neUserId 用户Id
	 * @return 仓库名列表
	 */
	List<String> getOtherRepositoryNames(@Param("neUserId") String neUserId);

	/**
	 * 查询用户其它仓库
	 * @param neUserId 用户Id
	 * @return 仓库名列表
	 */
	List<String> getDefaultRepositoryNames(@Param("neUserId") String neUserId);

	/**
	 * 查询默认用户信息
	 * @param userId 用户Id
	 * @return NexusUser
	 */
	NexusUser selectByUserId(@Param("userId") Long userId);

}
