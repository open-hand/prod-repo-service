package org.hrds.rdupm.nexus.app.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.app.job.NexusCapacityTask;
import org.hrds.rdupm.nexus.app.service.NexusComponentHandService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.api.NexusComponentsApi;
import org.hrds.rdupm.nexus.client.nexus.api.vo.AssetResponseData;
import org.hrds.rdupm.nexus.client.nexus.api.vo.ExtdirectResponseData;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerAsset;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerAssetUpload;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentUpload;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

/**
 * Created by wangxiang on 2021/1/4
 */
@Service
public class NexusComponentHandServiceImpl implements NexusComponentHandService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NexusCapacityTask nexusCapacityTask;
    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;
    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;

    @Autowired
    private NexusClient nexusClient;
    @Autowired
    private NexusComponentsApi nexusComponentsApi;

    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;


    @Override
    @Async
    public void uploadJar(Long repositoryId, NexusClient nexusClient, File jarFile, NexusServerComponentUpload nexusServerComponentUpload, NexusServer currentNexusServer, InputStream assetPomStream) {
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
            syncAssetsToDB(repositoryId);
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
    @Transactional(rollbackFor = Exception.class)
    public void syncAssetsToDB(Long repositoryId) {
        // 如果用的是默认的nexus
        NexusRepository nexusRepository = nexusRepositoryMapper.selectByPrimaryKey(repositoryId);
        NexusServerConfig nexusServerConfig = nexusServerConfigMapper.selectByPrimaryKey(nexusRepository.getConfigId());
        if (nexusServerConfig.getDefaultFlag().equals(BaseConstants.Flag.YES)) {
            List<ExtdirectResponseData> hostedNexusRepo = nexusCapacityTask.getHostedNexusRepo(nexusServerConfig);
            //根据仓库的名称 拿到仓库下所有的包
            //1.找到nexus上相应的仓库
            List<ExtdirectResponseData> dbNexusRepos = hostedNexusRepo.stream().filter(nexusRepo -> StringUtils.equalsIgnoreCase(nexusRepo.getName(), nexusRepository.getNeRepositoryName())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dbNexusRepos)) {
                return;
            }
            List<AssetResponseData> components = nexusCapacityTask.getComponentsByRepository(dbNexusRepos.get(0), nexusRepository.getNeRepositoryName());
            //组装成对象插入数据库
            nexusCapacityTask.insertNexusAssetsDb(nexusRepository.getRepositoryId(), nexusRepository.getProjectId(), components);
        }
        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAssetsToDB(Long repositoryId, String path) {
        NexusRepository nexusRepository = nexusRepositoryMapper.selectByPrimaryKey(repositoryId);
        AssertUtils.notNull(nexusRepository, "error.nexus.repository.is.null");
        NexusServerConfig nexusServerConfig = nexusServerConfigMapper.selectByPrimaryKey(nexusRepository.getConfigId());
        AssertUtils.notNull(nexusRepository, "error.nexus.config.is.null");
        if (nexusServerConfig.getDefaultFlag().equals(BaseConstants.Flag.YES)) {
            //塞上验证信息
            NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                    nexusServerConfig.getUserName(),
                    DESEncryptUtil.decode(nexusServerConfig.getPassword()));
            nexusClient.setNexusServerInfo(nexusServer);
            NexusServerAsset asset = nexusComponentsApi.findAsset(nexusRepository.getNeRepositoryName(), path);
            if (asset != null) {
                NexusAssets assets = new NexusAssets();
                assets.setName(asset.getPath());
                assets.setProjectId(nexusRepository.getProjectId());
                assets.setRepositoryId(repositoryId);
                assets.setAssetsId(asset.getId());
                //npm 还是 jar
                if (StringUtils.endsWithIgnoreCase(asset.getPath(), ".jar")) {
                    assets.setType(NexusConstants.RepoType.JAR);
                } else if (StringUtils.endsWithIgnoreCase(asset.getPath(), ".tgz")) {
                    assets.setType(NexusConstants.RepoType.NPM);
                }
                assets.setSize(Long.valueOf(asset.getSize()));
                NexusAssets record = new NexusAssets();
                record.setRepositoryId(repositoryId);
                record.setName(asset.getPath());
                List<NexusAssets> nexusAssetsList = nexusAssetsMapper.select(record);
                if (CollectionUtils.isEmpty(nexusAssetsList)) {
                    nexusAssetsMapper.insert(assets);
                }
            }
        }


    }

    @Override
    @Async
    public void uploadNPM(Long repositoryId, NexusClient nexusClient, NexusRepository nexusRepository, File filePath, NexusServer currentNexusServer) {
        try (
                InputStream assetTgzStream = new FileInputStream(filePath);
        ) {

            if (assetTgzStream != null) {
                InputStreamResource streamResource = new InputStreamResource(assetTgzStream);
                nexusClient.getComponentsApi().createNpmComponent(nexusRepository.getNeRepositoryName(), streamResource, currentNexusServer);
            }
            syncAssetsToDB(repositoryId);
        } catch (IOException e) {
            logger.error("上传npm包错误", e);
            throw new CommonException(e.getMessage());
        } finally {
            // remove配置信息
            nexusClient.removeNexusServerInfo();
        }
    }
}
