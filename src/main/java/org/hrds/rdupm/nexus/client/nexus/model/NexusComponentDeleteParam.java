package org.hrds.rdupm.nexus.client.nexus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 删除Component 参数
 * @author weisen.yang@hand-china.com 2020/6/4
 */
@Getter
@Setter
public class NexusComponentDeleteParam {
    private String repositoryName;
    private List<String> components;
}
