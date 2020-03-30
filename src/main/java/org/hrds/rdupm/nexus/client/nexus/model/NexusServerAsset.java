package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 资产信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusServerAsset {
	private String id;
	private String downloadUrl;
	private String path;
	private String repository;
	private String format;

	public String getId() {
		return id;
	}

	public NexusServerAsset setId(String id) {
		this.id = id;
		return this;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public NexusServerAsset setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
		return this;
	}

	public String getPath() {
		return path;
	}

	public NexusServerAsset setPath(String path) {
		this.path = path;
		return this;
	}

	public String getRepository() {
		return repository;
	}

	public NexusServerAsset setRepository(String repository) {
		this.repository = repository;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public NexusServerAsset setFormat(String format) {
		this.format = format;
		return this;
	}
}

