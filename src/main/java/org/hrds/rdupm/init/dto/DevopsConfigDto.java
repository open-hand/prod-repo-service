package org.hrds.rdupm.init.dto;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/06/05 2:12 下午
 */
@Getter
@Setter
public class DevopsConfigDto {

	private Long appServiceId;

	private Long organizationId;

	private Long projectId;

	private String name;
	//harbor
	private String type;

	private String config;

	private String repoUrl;

	private String loginName;

	private String password;

	private String repoName;

	private String email;

	private String publicFlag;

	public DevopsConfigDto(Long appServiceId, Long organizationId, Long projectId, String name, String type, String config) {
		this.appServiceId = appServiceId;
		this.organizationId = organizationId;
		this.projectId = projectId;
		this.name = name;
		this.type = type;
		this.config = config;

		Map<String,Object> configMap = JSONObject.parseObject(config, Map.class);
		this.repoUrl = configMap.get("url").toString();
		this.loginName = configMap.get("userName").toString();
		this.password = configMap.get("password").toString();
		this.repoName = configMap.get("project").toString();
		this.email = configMap.get("email").toString();
		this.publicFlag = configMap.get("isPrivate").toString();

	}
}
