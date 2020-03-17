package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 组件查询返回
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class ComponentResponse {
	private List<NexusComponent> items;
	private String continuationToken;

	public List<NexusComponent> getItems() {
		return items;
	}

	public ComponentResponse setItems(List<NexusComponent> items) {
		this.items = items;
		return this;
	}

	public String getContinuationToken() {
		return continuationToken;
	}

	public ComponentResponse setContinuationToken(String continuationToken) {
		this.continuationToken = continuationToken;
		return this;
	}
}
