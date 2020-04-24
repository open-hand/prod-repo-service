package org.hrds.rdupm.harbor.infra.config;

import org.hrds.rdupm.harbor.api.vo.ConfigVO;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:13
 */
public class ConfigurationProperties extends HarborConfigurationProperties {

    private String type;


    public ConfigurationProperties() {

    }

    public ConfigurationProperties(HarborConfigurationProperties harborConfigurationProperties) {
        this.setBaseUrl(harborConfigurationProperties.getBaseUrl());
        this.setProject(harborConfigurationProperties.getProject());
        this.setUsername(harborConfigurationProperties.getUsername());
        this.setPassword(harborConfigurationProperties.getPassword());
        this.setInsecureSkipTlsVerify(harborConfigurationProperties.getInsecureSkipTlsVerify());
        this.setEnabled(harborConfigurationProperties.isEnabled());
    }

    public ConfigurationProperties(ConfigVO config) {
        this.setBaseUrl(config.getUrl());
        this.setProject(config.getProject());
        this.setUsername(config.getUserName());
        this.setPassword(config.getPassword());
        //暂时设置为 false => 去校验用户
        this.setInsecureSkipTlsVerify(false);
    }

    public ConfigurationProperties(ChartConfigurationProperties chartConfigurationProperties) {
        this.setBaseUrl(chartConfigurationProperties.getBaseUrl());
    }

    public ConfigurationProperties(String harborBaseUrl, String harborUserName, String harborPassword, Boolean insecureSkipTlsVerify, String project, String type){
        this.setBaseUrl(harborBaseUrl);
        this.setUsername(harborUserName);
        this.setPassword(harborPassword);
        this.setInsecureSkipTlsVerify(insecureSkipTlsVerify);
        this.setProject(project);
        this.setType(type);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
