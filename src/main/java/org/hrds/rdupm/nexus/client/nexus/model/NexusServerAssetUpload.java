package org.hrds.rdupm.nexus.client.nexus.model;

import org.springframework.core.io.InputStreamResource;

/**
 * @author weisen.yang@hand-china.com 2020/3/19
 */
public class NexusServerAssetUpload {
	public static final String JAR = "jar";
	public static final String POM = "pom";

	public static final String XML = "xml";

	/**
	 * NPM
	 */
	public static final String TGZ = "tgz";

	private InputStreamResource assetName;
	/**
	 * 类型暂时只有： jar
	 */
	private String extension;

	public InputStreamResource  getAssetName() {
		return assetName;
	}

	public NexusServerAssetUpload setAssetName(InputStreamResource assetName) {
		this.assetName = assetName;
		return this;
	}

	public String getExtension() {
		return extension;
	}

	public NexusServerAssetUpload setExtension(String extension) {
		this.extension = extension;
		return this;
	}
}
