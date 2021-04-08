package org.hrds.rdupm.nexus.app.service;

import java.io.File;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangxiang on 2021/1/4
 */
public interface NexusComponentHandService {
    void uploadJar(NexusClient nexusClient, MultipartFile multipartFile, MultipartFile assetPom, NexusServerComponentUpload nexusServerComponentUpload, NexusServer currentNexusServer);

    void uploadJar(NexusClient nexusClient, File filePath, MultipartFile assetPom, NexusServerComponentUpload nexusServerComponentUpload, NexusServer currentNexusServer);

    void uploadNPM(NexusClient nexusClient, NexusRepository nexusRepository, MultipartFile assetTgz, NexusServer currentNexusServer);

}
