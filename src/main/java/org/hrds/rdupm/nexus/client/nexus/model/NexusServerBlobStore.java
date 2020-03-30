package org.hrds.rdupm.nexus.client.nexus.model;

/**
 * 存储信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusServerBlobStore {
	private String name;
	private String type;
	private String blobCount;
	private String totalSizeInBytes;
	private String availableSpaceInBytes;

	public String getName() {
		return name;
	}

	public NexusServerBlobStore setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public NexusServerBlobStore setType(String type) {
		this.type = type;
		return this;
	}

	public String getBlobCount() {
		return blobCount;
	}

	public NexusServerBlobStore setBlobCount(String blobCount) {
		this.blobCount = blobCount;
		return this;
	}

	public String getTotalSizeInBytes() {
		return totalSizeInBytes;
	}

	public NexusServerBlobStore setTotalSizeInBytes(String totalSizeInBytes) {
		this.totalSizeInBytes = totalSizeInBytes;
		return this;
	}

	public String getAvailableSpaceInBytes() {
		return availableSpaceInBytes;
	}

	public NexusServerBlobStore setAvailableSpaceInBytes(String availableSpaceInBytes) {
		this.availableSpaceInBytes = availableSpaceInBytes;
		return this;
	}
}
