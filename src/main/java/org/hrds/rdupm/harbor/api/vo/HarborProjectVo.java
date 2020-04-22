package org.hrds.rdupm.harbor.api.vo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.harbor.domain.entity.HarborMetadataDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.springframework.beans.BeanUtils;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 7:30 下午
 */
@Getter
@Setter
public class HarborProjectVo {

	private Integer harborId;

	private String code;

	private String name;

	@ApiModelProperty(name = "公开访问，true、false")
	@NotBlank
	private String publicFlag;

	@ApiModelProperty(value = "镜像数量限制")
	private Integer countLimit;

	@ApiModelProperty(value = "存储容量数值")
	private Integer storageNum;

	@ApiModelProperty(value = "存储容量单位")
	private String storageUnit;

	@ApiModelProperty(name = "内容信任，true、false")
	private String contentTrustFlag;

	@ApiModelProperty(name = "阻止潜在漏洞镜像，true、false")
	@NotBlank
	private String preventVulnerableFlag;

	@ApiModelProperty(name = "危害级别，low、medium、high、critical")
	@NotBlank
	private String severity;

	@ApiModelProperty(name = "自动扫描镜像，true、false")
	@NotBlank
	private String autoScanFlag;

	@ApiModelProperty(name = "启用系统白名单，true、false")
	private String useSysCveFlag;

	@ApiModelProperty(name = "启用项目白名单，true、false")
	private String useProjectCveFlag;

	@ApiModelProperty("CVE编号列表")
	private List<String> cveNoList;

	@ApiModelProperty("有效期至")
	private Date endDate;

	@ApiModelProperty("镜像数量")
	private Integer repoConut;

	public HarborProjectVo(){}

	public HarborProjectVo(HarborProjectDTO harborProjectDTO){
		HarborMetadataDTO harborMetadataDTO = harborProjectDTO.getMetadata();
		BeanUtils.copyProperties(harborMetadataDTO,this);
		this.harborId = harborProjectDTO.getProjectId();
		this.code = harborProjectDTO.getName();
		this.repoConut = harborProjectDTO.getRepoCount();
	}
}
