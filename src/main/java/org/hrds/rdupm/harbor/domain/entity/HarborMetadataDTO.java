package org.hrds.rdupm.harbor.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 11:07 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HarborMetadataDTO {

	@ApiModelProperty(name = "内容信任，true、false")
	@SerializedName("enable_content_trust")
	private String contentTrustFlag;

	@ApiModelProperty(name = "自动扫描镜像，true、false")
	@SerializedName("auto_scan")
	private String autoScanFlag;

	@ApiModelProperty(name = "危害级别，low、medium、high、critical")
	@SerializedName("severity")
	private String severity;

	@ApiModelProperty(name = "启动系统白名单，true、false")
	@SerializedName("reuse_sys_cve_whitelist")
	private String useSysCveFlag;

	@ApiModelProperty(name = "公开访问，true、false")
	@SerializedName("public")
	private String publicFlag;

	@ApiModelProperty(name = "阻止潜在漏洞镜像，true、false")
	@SerializedName("prevent_vul")
	private String preventVulnerableFlag;

}
