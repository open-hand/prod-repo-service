package org.hrds.rdupm.init.dto;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.hrds.rdupm.util.DESEncryptUtil;

/**
 * description
 *
 * @author chenxiuhong 2020/06/05 2:12 下午
 */
@Getter
@Setter
public class DevopsConfigDto {

	private Long id;

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

	public DevopsConfigDto(){

	}

	public DevopsConfigDto(Long appServiceId, Long organizationId, Long projectId, String name, String type, String config) {
		this.appServiceId = appServiceId;
		this.organizationId = organizationId;
		this.projectId = projectId;
		this.name = name;
		this.type = type;
		this.config = config;
	}

	public void parseConfig(){
		Map<String,Object> configMap = JSONObject.parseObject(config, Map.class);
		this.repoUrl = configMap.get("url").toString();
		this.loginName = configMap.get("userName").toString();
		this.password = DESEncryptUtil.encode(configMap.get("password").toString());
		if(configMap.get("project") != null){
			this.repoName = configMap.get("project").toString();
		}else {
			this.repoName = "project_harbor_default";
		}
		this.email = configMap.get("email").toString();
		if(configMap.get("isPrivate") != null){
			String isPrivate = configMap.get("isPrivate").toString();
			this.publicFlag = "true".equals(isPrivate) ? "false" : "true";
		}else {
			this.publicFlag = "false";
		}
	}
}
