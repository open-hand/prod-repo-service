package org.hrds.rdupm.harbor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/23 2:33 下午
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "harbor.init")
public class HarborInitConfiguration {

	private String defaultRepoUrl;

	private String defaultRepoUsername;

	private String defaultRepoPassword;

	private String customRepoUrl;

	private String customRepoUsername;

	private String customRepoPassword;

}
