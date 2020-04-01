package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponent;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;

import java.util.List;

/**
 * 组件API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusComponentsApi {

	/**
	 * 查询组件信息
	 * @param repositoryName 仓库名称
	 * @return List<NexusComponent>
	 */
	List<NexusServerComponent> getComponents(String repositoryName);

	/**
	 * 查询组件信息,分组处理后
	 * @param repositoryName 仓库名称
	 * @return List<NexusComponentInfo>
	 */
	List<NexusServerComponentInfo> getComponentInfo(String repositoryName);

	/**
	 * 删除组件信息
	 * @param componentId 组件Id
	 */
	void deleteComponent(String componentId);

	/**
	 * 组件jar包上传， 只支持maven release类型
	 * @param componentUpload 上传信息
	 */
	void createMavenComponent(NexusServerComponentUpload componentUpload);
}
