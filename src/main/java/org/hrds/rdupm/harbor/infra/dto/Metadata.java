package org.hrds.rdupm.harbor.infra.dto;

import com.google.gson.annotations.SerializedName;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/17 15:20
 */
public class Metadata {

    @SerializedName("public")
    private String harborPublic;

    public String getHarborPublic() {
        return harborPublic;
    }

    public void setHarborPublic(String harborPublic) {
        this.harborPublic = harborPublic;
    }
}
