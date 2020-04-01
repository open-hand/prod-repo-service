package org.hrds.rdupm.nexus.client.nexus.api;


import org.hrds.rdupm.nexus.client.nexus.model.NexusServerBlobStore;

import java.util.List;

/**
 * 存储API
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public interface NexusBlobStoreApi {

	/**
	 * 查询列表
	 * @return List<NexusBlobStore>
	 */
	List<NexusServerBlobStore> getBlobStore();
}
