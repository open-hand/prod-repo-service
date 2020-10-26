package org.hrds.rdupm.harbor.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hrds.rdupm.harbor.domain.entity.HarborRepoService;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 制品库-harbor仓库服务关联表Mapper
 *
 * @author mofei.li@hand-china.com 2020-06-02 09:51:58
 */
public interface HarborRepoServiceMapper extends BaseMapper<HarborRepoService> {

    /***
     * 删除关联关系
     * @param projectId
     */
    void deleteRelationByProjectId(Long projectId);

    /***
     * 更新projectShare
     * @param projectId
     * @param projectShare
     * @param repoId
     */
    void updateProjectShareByProjectId(@Param("projectId") Long projectId, @Param("projectShare") String projectShare,@Param("repoId") Long repoId);

    /***
     * 删除应用服务于其他自定义仓库的关系
     * @param projectId
     * @param appServiceIds
     * @param repoId
     */
    void deleteOtherRelationByService(@Param("projectId")Long projectId, List<Long> appServiceIds, @Param("repoId") Long repoId);
}
