package org.hrds.rdupm.nexus.app.job;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.ExternalTenantVO;
import org.hrds.rdupm.harbor.app.service.C7nBaseService;
import org.hrds.rdupm.harbor.infra.enums.SaasLevelEnum;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.api.vo.*;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.domain.entity.NexusAssets;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.enums.NexusRepoType;
import org.hrds.rdupm.nexus.infra.mapper.NexusAssetsMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusRepositoryMapper;
import org.hrds.rdupm.nexus.infra.mapper.NexusServerConfigMapper;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;

/**
 * Created by wangxiang on 2021/9/26
 */
@Component
public class NexusCapacityTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(NexusCapacityTask.class);


    /**
     * 免费版 试用版 标准版 一个项目限制 2GB
     */
    @Value("${nexus.choerodon.capacity.limit.base: 2}")
    private Integer nexusBaseCapacityLimit;

    /**
     * 企业版 一个项目限制5G
     */
    @Value("${nexus.choerodon.capacity.limit.business: 5}")
    private Integer nexusBusinessCapacityLimit;

    @Autowired
    private NexusServerConfigMapper nexusServerConfigMapper;

    @Autowired
    private NexusRepositoryMapper nexusRepositoryMapper;

    @Autowired
    private NexusAssetsMapper nexusAssetsMapper;

    @Autowired
    private C7nBaseService c7nBaseService;

    @Autowired
    private NexusClient nexusClient;


    @JobTask(maxRetryCount = 3, code = "nexusCapacityLimit", description = "SaaS组织,试用组织Nexus制品的同步")
    public void nexusCapacityLimit(Map<String, Object> map) {
        //1.查询所有的Saas组织，试用组织
        LOGGER.info("》》》》》》》》》》start nexus capacity limit 》》》》》》》》》》》");
        List<String> saasLevels = Arrays.asList(SaasLevelEnum.FREE.name(), SaasLevelEnum.STANDARD.name(), SaasLevelEnum.SENIOR.name());
        List<ExternalTenantVO> saasTenants = c7nBaseService.querySaasTenants(saasLevels);
        List<ExternalTenantVO> registerTenants = c7nBaseService.queryRegisterTenant();

        List<ExternalTenantVO> registerAndBaseSaasTenants = new ArrayList<>();


        if (!org.apache.commons.collections.CollectionUtils.isEmpty(saasTenants)) {
            // 过滤出免费版，标准版
            List<ExternalTenantVO> freeAndStandardTenants = saasTenants.stream().filter(externalTenantVO -> Arrays.asList(SaasLevelEnum.FREE.name(), SaasLevelEnum.STANDARD.name()).contains(externalTenantVO.getSaasLevel())).collect(Collectors.toList());
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(freeAndStandardTenants)) {
                registerAndBaseSaasTenants.addAll(freeAndStandardTenants);
            }
            //过滤出企业版本
            List<ExternalTenantVO> busExternalTenantVO = saasTenants.stream().filter(externalTenantVO -> StringUtils.equalsIgnoreCase(externalTenantVO.getSaasLevel(), SaasLevelEnum.SENIOR.name())).collect(Collectors.toList());
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(busExternalTenantVO)) {
                registerAndBaseSaasTenants.addAll(busExternalTenantVO);

            }
        }
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(registerTenants)) {
            registerAndBaseSaasTenants.addAll(registerTenants);
        }

        NexusServerConfig defaultNexusConfig = new NexusServerConfig();
        defaultNexusConfig.setDefaultFlag(BaseConstants.Digital.ONE);
        NexusServerConfig nexusServerConfig = nexusServerConfigMapper.selectOne(defaultNexusConfig);
        if (nexusServerConfig == null) {
            LOGGER.info("default nexus config is empty");
            return;
        }

        //查询所有的仓库
        List<ExtdirectResponseData> hostedNexusRepo = getHostedNexusRepo(nexusServerConfig);

        //将基础版和高级版的包持久化到数据库中
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(registerAndBaseSaasTenants)) {
            registerAndBaseSaasTenants.forEach(saaSTenantVO -> {
                persistenceNexusBaseAssetSize(saaSTenantVO, defaultNexusConfig.getConfigId(), hostedNexusRepo);
            });
        }
        LOGGER.info("》》》》》》》》》》end nexus capacity limit 》》》》》》》》》》》");

    }

    public List<ExtdirectResponseData> getHostedNexusRepo(NexusServerConfig nexusServerConfig) {
        NexusServer nexusServer = new NexusServer(nexusServerConfig.getServerUrl(),
                nexusServerConfig.getUserName(),
                DESEncryptUtil.decode(nexusServerConfig.getPassword()));
        nexusClient.setNexusServerInfo(nexusServer);

        ExtdirectRequestVO extdirectRequestVO = new ExtdirectRequestVO();
        extdirectRequestVO.setAction("coreui_Repository");
        extdirectRequestVO.setMethod("readReferences");
        extdirectRequestVO.setTid(1);
        extdirectRequestVO.setType("rpc");

        ExtdirectRequestData extdirectRequestData = new ExtdirectRequestData();
        extdirectRequestData.setPage(1);
        extdirectRequestData.setStart(0);
        List<ExtdirectRequestData> data = new ArrayList<>();
        data.add(extdirectRequestData);
        extdirectRequestVO.setData(data);

        List<ExtdirectResponseData> hostedRepo = new ArrayList<>();
        ExtdirectResponseVO extdirectResponseVO = nexusClient.getNexusExtdirectApi().getAllNexusRepo(extdirectRequestVO);
        List<ExtdirectResponseData> allNexusRepo = new ArrayList<>();
        if (extdirectResponseVO != null && extdirectResponseVO.getResult() != null && !CollectionUtils.isEmpty(extdirectResponseVO.getResult().getData())) {
            allNexusRepo = extdirectResponseVO.getResult().getData();
            //筛选出所有的hosted类型的仓库
            hostedRepo = allNexusRepo.stream().filter(nexusRepo -> StringUtils.equalsIgnoreCase(nexusRepo.getType(), NexusRepoType.HOSTED.getValue())).collect(Collectors.toList());
        }

        return hostedRepo;
    }

    private void persistenceNexusBaseAssetSize(ExternalTenantVO saaSTenantVO, Long defaultNexusConfigId, List<ExtdirectResponseData> hostedNexusRepo) {
        //1.查询这个外部组织下的所有的项目
        List<ProjectDTO> projectDTOS = c7nBaseService.queryProjectByOrgId(saaSTenantVO.getTenantId());
        if (CollectionUtils.isEmpty(projectDTOS)) {
            return;
        }
        projectDTOS.forEach(projectDTO -> {
            //查询项目下的nexus 仓库
            NexusRepository nexusRepository = new NexusRepository();
            nexusRepository.setProjectId(projectDTO.getId());
            nexusRepository.setConfigId(defaultNexusConfigId);
            List<NexusRepository> nexusRepositoryList = nexusRepositoryMapper.select(nexusRepository);
            if (CollectionUtils.isEmpty(nexusRepositoryList)) {
                return;
            }
            nexusRepositoryList.forEach(nexusRepository1 -> {
                //根据仓库的名称 拿到仓库下所有的包
                //1.找到nexus上相应的仓库
                List<ExtdirectResponseData> dbNexusRepos = hostedNexusRepo.stream().filter(nexusRepo -> StringUtils.equalsIgnoreCase(nexusRepo.getName(), nexusRepository1.getNeRepositoryName())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(dbNexusRepos)) {
                    return;
                }
                List<AssetResponseData> components = getComponentsByRepository(dbNexusRepos.get(0), nexusRepository1.getNeRepositoryName());
                //组装成对象插入数据库
                insertNexusAssetsDb(nexusRepository1.getRepositoryId(), nexusRepository1.getProjectId(), components);
            });
        });

    }

    public void insertNexusAssetsDb(Long repositoryId, Long projectId, List<AssetResponseData> components) {
        components.forEach(responseData -> {
            NexusAssets assets = new NexusAssets();
            assets.setName(responseData.getName());
            assets.setProjectId(projectId);
            assets.setRepositoryId(repositoryId);
            assets.setAssetsId(responseData.getId());
            //npm 还是 jar
            if (StringUtils.endsWithIgnoreCase(responseData.getName(), ".jar") && !StringUtils.endsWithIgnoreCase(responseData.getName(), "javadoc.jar")) {
                assets.setType(NexusConstants.RepoType.JAR);
            } else if (StringUtils.endsWithIgnoreCase(responseData.getName(), ".tgz")) {
                assets.setType(NexusConstants.RepoType.NPM);
            } else {
                return;
            }
            assets.setSize(Long.valueOf(responseData.getSize()));
            NexusAssets record = new NexusAssets();
            record.setRepositoryId(repositoryId);
            record.setName(responseData.getName());
            List<NexusAssets> nexusAssetsList = nexusAssetsMapper.select(record);
            if (CollectionUtils.isEmpty(nexusAssetsList)) {
                nexusAssetsMapper.insert(assets);
            }
        });
    }

    public List<AssetResponseData> getComponentsByRepository(ExtdirectResponseData nexusRepo, String repositoryName) {
        List<AssetResponseData> components = new ArrayList<>();
        //如果不是叶子节点就一直请求,找到是叶子节点且是以.jar或者是以.tgz结尾的文件

        //循环遍历这个仓库，找到所有的包文件
        //构造请求参数
        ExtdirectRequestVO extdirectRequestVO = new ExtdirectRequestVO();
        extdirectRequestVO.setAction("coreui_Browse");
        extdirectRequestVO.setMethod("read");
        extdirectRequestVO.setTid(1);
        extdirectRequestVO.setType("rpc");

        ExtdirectRequestData extdirectRequestData = new ExtdirectRequestData();
        extdirectRequestData.setRepositoryName(repositoryName);
        extdirectRequestData.setNode("/");
        List<ExtdirectRequestData> data = new ArrayList<>();
        data.add(extdirectRequestData);
        extdirectRequestVO.setData(data);
        ExtdirectResponseVO extdirectResponseVO = nexusClient.getNexusExtdirectApi().getAllNexusRepo(extdirectRequestVO);
        ExtdirectResponseResult result = extdirectResponseVO.getResult();
        //仓库下的以及文件夹
        List<ExtdirectResponseData> extdirectResponseData = result.getData();
        if (!CollectionUtils.isEmpty(extdirectResponseData)) {
            //遍历每个文件夹找到type类型为asset的组件
            extdirectResponseData.forEach(nexusFolder -> {
                List<AssetResponseData> responseData = new ArrayList<>();
                getComponentsByNexusFolder(nexusFolder, responseData, repositoryName);
                components.addAll(responseData);
            });
        }
        return components;
    }

    private void getComponentsByNexusFolder(ExtdirectResponseData nexusFolder, List<AssetResponseData> responseData, String repositoryName) {
        if (nexusFolder.getLeaf() && StringUtils.equalsIgnoreCase(nexusFolder.getType(), "asset") && (StringUtils.endsWithIgnoreCase(nexusFolder.getId(), ".jar") || StringUtils.endsWithIgnoreCase(nexusFolder.getId(), ".tgz"))) {
            //找到assetId 发起请求
            ExtdirectRequestVO folderRequestVO = new ExtdirectRequestVO();
            folderRequestVO.setAction("coreui_Component");
            folderRequestVO.setMethod("readAsset");
            folderRequestVO.setTid(1);
            folderRequestVO.setType("rpc");

            List<String> data = new ArrayList<>();
            data.add(nexusFolder.getAssetId());
            //仓库名字
            data.add(repositoryName);
            folderRequestVO.setData(data);
            AssetResponseVO responseVO = nexusClient.getNexusExtdirectApi().getAsset(folderRequestVO);
            AssetResponseData assetResponseData = responseVO.getResult().getData();
            responseData.add(assetResponseData);
            return;

        } else if (nexusFolder.getLeaf()) {
            //非文件夹 非包类型的子节点直接返回
            return;
        } else {
            //构造请求参数
            ExtdirectRequestVO folderRequestVO = new ExtdirectRequestVO();
            folderRequestVO.setAction("coreui_Browse");
            folderRequestVO.setMethod("read");
            folderRequestVO.setTid(1);
            folderRequestVO.setType("rpc");
            ExtdirectRequestData folderRequestData = new ExtdirectRequestData();
            folderRequestData.setRepositoryName(repositoryName);
            folderRequestData.setNode(nexusFolder.getId());
            List<ExtdirectRequestData> folderData = new ArrayList<>();
            folderData.add(folderRequestData);
            folderRequestVO.setData(folderData);
            ExtdirectResponseVO responseVO = nexusClient.getNexusExtdirectApi().getAllNexusRepo(folderRequestVO);
            if (responseVO != null && !CollectionUtils.isEmpty(responseVO.getResult().getData())) {
                List<ExtdirectResponseData> extdirectResponseData = responseVO.getResult().getData();
                extdirectResponseData.forEach(responseData1 -> {
                    getComponentsByNexusFolder(responseData1, responseData, repositoryName);
                });
            }
            return;
        }
    }


    @JobTask(maxRetryCount = 3,
            code = "nexusProjectCapacityStatistics",
            description = "nexus项目容量统计",
            params = {@JobParam(name = "choerodonProjectId", description = "猪齿鱼项目id")})
    public void nexusProjectCapacityStatistics(Map<String, Object> param) {
        // <> 获取组织
        long choerodonProjectId = 0L;
        NexusServerConfig nexusServerConfig = new NexusServerConfig();
        nexusServerConfig.setDefaultFlag(BaseConstants.Digital.ONE);
        //查询组织下所有默认服务的仓库
        NexusServerConfig serverConfig = nexusServerConfigMapper.selectOne(nexusServerConfig);
        if (serverConfig == null) {
            return;
        }

        if (param.containsKey("choerodonProjectId") && Objects.nonNull(param.get("choerodonProjectId"))) {
            choerodonProjectId = Long.parseLong(param.get("choerodonProjectId").toString());
        }
        NexusRepository nexusRepository = new NexusRepository();
        nexusRepository.setConfigId(serverConfig.getConfigId());
        nexusRepository.setProjectId(choerodonProjectId);
        List<NexusRepository> nexusRepositoryList = nexusRepositoryMapper.select(nexusRepository);
        if (CollectionUtils.isEmpty(nexusRepositoryList)) {
            LOGGER.info("项目{}下无nexus仓库", choerodonProjectId);
            return;
        }
        nexusRepositoryList.forEach(nexusRepository1 -> {
            // TODO: 2021/9/26
        });

    }

    //1.查询所有仓库的请求参数
//    {
//        "action": "coreui_Repository",
//            "method": "readReferences",
//            "data": [{
//        "page": 1,
//                "start": 0
//    }],
//        "type": "rpc",
//            "tid": 7   //tid是一个必传参数，随便给什么值都行
//    }

    //返回的数据类型，和请求数据类型一致


    //2.过滤出proxy,hosted类型的仓库
    //3.请求每个仓库下面的文件 {"action":"coreui_Browse","method":"read","data":[{"repositoryName":"wx-re-release","node":"/"}],"type":"rpc","tid":37}

    //返回数据类型

}
