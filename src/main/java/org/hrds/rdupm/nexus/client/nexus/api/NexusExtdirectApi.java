package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.api.vo.AssetResponseVO;
import org.hrds.rdupm.nexus.client.nexus.api.vo.ExtdirectRequestVO;
import org.hrds.rdupm.nexus.client.nexus.api.vo.ExtdirectResponseVO;

/**
 * nexus extdirect的API
 *
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public interface NexusExtdirectApi {


    /**
     * 查询nexus仓库
     * @param extdirectRequestVO
     * @return
     */
    ExtdirectResponseVO getAllNexusRepo(ExtdirectRequestVO extdirectRequestVO);


    AssetResponseVO getAsset(ExtdirectRequestVO folderRequestVO);
}
