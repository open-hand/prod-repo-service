package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageVo;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 3:07 下午
 */
public interface HarborImageService {

	/***
	 * 项目层--获取镜像列表
	 * @param projectId
	 * @param imageName
	 * @param pageRequest
	 * @return
	 */
	PageInfo<HarborImageVo> getByProject(Long projectId, String imageName, PageRequest pageRequest);

	/***
	 * 组织层--获取镜像列表
	 * @param organizationId
	 * @param projectCode
	 * @param projectName
	 * @param imageName
	 * @param pageRequest
	 * @return
	 */
	PageInfo<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest);

	/***
	 * 删除镜像
	 * @param harborImageVo
	 */
	void delete(HarborImageVo harborImageVo);

	/***
	 * 更新镜像描述
	 * @param harborImageVo
	 */
	void updateDesc(HarborImageVo harborImageVo);
}
