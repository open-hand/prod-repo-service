package org.hrds.rdupm.nexus.api.dto;

import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * maven 仓库关联dto
 * @author weisen.yang@hand-china.com 2020/3/30
 */
@ApiModel("maven 仓库关联")
public class NexusRepositoryRelatedDTO {
	private static final String ADMIN = "admin";

	/**
	 * 参数校验
	 */
	public void validParam(){
		if (this.userName.equals(ADMIN)) {
			throw new CommonException(NexusMessageConstants.NEXUS_RELATED_REPO_NOT_ADMIN);
		}
		if (CollectionUtils.isEmpty(repositoryList)) {
			throw new CommonException(NexusMessageConstants.NEXUS_REPO_LIST_NOT_EMPTY);
		}
	}

	@ApiModelProperty(value = "用户名",required = true)
	@NotBlank
	private String userName;
	@ApiModelProperty(value = "密码",required = true)
	@NotBlank
	private String password;
	@ApiModelProperty(value = "仓库列表",required = true)
	@NotNull
	private List<String> repositoryList;

	public String getUserName() {
		return userName;
	}

	public NexusRepositoryRelatedDTO setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public NexusRepositoryRelatedDTO setPassword(String password) {
		this.password = password;
		return this;
	}

	public List<String> getRepositoryList() {
		return repositoryList;
	}

	public NexusRepositoryRelatedDTO setRepositoryList(List<String> repositoryList) {
		this.repositoryList = repositoryList;
		return this;
	}
}
