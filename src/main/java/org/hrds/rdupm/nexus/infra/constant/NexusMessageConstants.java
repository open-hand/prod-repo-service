package org.hrds.rdupm.nexus.infra.constant;

/**
 * @author weisen.yang@hand-china.com 2020/3/19
 */
public interface NexusMessageConstants {
	/**
	 * 请选择文件上传
	 */
	String NEXUS_SELECT_FILE = "error.nexus.not.select.file";

	/**
	 * 文件类型错误，请选择jar或xml类型文件
	 */
	String NEXUS_FILE_TYPE_ERROR = "error.nexus.file.type";

	/**
	 * nexus服务生效的配置有多个，请联系管理员检查
	 */
	String NEXUS_SERVER_CONFIG_MUL = "error.nexus.server.config.multi";

	/**
	 * nexus服务信息未配置，请联系管理员配置
	 */
	String NEXUS_SERVER_INFO_NOT_CONFIG = "error.nexus.server.info.not.config";

	/**
	 * 仓库名后缀限制为以下数据：{0}
	 */
	String NEXUS_REPO_NAME_SUFFIX = "error.nexus.repo.name.suffix";

	/**
	 * 是否允许匿名访问不能为空
	 */
	String NEXUS_ALLOW_ANONYMOUS_NOT_EMPTY = "error.nexus.allow.anonymous.not.empty";
	/**
	 * 仓库策略不能为空
	 */
	String NEXUS_VERSION_POLICY_NOT_EMPTY = "error.nexus.version.policy.not.empty";
	/**
	 * 版本策略不能为空
	 */
	String NEXUS_WRITE_POLICY_NOT_EMPTY = "error.nexus.write.policy.not.empty";
	/**
	 * 远程仓库地址不能为空
	 */
	String NEXUS_REMOTE_URL_NOT_EMPTY = "error.nexus.remote.url.not.empty";
	/**
	 * 填写了远程仓库账号，必须填写账号密码
	 */
	String NEXUS_REMOTE_USER_PASSWORD_NOT_EMPTY = "error.nexus.remote.user.password.not.empty";
	/**
	 * 仓库组成员不能为空
	 */
	String NEXUS_REPO_MEMBER_NOT_EMPTY = "error.nexus.repo.member.not.empty";
	/**
	 * 仓库类型错误
	 */
	String NEXUS_MAVEN_REPO_TYPE_ERROR = "error.nexus.maven.repo.type.error";
}
