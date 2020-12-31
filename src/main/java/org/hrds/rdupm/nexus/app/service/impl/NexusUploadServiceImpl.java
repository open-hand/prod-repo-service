package org.hrds.rdupm.nexus.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.hrds.rdupm.nexus.app.service.NexusUploadService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerAssetUpload;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2020/12/31
 */
@Service
public class NexusUploadServiceImpl  implements NexusUploadService {
    private static final Logger logger = LoggerFactory.getLogger(NexusUploadServiceImpl.class);

    @Autowired
    private NexusClient nexusClient;

    @Async
    public void uploadJar(NexusServerComponentUpload componentUpload, MultipartFile assetJar, MultipartFile assetPom) {

    }
}
