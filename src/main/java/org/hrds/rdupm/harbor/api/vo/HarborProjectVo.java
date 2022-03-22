package org.hrds.rdupm.harbor.api.vo;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.harbor.domain.entity.HarborMetadataDTO;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.feign.dto.ProjectDTO;
import org.hrds.rdupm.harbor.infra.feign.dto.UserDTO;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 7:30 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborProjectVo {

	public static final String ENCRYPT_KEY = "HarborProjectVo";

	@Encrypt
	private Integer harborId;

	private String code;

	private String name;

	@ApiModelProperty(value ="公开访问，true、false")
	@NotBlank
	private String publicFlag;

	@ApiModelProperty(value = "镜像数量限制")
	private Integer countLimit;

	@ApiModelProperty(value = "存储容量数值")
	private Integer storageNum;

	@ApiModelProperty(value = "存储容量单位")
	private String storageUnit;

	@ApiModelProperty(value ="内容信任，true、false")
	private String contentTrustFlag;

	@ApiModelProperty(value ="阻止潜在漏洞镜像，true、false")
	@NotBlank
	private String preventVulnerableFlag;

	@ApiModelProperty(value ="危害级别，low、medium、high、critical")
	@NotBlank
	private String severity;

	@ApiModelProperty(value ="自动扫描镜像，true、false")
	@NotBlank
	private String autoScanFlag;

	@ApiModelProperty(value ="启用系统白名单，true、false")
	private String useSysCveFlag;

	@ApiModelProperty(value ="启用项目白名单，true、false")
	private String useProjectCveFlag;

	@ApiModelProperty("CVE编号列表")
	private List<String> cveNoList;

	@ApiModelProperty("有效期至")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endDate;

	@ApiModelProperty("镜像数量")
	private Integer repoCount;

	@ApiModelProperty("镜像已使用数量")
	private Integer usedCount;

	@ApiModelProperty("存储容量限制值")
	private Long storageLimit;

	@ApiModelProperty("存储容量已使用值")
	private Long usedStorage;

	@ApiModelProperty(value = "已使用存储容量数值")
	private BigDecimal usedStorageNum;

	@ApiModelProperty(value = "已使用存储容量单位")
	private String usedStorageUnit;

	private ProjectDTO projectDTO;

	private UserDTO userDTO;

	@SerializedName("cve_allowlist")
	private Map<String, Object> cveAllowList;


	public HarborProjectVo(){}

	public HarborProjectVo(HarborProjectDTO harborProjectDTO, Boolean isApiVersionV1) {
		HarborMetadataDTO harborMetadataDTO = harborProjectDTO.getMetadata();
		BeanUtils.copyProperties(harborMetadataDTO,this);
		this.harborId = harborProjectDTO.getHarborId();
		this.code = harborProjectDTO.getName();
		this.repoCount = harborProjectDTO.getRepoCount();
		this.cveAllowList = harborProjectDTO.getCveAllowList();

		if(isApiVersionV1) {
			if (!HarborConstants.TRUE.equals(harborMetadataDTO.getUseSysCveFlag())) {
				this.useProjectCveFlag = HarborConstants.TRUE;
				Map<String, Object> whiteMap = harborProjectDTO.getCveWhiteList();
				List<Map<String, String>> itemMapList = (List<Map<String, String>>) whiteMap.get("items");
				List<String> cveNoList = new ArrayList<>();
				for (Map<String, String> itemMap : itemMapList) {
					cveNoList.add(itemMap.get("cve_id"));
				}
				this.cveNoList = cveNoList;
				this.endDate = HarborUtil.timestampToDate(whiteMap);
			} else {
				this.useProjectCveFlag = HarborConstants.FALSE;
			}
		}else {
			if (!HarborConstants.TRUE.equals(harborMetadataDTO.getUseSysCveFlagV2())) {
				this.useProjectCveFlag = HarborConstants.TRUE;
				Map<String, Object> whiteMap = harborProjectDTO.getCveAllowList();
				List<Map<String, String>> itemMapList = (List<Map<String, String>>) whiteMap.get("items");
				List<String> cveNoList = new ArrayList<>();
				for (Map<String, String> itemMap : itemMapList) {
					cveNoList.add(itemMap.get("cve_id"));
				}
				this.cveNoList = cveNoList;
				this.endDate = HarborUtil.timestampToDate(whiteMap);
			} else {
				this.useProjectCveFlag = HarborConstants.FALSE;
			}
		}

	}

	public HarborProjectVo(Integer countLimit, Integer storageNum, String storageUnit) {
		this.countLimit = countLimit;
		this.storageNum = storageNum;
		this.storageUnit = storageUnit;
	}
}
