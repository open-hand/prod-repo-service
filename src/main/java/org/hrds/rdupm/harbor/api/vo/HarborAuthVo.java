package org.hrds.rdupm.harbor.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/04/27 5:10 下午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HarborAuthVo {

	@SerializedName("id")
	private Long harborAuthId;

	@SerializedName("project_id")
	private Long harborId;

	@SerializedName("entity_name")
	private String entityName;

	@SerializedName("role_name")
	private String roleName;

	@SerializedName("role_id")
	private Long roleId;

	@SerializedName("entity_id")
	private Long entityId;

	@SerializedName("entity_type")
	private String entityType;
}
