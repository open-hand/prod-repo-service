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

    private String version;

    public HarborCustomConfiguration() {
    }

    public HarborCustomConfiguration(String name, String url, String loginName, String password, String email, String harborProject, String publicFlag, String version) {
        this.name = name;
        this.url = url;
        this.loginName = loginName;
        this.password = password;
        this.email = email;
        this.harborProject = harborProject;
        this.publicFlag = publicFlag;
        this.version = version;
    }

    public HarborCustomConfiguration(String url, String loginName, String password, String version) {
        this.url = url;
        this.loginName = loginName;
        this.password = password;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
