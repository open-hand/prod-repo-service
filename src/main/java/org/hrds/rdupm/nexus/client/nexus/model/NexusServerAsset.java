package org.hrds.rdupm.nexus.client.nexus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 资产信息
 * @author weisen.yang@hand-china.com 2020/3/17
 */
@Getter
@Setter
public class NexusServerAsset {
	private String id;
	private String downloadUrl;
	private String path;
	private String repository;
	private String format;
	private String extension;
	private CheckSum checksum;
	private String lastUpdateDate;
	private String componentId;
	private String lastDownloadDate;
	private String createdBy;
	private String createdByIp;


	public class CheckSum {
		private String sha1;

		public String getSha1() {
			return sha1;
		}

		public void setSha1(String sha1) {
			this.sha1 = sha1;
		}
	}
}

