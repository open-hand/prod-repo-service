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
	/**
	 * 不能更改其它项目的仓库
	 */
	String NEXUS_MAVEN_REPO_NOT_CHANGE_OTHER_PRO = "error.nexus.maven.repo.not.update.other.pro";
	/**
	 * 请填写管理员admin之外的用户
	 */
	String NEXUS_RELATED_REPO_NOT_ADMIN = "error.nexus.related.repo.not.admin";
	/**
	 * 仓库列表不能为空
	 */
	String NEXUS_REPO_LIST_NOT_EMPTY = "error.nexus.repo.list.not.empty";
	/**
	 * 用户名或密码错误
	 */
	String NEXUS_USER_AND_PASSWORD_ERROR = "error.nexus.user.and.password.error";
	/**
	 * 仓库{0}已被关联，不能再关联
	 */
	String NEXUS_REPO_ALREADY_RELATED = "error.nexus.repo.already.related";
	/**
	 * 地址填写有误，如：http://www.example.com
	 */
	String NEXUS_URL_ERROR = "error.nexus.url.error";

	/**
	 * 不能删除该仓库下的数据（只能删除自建或已关联仓库下的包）
	 */
	String NEXUS_NOT_DELETE_COMPONENT = "error.nexus.not.delete.component";
	/**
	 * 新密码不能为空
	 */
	String NEXUS_NEW_PASSWORD_NOT_NULL = "error.nexus.new.password.not.null";
	/**
	 * 旧密码不能为空
	 */
	String NEXUS_OLD_PASSWORD_NOT_NULL = "error.nexus.old.password.not.null";
	/**
	 * 不能更改其它仓库管理用户密码
	 */
	String NEXUS_NOT_CHANGE_OTHER_REPO_PWD = "error.nexus.not.change.other.repo.password";
	/**
	 * 原密码填写错误
	 */
	String NEXUS_OLD_PASSWORD_ERROR = "error.nexus.old.password.error";
}
