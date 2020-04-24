package org.hrds.rdupm.harbor.domain.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.springframework.beans.BeanUtils;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 10:56 上午
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HarborProjectDTO {

	@SerializedName("project_id")
	private Integer harborId;

	/**
	* 项目名称
	* */
	@SerializedName(value = "project_name",alternate = {"name"})
	private String name;

	/**
	 * 项目所有者ID
	 * */
	@SerializedName("owner_id")
	private Integer ownerId;

	/**
	 * 项目所有者登录名
	 * */
	@SerializedName("owner_name")
	private String ownerName;

	/**
	 * 当前用户最高权限角色ID
	 * */
	@SerializedName("current_user_role_id")
	private Integer currentUserRoleId;

	/**
	 * 当前用户权限角色ID数组
	 * */
	@SerializedName("current_user_role_ids")
	private Integer [] currentUserRoleIds;

	/**
	 * 镜像数量
	 * */
	@SerializedName("repo_count")
	private Integer repoCount;

	/**
	 * chart数量
	 * */
	@SerializedName("chart_count")
	private Integer chartCount;

	@SerializedName("update_time")
	private String updateTime;

	@SerializedName("creation_time")
	private String creationTime;

	/**
	* 镜像数量限制
	* */
	@SerializedName("count_limit")
	private Integer countLimit;

	/**
	* 存储容量限制
	* */
	@SerializedName("storage_limit")
	private Integer storageLimit;

	@SerializedName("metadata")
	private HarborMetadataDTO metadata;

	@SerializedName("cve_whitelist")
	private Map<String,Object> cveWhiteList;

	public HarborProjectDTO() {

	}

	public HarborProjectDTO(HarborProjectVo harborProjectVo) {
		this.harborId = harborProjectVo.getHarborId();
		this.countLimit = harborProjectVo.getCountLimit();
		if(!StringUtils.isEmpty(harborProjectVo.getStorageUnit())){
			this.storageLimit = HarborUtil.getStorageLimit(harborProjectVo.getStorageNum(),harborProjectVo.getStorageUnit());
		}
		HarborMetadataDTO metadataDTO = new HarborMetadataDTO();
		BeanUtils.copyProperties(harborProjectVo,metadataDTO);
		this.metadata = metadataDTO;
	}
}
