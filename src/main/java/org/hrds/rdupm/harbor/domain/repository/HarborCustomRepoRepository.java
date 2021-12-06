package org.hrds.rdupm.harbor.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rdupm.harbor.domain.entity.HarborCustomRepo;

/**
 * 制品库-harbor自定义镜像仓库表资源库
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
public interface HarborCustomRepoRepository extends BaseRepository<HarborCustomRepo> {

    Boolean checkName(Long projectId, String repositoryName);
}
