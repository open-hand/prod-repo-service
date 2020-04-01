package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public class NexusServerScript {
	private String name;
	private String content;
	private String type;

	public String getName() {
		return name;
	}

	public NexusServerScript setName(String name) {
		this.name = name;
		return this;
	}

	public String getContent() {
		return content;
	}

	public NexusServerScript setContent(String content) {
		this.content = content;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusServerScript setType(String type) {
		this.type = type;
		return this;
	}
}
