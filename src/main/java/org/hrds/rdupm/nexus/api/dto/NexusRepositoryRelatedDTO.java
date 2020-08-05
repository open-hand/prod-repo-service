package org.hrds.rdupm.nexus.api.dto;

import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.app.service.NexusRepositoryService;
import org.hrds.rdupm.nexus.client.nexus.NexusClient;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServer;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerRepository;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerUser;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.domain.entity.NexusServerConfig;
import org.hrds.rdupm.nexus.domain.repository.NexusRepositoryRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * maven 仓库关联dto
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("maven 仓库关联")
@Getter
@Setter
public class NexusRepositoryRelatedDTO {

	/**
	 * 参数校验
	 */
	public void validParam(NexusClient nexusClient, NexusServerConfig serverConfig, String repoType,
						   NexusRepositoryService nexusRepositoryService, NexusRepositoryRepository nexusRepositoryRepository){

		// 用户校验
		NexusServer nexusServer = new NexusServer(serverConfig.getServerUrl(), this.userName, this.password);
		nexusClient.setNexusServerInfo(nexusServer);
		NexusServerUser nexusExistUser = null;
		try {
			nexusExistUser = nexusClient.getNexusUserApi().getUsers(this.userName);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
				throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_PERMISSIONS);
			}
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				throw new CommonException(NexusMessageConstants.NEXUS_USER_AND_PASSWORD_ERROR);
			}
			throw e;
		}
		// 用户权限校验，需要有admin管理员权限
		Boolean adminFlag = nexusClient.getNexusUserApi().validAdmin(this.userName);
		if (!adminFlag) {
			throw new CommonException(NexusMessageConstants.NEXUS_USER_NOT_PERMISSIONS);
		}

		// 仓库
		if (CollectionUtils.isEmpty(repositoryList)) {
			throw new CommonException(NexusMessageConstants.NEXUS_REPO_LIST_NOT_EMPTY);
		}
		List<String> repoNameList = new ArrayList<>();
		repositoryList.forEach(repositoryItem -> {
			// 是否存在
			NexusServerRepository nexusServerRepository = nexusClient.getRepositoryApi().getRepositoryByName(repositoryItem);
			if (nexusServerRepository == null) {
				repoNameList.add(repositoryItem);
			} else {
				// 是否已有项目关联
				NexusRepository query = new NexusRepository();
				query.setNeRepositoryName(repositoryItem);
				query.setConfigId(serverConfig.getConfigId());
				NexusRepository nexusRepository = nexusRepositoryRepository.selectOne(query);
				if (nexusRepository != null) {
					throw new CommonException(NexusMessageConstants.NEXUS_REPO_ALREADY_RELATED, repositoryItem);
				}
				// 类型是否正确
				if (!nexusServerRepository.getFormat().equals(nexusRepositoryService.convertRepoTypeToFormat(this.repoType))) {
					// 仓库类型错误，{0}仓库不是{1}类型的仓库
					throw new CommonException(NexusMessageConstants.NEXUS_REPO_RELATED_TYPE_ERROR, repositoryItem, this.repoType);
				}
			}
		});
		if (CollectionUtils.isNotEmpty(repoNameList)) {
			throw new CommonException(NexusMessageConstants.NEXUS_REPO_RELATED_NOT_EXIST, StringUtils.join(repoNameList,", "));
		}

	}

	@ApiModelProperty(value = "用户名",required = true)
	@NotBlank
	private String userName;
	@ApiModelProperty(value = "用户名",required = false, hidden = true)
	private String repoType;
	@ApiModelProperty(value = "密码",required = true)
	@NotBlank
	private String password;
	@ApiModelProperty(value = "仓库列表",required = true)
	@NotNull
	private List<String> repositoryList;
}
