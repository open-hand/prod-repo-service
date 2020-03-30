package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public class NexusServerRepository {
	private String name;
	private String format;
	private String url;
	private String online;
	private String type;


	public String getName() {
		return name;
	}

	public NexusServerRepository setName(String name) {
		this.name = name;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerRepository setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public NexusServerRepository setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getOnline() {
		return online;
	}

	public NexusServerRepository setOnline(String online) {
		this.online = online;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusServerRepository setType(String type) {
		this.type = type;
		return this;
	}
}
