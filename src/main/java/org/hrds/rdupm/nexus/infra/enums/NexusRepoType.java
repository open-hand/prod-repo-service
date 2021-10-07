package org.hrds.rdupm.nexus.infra.enums;

/**
 * Created by wangxiang on 2021/10/5
 */
public enum NexusRepoType {

    HOSTED("hosted");
    private String value;

    NexusRepoType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
