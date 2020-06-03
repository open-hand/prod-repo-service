package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusComponentQuery;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponent;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

/**
 * 组件API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusComponentsApi {

	/**
	 * 查询maven组件信息
	 * @param componentQuery 查询参数
	 * @return List<NexusComponentInfo>
	 */
	List<NexusServerComponent> searchMavenComponent(NexusComponentQuery componentQuery);

	/**
	 * 查询maven组件信息,分组处理后
	 * @param componentQuery 查询参数
	 * @return List<NexusComponentInfo>
	 */
	List<NexusServerComponentInfo> searchMavenComponentInfo(NexusComponentQuery componentQuery);

	/**
	 * 查询npm组件信息
	 * @param componentQuery 查询参数
	 * @return List<NexusComponentInfo>
	 */
	List<NexusServerComponent> searchNpmComponent(NexusComponentQuery componentQuery);

	/**
	 * 查询npm组件信息,分组处理后
	 * @param componentQuery 查询参数
	 * @return List<NexusComponentInfo>
	 */
	List<NexusServerComponentInfo> searchNpmComponentInfo(NexusComponentQuery componentQuery);

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

	/**
	 * 组件npm tgz包上传
	 * @param repositoryName 仓库名称
	 * @param streamResource 上传信息
	 */
	void createNpmComponent(String repositoryName, InputStreamResource streamResource);
}
