package org.hrds.rdupm.nexus.client.nexus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色查询返回
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class NexusServerRole {
	//     允许，赋予匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
	//     不允许，去除匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
	/**
	 * 仓库默认角色权限
	 */
	private static final String DEFAULT_PRI = "nx-repository-view-maven2-{repositoryName}-*";

	/**
	 * 匿名访问角色权限
	 */
	private static final String DEFAULT_ANONYMOUS_READ = "nx-repository-view-maven2-{repositoryName}-read";
	private static final String DEFAULT_ANONYMOUS_BROWSE = "nx-repository-view-maven2-{repositoryName}-browse";

	/**
	 * 生成默认角色ID
	 * @param repositoryName 仓库名称（Id）
	 * @return 生成的Id
	 */
	private String createDefaultId(String repositoryName){
		return repositoryName + "-defRole";
	}

	/**
	 * 生成默认角色，描述
	 * @param repositoryName 仓库名称（Id）
	 * @return 描述信息
	 */
	private String createDefDesc(String repositoryName){
		return repositoryName + " 仓库, 默认管理角色";
	}

	/**
	 * 生成默认权限列表
	 * @param repositoryName 仓库名称（Id）
	 * @return 描述信息
	 */
	private List<String> createDefPri(String repositoryName){
		List<String> privilegeList = new ArrayList<>();
		privilegeList.add(DEFAULT_PRI.replace("{repositoryName}", repositoryName));
		return privilegeList;
	}

	/**
	 * 创建仓库默认角色
	 * @param repositoryName 仓库名称（Id）
	 */
	public void createDefRole(String repositoryName){
		this.setId(this.createDefaultId(repositoryName));
		this.setName(this.getId());
		this.setDescription(this.createDefDesc(repositoryName));
		this.setPrivileges(this.createDefPri(repositoryName));
	}

	/**
	 * 匿名访问，权限设置
	 * @param repositoryName 仓库列表
	 * @param allowAnonymous  是否允许匿名访问
	 */
	public void setAnonymousPri(String repositoryName, Integer allowAnonymous){
		String readPri = DEFAULT_ANONYMOUS_READ.replace("{repositoryName}", repositoryName);
		String browsePri = DEFAULT_ANONYMOUS_BROWSE.replace("{repositoryName}", repositoryName);
		if (allowAnonymous == 1) {
			// 允许匿名访问，添加权限
			this.privileges.add(readPri);
			this.privileges.add(browsePri);
		} else if (allowAnonymous == 0){
			// 不允许匿名访问，去除权限
			this.privileges.remove(readPri);
			this.privileges.remove(browsePri);
		}
	}

	private String id;
	private String source;
	private String name;
	private String description;
	private List<String> privileges;
	private List<String> roles;

	public String getId() {
		return id;
	}

	public NexusServerRole setId(String id) {
		this.id = id;
		return this;
	}

	public String getSource() {
		return source;
	}

	public NexusServerRole setSource(String source) {
		this.source = source;
		return this;
	}

	public String getName() {
		return name;
	}

	public NexusServerRole setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public NexusServerRole setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<String> getPrivileges() {
		return privileges;
	}

	public NexusServerRole setPrivileges(List<String> privileges) {
		this.privileges = privileges;
		return this;
	}

	public List<String> getRoles() {
		return roles;
	}

	public NexusServerRole setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}
}
