package org.hrds.rdupm.nexus.app.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.hrds.rdupm.nexus.app.service.NexusComponentHandService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerAssetUpload;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2021/1/4
 */
@Service
public class NexusComponentHandServiceImpl implements NexusComponentHandService {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    @Async
    public void uploadJar(NexusClient nexusClient, File jarFile, NexusServerComponentUpload nexusServerComponentUpload, NexusServer currentNexusServer, InputStream assetPomStream) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>进入异步的分片上传方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try (
                InputStream assetJarStream = jarFile != null ? new FileInputStream(jarFile) : null;
        ) {
            List<NexusServerAssetUpload> assetUploadList = new ArrayList<>();
            if (assetJarStream != null) {
                NexusServerAssetUpload assetUpload = new NexusServerAssetUpload();
                assetUpload.setAssetName(new InputStreamResource(assetJarStream));
                assetUpload.setExtension(NexusServerAssetUpload.JAR);
                assetUploadList.add(assetUpload);
            }
            if (assetPomStream != null) {
                NexusServerAssetUpload assetUpload = new NexusServerAssetUpload();
                assetUpload.setAssetName(new InputStreamResource(assetPomStream));
                assetUpload.setExtension(NexusServerAssetUpload.POM);
                assetUploadList.add(assetUpload);
            }
            nexusServerComponentUpload.setAssetUploads(assetUploadList);
            nexusClient.getComponentsApi().createMavenComponent(nexusServerComponentUpload, currentNexusServer);
        } catch (Exception e) {
            logger.error("上传jar包错误", e);
        } finally {
            // remove配置信息
            nexusClient.removeNexusServerInfo();
            if (assetPomStream != null) {
                try {
                    assetPomStream.close();
                } catch (IOException e) {
                    logger.error("关闭流失败", e);
                }
            }
        }
    }


    @Override
    @Async
    public void uploadNPM(NexusClient nexusClient, NexusRepository nexusRepository, File filePath, NexusServer currentNexusServer) {
        try (
                InputStream assetTgzStream = new FileInputStream(filePath);
        ) {

            if (assetTgzStream != null) {
                InputStreamResource streamResource = new InputStreamResource(assetTgzStream);
                nexusClient.getComponentsApi().createNpmComponent(nexusRepository.getNeRepositoryName(), streamResource, currentNexusServer);
            }
        } catch (IOException e) {
            logger.error("上传npm包错误", e);
            throw new CommonException(e.getMessage());
        } finally {
            // remove配置信息
            nexusClient.removeNexusServerInfo();
        }
    }
}
