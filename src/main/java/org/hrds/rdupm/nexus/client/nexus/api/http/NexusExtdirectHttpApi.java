package org.hrds.rdupm.nexus.client.nexus.api.http;

import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusExtdirectApi;
import org.hrds.rdupm.nexus.client.nexus.api.vo.AssetResponseVO;
import org.hrds.rdupm.nexus.client.nexus.api.vo.ExtdirectRequestVO;
import org.hrds.rdupm.nexus.client.nexus.api.vo.ExtdirectResponseVO;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2021/10/4
 */
@Component
public class NexusExtdirectHttpApi implements NexusExtdirectApi {

    @Autowired
    private NexusRequest nexusRequest;

    @Override
    public ExtdirectResponseVO getAllNexusRepo(ExtdirectRequestVO extdirectRequestVO) {
        ExtdirectResponseVO extdirectResponseVO = null;
        try {
            String requestParamJson = JsonHelper.marshalByJackson(extdirectRequestVO);
            ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Extdirect.EXTDIRECT, HttpMethod.POST, null, requestParamJson);
            extdirectResponseVO = JsonHelper.unmarshalByJackson(responseEntity.getBody(), ExtdirectResponseVO.class);
        } catch (Exception e) {
            throw new CommonException(e);
        }
        return extdirectResponseVO;
    }

    @Override
    public AssetResponseVO getAsset(ExtdirectRequestVO folderRequestVO) {
        AssetResponseVO assetResponseVO = null;
        try {
            String requestParamJson = JsonHelper.marshalByJackson(folderRequestVO);
            ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Extdirect.EXTDIRECT, HttpMethod.POST, null, requestParamJson);
            assetResponseVO = JsonHelper.unmarshalByJackson(responseEntity.getBody(), AssetResponseVO.class);
        } catch (Exception e) {
            throw new CommonException(e);
        }
        return assetResponseVO;
    }
}
