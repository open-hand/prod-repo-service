package org.hrds.rdupm.harbor.api.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/05/28 4:31 下午
 */
@Getter
@Setter
public class FdProjectDto{
	String code;
	String name;
	Long projectId;
	Long organizationId;
	Long createdBy;
	String tenantNum;
	String tenantName;
	String tenantProjectCode;

	public FdProjectDto() {
	}

	public FdProjectDto(String code, String name, Long projectId, Long organizationId, Long createdBy, String tenantNum, String tenantName, String tenantProjectCode) {
		this.code = code;
		this.name = name;
		this.projectId = projectId;
		this.organizationId = organizationId;
		this.createdBy = createdBy;
		this.tenantNum = tenantNum;
		this.tenantName = tenantName;
		this.tenantProjectCode = tenantProjectCode;
	}
}