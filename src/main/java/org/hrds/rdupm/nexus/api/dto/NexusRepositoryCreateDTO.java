package org.hrds.rdupm.nexus.api.dto;

import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.model.*;
import org.hrds.rdupm.nexus.domain.entity.NexusAuth;
import org.hrds.rdupm.nexus.domain.entity.NexusRepository;
import org.hrds.rdupm.nexus.infra.constant.NexusConstants;
import org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants;
import org.hrds.rdupm.nexus.infra.feign.BaseServiceFeignClient;
import org.hrds.rdupm.nexus.infra.feign.vo.LookupVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * maven 仓库创建dto
 * @author weisen.yang@hand-china.com 2020/3/27
 */
@ApiModel("maven 仓库创建")
@Getter
@Setter
public class NexusRepositoryCreateDTO {

	private static final Logger logger = LoggerFactory.getLogger(NexusRepositoryCreateDTO.class);
	/**
	 * url检验
	 */
	public static Pattern URL_PATTERN = Pattern.compile("(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	/**
	 * 仓库名称校验: 仅允许英文、数字、下划线、中划线、点(.)
	 */
    public static Pattern NAME_PATTERN = Pattern.compile("^[\\w\\d\\_\\-\\.]+");


	/**
	 * 默认存储器
	 */
	private static final String BLOB_DEFAULT = "default";

	/**
	 * 参数信息校验
	 * @param baseServiceFeignClient feign
	 * @param validNameSufFlag 是否校验name后缀  true:校验  false:不校验
	 */
	public void validParam(BaseServiceFeignClient baseServiceFeignClient, Boolean validNameSufFlag){

		// 仓库名后缀校验
		if (validNameSufFlag) {
			List<LookupVO> lookupVOList = baseServiceFeignClient.queryCodeValueByCode(NexusConstants.Lookup.REPO_NAME_SUFFIX);
			if (CollectionUtils.isNotEmpty(lookupVOList)) {
				boolean nameSuffixFlag = false;
				for (LookupVO lookupVO : lookupVOList) {
					logger.info("suffix  value: " + lookupVO.getValue());
					if (this.name.toLowerCase().endsWith(lookupVO.getValue().toLowerCase())) {
						nameSuffixFlag = true;
					}
				}
                String nameStr = StringUtils.join(",", lookupVOList.stream().map(LookupVO::getValue).collect(Collectors.toList()));
                if (!nameSuffixFlag) {
					// 仓库名后缀限制为以下数据：{0}
					throw new CommonException(NexusMessageConstants.NEXUS_REPO_NAME_SUFFIX, nameStr);
				}
			}
			if (!NAME_PATTERN.matcher(this.name).matches()) {
				// 仅允许英文、数字、下划线、中划线、点(.)组成
				throw new CommonException(NexusMessageConstants.NEXUS_REPO_NAME_VALID);
			}
		}

		if (this.allowAnonymous == null) {
			throw new CommonException(NexusMessageConstants.NEXUS_ALLOW_ANONYMOUS_NOT_EMPTY);
		}
		switch (this.getType()) {
			case NexusApiConstants.RepositoryType.HOSTED:
				// 创建本地仓库
				if (StringUtils.isBlank(this.versionPolicy) && StringUtils.equals(this.repoType, NexusConstants.RepoType.MAVEN)) {
					throw new CommonException(NexusMessageConstants.NEXUS_VERSION_POLICY_NOT_EMPTY);
				}
				if (StringUtils.isBlank(this.writePolicy)) {
					throw new CommonException(NexusMessageConstants.NEXUS_WRITE_POLICY_NOT_EMPTY);
				}
				break;
			case NexusApiConstants.RepositoryType.PROXY:
				// 创建代理仓库
				/*if (StringUtils.isBlank(this.versionPolicy)) {
					throw new CommonException(NexusMessageConstants.NEXUS_VERSION_POLICY_NOT_EMPTY);
				}*/
				if (StringUtils.isBlank(this.remoteUrl)) {
					throw new CommonException(NexusMessageConstants.NEXUS_REMOTE_URL_NOT_EMPTY);
				}
				if (!URL_PATTERN.matcher(this.remoteUrl).matches()) {
					throw new CommonException(NexusMessageConstants.NEXUS_URL_ERROR);
				}
				if (StringUtils.isNotBlank(this.remoteUsername) && StringUtils.isBlank(this.remotePassword)) {
					throw new CommonException(NexusMessageConstants.NEXUS_REMOTE_USER_PASSWORD_NOT_EMPTY);
				}
				break;
			case NexusApiConstants.RepositoryType.GROUP:
				// 创建仓库组
				if (CollectionUtils.isEmpty(repoMemberList)) {
					throw new CommonException(NexusMessageConstants.NEXUS_REPO_MEMBER_NOT_EMPTY);
				}
				break;
			default:
				throw new CommonException(NexusMessageConstants.NEXUS_MAVEN_REPO_TYPE_ERROR);
		}
	}

	/**
	 * 创建hosted仓库，参数组织
	 * @return RepositoryMavenRequest
	 */
	public RepositoryMavenInfo convertMavenHostedRequest(){
		RepositoryMavenInfo request = new RepositoryMavenInfo();
		request.setName(this.name);
		request.setOnline(Boolean.TRUE);

		RepositoryStorage storage = new RepositoryStorage();
		storage.setBlobStoreName(this.blobStoreName == null ? BLOB_DEFAULT : this.blobStoreName);
		storage.setStrictContentTypeValidation(Boolean.TRUE);
		storage.setWritePolicy(this.writePolicy);
		request.setStorage(storage);

		RepositoryMaven maven = new RepositoryMaven();
		maven.setLayoutPolicy("STRICT");
		maven.setVersionPolicy(this.versionPolicy);
		request.setMaven(maven);

		return request;
	}

	/**
	 * 创建proxy仓库，参数组织
	 * @return RepositoryMavenRequest
	 */
	public NexusServerMavenProxy convertMavenProxyRequest(){
		NexusServerMavenProxy nexusMavenProxy = new NexusServerMavenProxy();
		nexusMavenProxy.setName(this.name);
		nexusMavenProxy.setRemoteUrl(this.remoteUrl);
		nexusMavenProxy.setBlobStoreName(this.blobStoreName == null ? BLOB_DEFAULT : this.blobStoreName);
		nexusMavenProxy.setStrictContentValidation(Boolean.TRUE);
		nexusMavenProxy.setVersionPolicy(this.versionPolicy);
		nexusMavenProxy.setLayoutPolicy("STRICT");
		nexusMavenProxy.setRemoteUsername(this.remoteUsername);
		nexusMavenProxy.setRemotePassword(this.remotePassword);
		return nexusMavenProxy;
	}

	/**
	 * 创建maven仓库组，参数组织
	 * @return NexusMavenGroup
	 */
	public NexusServerMavenGroup convertMavenGroupRequest(){
		NexusServerMavenGroup nexusMavenGroup = new NexusServerMavenGroup();
		nexusMavenGroup.setName(this.name);
		nexusMavenGroup.setBlobStoreName(this.blobStoreName == null ? BLOB_DEFAULT : this.blobStoreName);
		nexusMavenGroup.setMembers(this.getRepoMemberList());
		return nexusMavenGroup;
	}

	@Encrypt
	private Long repositoryId;
	/**
	 * hosted，proxy，group
	 */
	@ApiModelProperty(value = "仓库类型",required = true)
	@NotBlank
	private String type;
	@ApiModelProperty(value = "仓库名称",required = true)
	@NotBlank
	private String name;
	@ApiModelProperty(value = "存储器",required = true)
	private String blobStoreName;

	/**
	 * MIXED, SNAPSHOT, RELEASE
	 */
	@ApiModelProperty(value = "仓库策略")
	private String versionPolicy;
	/**
	 * DENY: 只读   ALLOW_ONCE：禁止覆盖  ALLOW：允许覆盖
	 */
	@ApiModelProperty(value = "版本策略")
	private String writePolicy;
	@ApiModelProperty(value = "是否允许匿名访问")
	private Integer allowAnonymous;

	@ApiModelProperty(value = "创建仓库组（group）时，仓库成员")
	private List<String> repoMemberList;

	@ApiModelProperty(value = "远程仓库地址")
	private String remoteUrl;
	@ApiModelProperty(value = "远程仓库账号")
	private String remoteUsername;
	@ApiModelProperty(value = "远程仓库密码")
	private String remotePassword;

	@ApiModelProperty(value = "仓库分配-仓库管理员用户ID")
	@Encrypt
	private Long distributeRepoAdminId;

	@ApiModelProperty(value = "制品库格式类型： maven2、npm", hidden = true)
	private String format;
	@ApiModelProperty(value = "制品库类型", hidden = true)
	private String repoType;

	@ApiModelProperty(value = "项目Id", hidden = true)
	private Long projectId;
	@ApiModelProperty(value = "组织Id", hidden = true)
	private Long organizationId;

	@ApiModelProperty(value = "用户权限信息", hidden = true)
	private List<NexusAuth> nexusAuthList;
}
