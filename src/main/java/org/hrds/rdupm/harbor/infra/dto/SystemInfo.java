package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:22
 */
public class SystemInfo {

    @SerializedName("harbor_version")
    private String harborVersion;


    public String getHarborVersion() {
        return harborVersion;
    }

    public void setHarborVersion(String harborVersion) {
        this.harborVersion = harborVersion;
    }
}
