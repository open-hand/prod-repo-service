package org.hrds.rdupm.nexus.api.vo;

import java.util.List;


/**
 * Created by wangxiang on 2021/6/23
 */
public class MavenComponentVO {

    private List<String> componentIds;

    public List<String> getComponentIds() {
        return componentIds;
    }

    public void setComponentIds(List<String> componentIds) {
        this.componentIds = componentIds;
    }
}
