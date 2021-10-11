package org.hrds.rdupm.nexus.app.service;

import java.io.File;
import java.io.InputStream;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangxiang on 2021/1/4
 */
public interface NexusComponentHandService {

    void uploadJar(Long repositoryId, NexusClient nexusClient, File filePath, NexusServerComponentUpload nexusServerComponentUpload, NexusServer currentNexusServer, InputStream inputStream);


    void uploadNPM(Long repositoryId, NexusClient nexusClient, NexusRepository nexusRepository, File filePath, NexusServer currentNexusServer);

    void syncAssetsToDB(Long repositoryId);
}
