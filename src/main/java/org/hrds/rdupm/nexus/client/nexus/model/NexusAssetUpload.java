package org.hrds.rdupm.nexus.client.nexus.model;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;

/**
 * @author weisen.yang@hand-china.com 2020/3/19
 */
public class NexusAssetUpload {
	public static final String JAR = "jar";

	private InputStreamResource assetName;
	/**
	 * 类型暂时只有： jar
	 */
	private String extension;

	public InputStreamResource  getAssetName() {
		return assetName;
	}

	public NexusAssetUpload setAssetName(InputStreamResource assetName) {
		this.assetName = assetName;
		return this;
	}

	public String getExtension() {
		return extension;
	}

	public NexusAssetUpload setExtension(String extension) {
		this.extension = extension;
		return this;
	}
}
