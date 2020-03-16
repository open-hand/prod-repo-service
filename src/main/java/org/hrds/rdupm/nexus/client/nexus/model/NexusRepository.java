package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.List;

/**
 * 仓库信息
 * @author weisen.yang@hand-china.com 2020/3/16
 */
public class NexusRepository {
	private String name;
	private String format;
	private String url;
	private String online;
	private String type;


	public String getName() {
		return name;
	}

	public NexusRepository setName(String name) {
		this.name = name;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusRepository setFormat(String format) {
		this.format = format;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public NexusRepository setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getOnline() {
		return online;
	}

	public NexusRepository setOnline(String online) {
		this.online = online;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusRepository setType(String type) {
		this.type = type;
		return this;
	}
}
