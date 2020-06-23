package org.hrds.rdupm.harbor.api.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/04/28 5:35 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborQuotaVo {
	@ApiModelProperty("Artifact限制")
	private Integer countLimit;

	@ApiModelProperty("Artifact已使用数量")
	private Integer usedCount;

	@ApiModelProperty("存储容量限制值")
	private Long storageLimit;

	@ApiModelProperty(value = "存储容量限制--数值")
	private Integer storageNum;

	@ApiModelProperty(value = "存储容量限制--单位")
	private String storageUnit;

	@ApiModelProperty("存储容量已使用值")
	private Long usedStorage;

	@ApiModelProperty(value = "已使用存储容量--数值")
	private BigDecimal usedStorageNum;

	@ApiModelProperty(value = "已使用存储容量--单位")
	private String usedStorageUnit;

	public HarborQuotaVo(){}

	public HarborQuotaVo( Integer storageNum, String storageUnit, Long usedStorage,  String usedStorageUnit) {
		this.storageNum = storageNum;
		this.storageUnit = storageUnit;
		this.usedStorage = usedStorage;
		this.usedStorageUnit = usedStorageUnit;
	}
}
