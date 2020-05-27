package org.hrds.rdupm.nexus.api.dto;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusUser;
import org.hrds.rdupm.nexus.infra.util.VelocityUtils;
import org.hrds.rdupm.util.DESEncryptUtil;
import org.hzero.core.base.BaseConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置指引信息DTO
 * @author weisen.yang@hand-china.com 2020/4/1
 */
@ApiModel("nexus maven-仓库配置")
public class NexusGuideDTO extends NexusBaseGuideDTO{
	public static final String PUSH_CMD = "mvn clean deploy -DskipTests";




	/**
	 * 值设置
	 * @param nexusServerRepository nexus服务,仓库信息
	 * @param nexusRepository 数据库表，仓库信息
	 * @param nexusUser 仓库默认管理用户
	 * @param showPushFlag 是否返回发布的配置信息  true:返回  false:不反回
	 */
	public void handlePushGuideValue(NexusServerRepository nexusServerRepository,
									 NexusRepository nexusRepository,
									 NexusUser nexusUser,
									 Boolean showPushFlag){
		Map<String, Object> map = new HashMap<>(16);
		map.put("versionPolicy", nexusServerRepository.getVersionPolicy());
		map.put("repositoryName", nexusServerRepository.getName());
		map.put("url", nexusServerRepository.getUrl());
		map.put("type", nexusServerRepository.getType());

		// 发布信息
		if (nexusServerRepository.getType().equals(NexusApiConstants.RepositoryType.GROUP)
				|| nexusServerRepository.getType().equals(NexusApiConstants.RepositoryType.PROXY)) {
			// group 与 proxy 不需要
			this.setShowPushFlag(false);
		} else {
			this.setShowPushFlag(showPushFlag);
			if (showPushFlag) {
				// 为true时，处理发布的信息
				if (nexusUser == null) {
					throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
				}

				//String neUserPassword = DESEncryptUtil.decode(nexusUser.getNeUserPassword());

				//map.put("username", );
				//this.setPushPassword(neUserPassword);
				this.setPushServerInfo(VelocityUtils.getJsonString(map, VelocityUtils.SET_SERVER_FILE_NAME));
				this.setPushServerInfoPassword(this.getPushServerInfo());

				this.setPushPomManageInfo(VelocityUtils.getJsonString(map, VelocityUtils.POM_MANGE_FILE_NAME));
				this.setPushCmd(NexusGuideDTO.PUSH_CMD);
			}
		}
	}


	@ApiModelProperty(value = "发布配置：server配置信息")
	private String pushServerInfo;
	@ApiModelProperty(value = "发布配置：server配置信息(包含密码)")
	private String pushServerInfoPassword;
	@ApiModelProperty(value = "发布配置：密码")
	private String pushPassword;
	@ApiModelProperty(value = "发布配置：pom文件，仓库配置")
	private String pushPomManageInfo;
	@ApiModelProperty(value = "发布配置：运行命令")
	private String pushCmd;

	public String getPushServerInfo() {
		return pushServerInfo;
	}

	public NexusGuideDTO setPushServerInfo(String pushServerInfo) {
		this.pushServerInfo = pushServerInfo;
		return this;
	}

	public String getPushServerInfoPassword() {
		return pushServerInfoPassword;
	}

	public NexusGuideDTO setPushServerInfoPassword(String pushServerInfoPassword) {
		this.pushServerInfoPassword = pushServerInfoPassword;
		return this;
	}

	public String getPushPassword() {
		return pushPassword;
	}

	public NexusGuideDTO setPushPassword(String pushPassword) {
		this.pushPassword = pushPassword;
		return this;
	}

	public String getPushPomManageInfo() {
		return pushPomManageInfo;
	}

	public NexusGuideDTO setPushPomManageInfo(String pushPomManageInfo) {
		this.pushPomManageInfo = pushPomManageInfo;
		return this;
	}

	public String getPushCmd() {
		return pushCmd;
	}

	public NexusGuideDTO setPushCmd(String pushCmd) {
		this.pushCmd = pushCmd;
		return this;
	}
}
