package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 存储信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusBlobStore {
	private String name;
	private String type;
	private String blobCount;
	private String totalSizeInBytes;
	private String availableSpaceInBytes;

	public String getName() {
		return name;
	}

	public NexusBlobStore setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusBlobStore setType(String type) {
		this.type = type;
		return this;
	}

	public String getBlobCount() {
		return blobCount;
	}

	public NexusBlobStore setBlobCount(String blobCount) {
		this.blobCount = blobCount;
		return this;
	}

	public String getTotalSizeInBytes() {
		return totalSizeInBytes;
	}

	public NexusBlobStore setTotalSizeInBytes(String totalSizeInBytes) {
		this.totalSizeInBytes = totalSizeInBytes;
		return this;
	}

	public String getAvailableSpaceInBytes() {
		return availableSpaceInBytes;
	}

	public NexusBlobStore setAvailableSpaceInBytes(String availableSpaceInBytes) {
		this.availableSpaceInBytes = availableSpaceInBytes;
		return this;
	}
}
