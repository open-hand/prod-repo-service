package org.hrds.rdupm.harbor.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rdupm.harbor.api.vo.HarborImageReTag;
import org.hrds.rdupm.harbor.api.vo.HarborImageTagVo;

/**
 * description
 *
 * @author chenxiuhong 2020/04/24 1:44 下午
 */
public interface HarborImageTagService {
	/***
	 * 查询镜像TAG列表
	 * @param projectId
	 * @param repoName
	 * @param tagName
	 * @param pageRequest
	 * @return
	 */
	Page<HarborImageTagVo> list(Long projectId,String repoName, String tagName, PageRequest pageRequest);

	/***
	 * 获取构建日志
	 * @param repoName
	 * @param tagName
	 * @return
	 */
	String buildLog(String repoName, String tagName, String digest);

	/***
	 * 删除镜像TAG
	 * @param repoName
	 * @param tagName
	 */
	void delete(String repoName, String tagName, Boolean adminAccountFlag);

	/***
	 * 复制镜像TAG
	 * @param harborImageReTag
	 */
	void copyTag(HarborImageReTag harborImageReTag);

}
