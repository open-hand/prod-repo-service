package org.hrds.rdupm.init.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 环境变量配置值-默认nexus服务数据
 *
 * @author weisen.yang@hand-china.com 2020/8/4
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nexus.default")
public class NexusDefaultInitConfiguration {
    private String serverUrl;
    private String username;
    private String password;
    private Integer enableAnonymousFlag;
    private String anonymousUser;
    private String anonymousRole;
}
