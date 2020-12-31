package org.hrds.rdupm.nexus.app.service;

import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangxiang on 2020/12/31
 */
public interface NexusUploadService {
     void uploadJar(NexusServerComponentUpload componentUpload, MultipartFile assetJar, MultipartFile assetPom);
}
