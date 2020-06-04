package org.hrds.rdupm.nexus.client.nexus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 脚本执行返回
 * @author weisen.yang@hand-china.com 2020/6/4
 */
@Getter
@Setter
public class NexusScriptResult {
    private String name;
    private String result;
}
