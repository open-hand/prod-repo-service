package org.hrds.rdupm.harbor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/06/02 15:23
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Component
public class HarborCustomConfiguration {
    private String name;

    private String url;

    private String loginName;

    private String password;

    private String email;

    private String harborProject;

    private String publicFlag;

    public HarborCustomConfiguration() {
    }

    public HarborCustomConfiguration(String name, String url, String loginName, String password, String email, String harborProject, String publicFlag) {
        this.name = name;
        this.url = url;
        this.loginName = loginName;
        this.password = password;
        this.email = email;
        this.harborProject = harborProject;
        this.publicFlag = publicFlag;
    }

    public HarborCustomConfiguration(String url, String loginName, String password) {
        this.url = url;
        this.loginName = loginName;
        this.password = password;
    }
}
