package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.init.config.NexusProxyConfigProperties;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.infra.util.VelocityUtils;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置信息父类
 *
 * @author weisen.yang@hand-china.com 2020/5/13
 */
@ApiModel("nexus maven-配置信息父类")
@Setter
@Getter
public class NexusBaseGuideDTO {


    /**
     * 值设置
     *
     * @param nexusServerRepository nexus服务,仓库信息
     * @param nexusRepository       数据库表，仓库信息
     * @param nexusUser             仓库默认管理用户
     */
    public void handlePullGuideValue(NexusServerRepository nexusServerRepository,
                                     NexusRepository nexusRepository,
                                     NexusUser nexusUser,
                                     NexusServerConfig serverConfig,
                                     NexusProxyConfigProperties nexusProxyConfigProperties) {
        // 拉取配置，仓库信息
        Map<String, Object> map = getStringObjectMap(nexusServerRepository);
        if (!Objects.isNull(nexusProxyConfigProperties)) {
            map.put("url", getProxyUrl(nexusServerRepository.getUrl(), nexusProxyConfigProperties));
        }
        handlePullGuideValue(nexusServerRepository, nexusRepository, nexusUser, serverConfig, map);
    }

    public void handlePullGuideValue(NexusServerRepository nexusServerRepository,
                                     NexusRepository nexusRepository,
                                     NexusUser nexusUser,
                                     NexusServerConfig serverConfig,
                                     Map<String, Object> map) {

        // 拉取信息
        // 仓库是匿名访问，且nexus开启了匿名访问控制。 则不显示，否则显示
        this.setPullServerFlag(!(nexusRepository.getAllowAnonymous().equals(BaseConstants.Flag.YES)
                && serverConfig.getEnableAnonymousFlag().equals(BaseConstants.Flag.YES)));
        if (this.getPullServerFlag() && nexusUser != null) {
            // 要显示的时候，返回数据
            String nePullUserPassword = DESEncryptUtil.decode(nexusUser.getNePullUserPassword());

            map.put("username", nexusUser.getNePullUserId());
            this.setPullServerInfo(VelocityUtils.getJsonString(map, VelocityUtils.SET_SERVER_FILE_NAME));
            this.setPullPassword(nePullUserPassword);
            this.setPullServerInfoPassword(this.getPullServerInfo().replace("[password]", this.getPullPassword()));
            this.setPullServerInfoPassword(this.getPullServerInfoPassword().replace("[username]", nexusUser.getNePullUserId()));
        }
        this.setPullPomRepoInfo(VelocityUtils.getJsonString(map, VelocityUtils.POM_REPO_FILE_NAME));

    }

    private Map<String, Object> getStringObjectMap(NexusServerRepository nexusServerRepository) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("versionPolicy", nexusServerRepository.getVersionPolicy());
        map.put("repositoryName", nexusServerRepository.getName());
        map.put("url", nexusServerRepository.getUrl());
        map.put("type", nexusServerRepository.getType());
        return map;
    }


    public String getProxyUrl(String url, NexusProxyConfigProperties nexusProxyConfigProperties) {
        // http://xxx/repository/zmf-test-mixed  =>http://api/route/v1/nexus/proxy/repository/zmf-test-mixed
        String baseUrl = url.split(nexusProxyConfigProperties.getBase())[1];
        return nexusProxyConfigProperties.getServicesGatewayUrl() + nexusProxyConfigProperties.getServiceRoute() + nexusProxyConfigProperties.getUriPrefix() + baseUrl;
    }

    @ApiModelProperty(value = "拉取配置：server配置是否显示")
    private Boolean pullServerFlag;
    @ApiModelProperty(value = "拉取配置：server配置信息")
    private String pullServerInfo;
    @ApiModelProperty(value = "拉取配置：server配置信息(包含密码)")
    private String pullServerInfoPassword;
    @ApiModelProperty(value = "拉取配置：密码")
    private String pullPassword;

    @ApiModelProperty(value = "拉取配置：pom文件，仓库配置")
    private String pullPomRepoInfo;
    @ApiModelProperty(value = "拉取配置：拉取配置是否显示")
    private Boolean showPushFlag;
}
