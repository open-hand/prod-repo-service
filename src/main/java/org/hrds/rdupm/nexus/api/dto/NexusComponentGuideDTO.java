package org.hrds.rdupm.nexus.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerComponentInfo;
import org.hrds.rdupm.nexus.infra.util.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 配置指引信息DTO
 * @author weisen.yang@hand-china.com 2020/4/1
 */
@ApiModel("nexus maven-jar依赖配置")
public class NexusComponentGuideDTO extends NexusBaseGuideDTO {


	/**
	 * 值设置
	 * @param componentInfo 包信息
	 */
	public void handleDepGuideValue(NexusServerComponentInfo componentInfo){
		// 拉取配置，包信息
		Map<String, Object> map = new HashMap<>(16);
		map.put("groupId", componentInfo.getGroup());
		map.put("name", componentInfo.getName());
		map.put("version", componentInfo.getVersion());
		map.put("extension", componentInfo.getExtension() == null ? "" : componentInfo.getExtension());
		this.setPullPomDep(VelocityUtils.getJsonString(map, VelocityUtils.POM_DEPENDENCY_FILE_NAME));
	}

	@ApiModelProperty(value = "拉取配置：jar引入")
	private String pullPomDep;

	public String getPullPomDep() {
		return pullPomDep;
	}

	public NexusComponentGuideDTO setPullPomDep(String pullPomDep) {
		this.pullPomDep = pullPomDep;
		return this;
	}
}
