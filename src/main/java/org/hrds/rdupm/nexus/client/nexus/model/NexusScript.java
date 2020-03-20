package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public class NexusScript {
	private String name;
	private String content;
	private String type;

	public String getName() {
		return name;
	}

	public NexusScript setName(String name) {
		this.name = name;
		return this;
	}

	public String getContent() {
		return content;
	}

	public NexusScript setContent(String content) {
		this.content = content;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusScript setType(String type) {
		this.type = type;
		return this;
	}
}
