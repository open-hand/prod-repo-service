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
	 * @param harborId
	 * @param imageName
	 * @return
	 */
	PageInfo<HarborImageVo> getByProject(Long harborId, String imageName, PageRequest pageRequest);

	/***
	 * 组织层--获取镜像列表
	 * @param organizationId
	 * @param projectCode
	 * @param projectName
	 * @param imageName
	 * @return
	 */
	PageInfo<HarborImageVo> getByOrg(Long organizationId, String projectCode, String projectName, String imageName, PageRequest pageRequest);
}
