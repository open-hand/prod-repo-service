package org.hrds.rdupm.nexus.client.nexus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 删除仓库，仓库组件count查询
 *
 * @author weisen.yang@hand-china.com 2020/10/21
 */
@Getter
@Setter
public class NexusComponentCountParam {
    private String repositoryName;
}
