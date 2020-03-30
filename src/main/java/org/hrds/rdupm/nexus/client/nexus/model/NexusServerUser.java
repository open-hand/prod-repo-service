package org.hrds.rdupm.nexus.client.nexus.model;

import org.hzero.core.util.UUIDUtils;

import java.util.Collections;
import java.util.List;

/**
 * 用户查询返回
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class NexusServerUser {


	/**
	 * 创建仓库默认用户
	 * @param repositoryName 仓库名称（Id）
	 *  @param roleId 角色Id
	 */
	public void createDefUser(String repositoryName, String roleId){
		this.setUserId(this.createDefUserId(repositoryName));
		this.setPassword(this.createDefPassword());
		this.setFirstName(this.getUserId());
		this.setLastName(this.getUserId());
		this.setEmailAddress(this.getUserId() + "@default.com");
		this.setStatus("active");
		this.setRoles(Collections.singletonList(roleId));
	}

	/**
	 * 生成默认用户ID
	 * @param repositoryName 仓库名称（Id）
	 * @return 生成的Id
	 */
	private String createDefUserId(String repositoryName){
		return repositoryName + "-defUser";
	}

	/**
	 * 生成默认密码
	 * @return 生成的密码
	 */
	private String createDefPassword(){
		return UUIDUtils.generateUUID().substring(0, 8);
	}


	private String userId;
	private String password;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String source;
	private String status;
	private String readOnly;
	private List<String> roles;
	private List<String> externalRoles;

	public String getUserId() {
		return userId;
	}

	public NexusServerUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public NexusServerUser setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public NexusServerUser setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public NexusServerUser setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public NexusServerUser setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
	}

	public String getSource() {
		return source;
	}

	public NexusServerUser setSource(String source) {
		this.source = source;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public NexusServerUser setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getReadOnly() {
		return readOnly;
	}

	public NexusServerUser setReadOnly(String readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public List<String> getRoles() {
		return roles;
	}

	public NexusServerUser setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}

	public List<String> getExternalRoles() {
		return externalRoles;
	}

	public NexusServerUser setExternalRoles(List<String> externalRoles) {
		this.externalRoles = externalRoles;
		return this;
	}
}