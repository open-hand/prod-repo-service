package org.hrds.rdupm.nexus.client.nexus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色查询返回
 * @author weisen.yang@hand-china.com 2020/3/18
 */
@Getter
@Setter
public class NexusServerRole {
	//     允许，赋予匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
	//     不允许，去除匿名用户权限：nx-repository-view-maven2-[仓库名]-read   nx-repository-view-maven2-[仓库名]-browse
	/**
	 * 仓库发布权限
	 */
	private static final String DEFAULT_PRI = "nx-repository-view-{format}-{repositoryName}-*";

	/**
	 * 拉取权限
	 */
	private static final String DEFAULT_ANONYMOUS_READ = "nx-repository-view-{format}-{repositoryName}-read";
	private static final String DEFAULT_ANONYMOUS_BROWSE = "nx-repository-view-{format}-{repositoryName}-browse";

	public NexusServerRole() {
	}

	public NexusServerRole(String id, String name, String description, List<String> privileges, List<String> roles) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.privileges = privileges;
		this.roles = roles;
	}

	/**
	 * 生成默认发布权限列表
	 * @param repositoryName 仓库名称（Id）
	 * @param format 类型： maven2、npm
	 * @return 描述信息
	 *
	 */

	private List<String> createDefPushPri(String repositoryName, String format){
		List<String> privilegeList = new ArrayList<>();
		privilegeList.add(DEFAULT_PRI.replace("{repositoryName}", repositoryName).replace("{format}", format));
		return privilegeList;
	}

	/**
	 * 添加仓库发布权限
	 * @param repositoryName 仓库名称（Id）
	 * @param format 类型： maven2、npm
	 */
	public void addDefPushPri(String repositoryName, String format) {
		this.privileges.add(DEFAULT_PRI.replace("{repositoryName}", repositoryName).replace("{format}", format));
	}

	/**
	 * 删除仓库发布权限
	 * @param repositoryName 仓库名称（Id）
	 * @param format 类型： maven2、npm
	 */
	public void removeDefPushPri(String repositoryName, String format) {
		this.privileges.remove(DEFAULT_PRI.replace("{repositoryName}", repositoryName).replace("{format}", format));
	}

	/**
	 * 创建仓库默认发布角色
	 * @param repositoryName 仓库名称（Id）
	 * @param pushFlag 是否赋予发布权限
	 * @param id 角色Id
	 * @param format 类型： maven2、npm
	 */
	public void createDefPushRole(String repositoryName, Boolean pushFlag, String id, String format){
		if (id == null) {
			this.setId(repositoryName + "-defRole");
		} else {
			this.setId(id);
		}
		this.setName(this.getId());
		this.setDescription(repositoryName + " 仓库, 默认发布角色");
		if (pushFlag) {
			this.setPrivileges(this.createDefPushPri(repositoryName, format));
		} else {
			this.setPrivileges(new ArrayList<>());
		}

	}

	/**
	 * 创建仓库默认拉取角色
	 * @param repositoryName 仓库名称（Id）
	 * @param id 角色Id
	 * @param format 类型： maven2、npm
	 */
	public void createDefPullRole(String repositoryName, String id, String format){
		if (id == null) {
			this.setId(repositoryName + "-defPullRole");
		} else {
			this.setId(id);
		}
		this.setName(this.getId());
		this.setDescription(repositoryName + " 仓库, 默认拉取角色");
		this.setPrivileges(new ArrayList<>());
		this.setPullPri(repositoryName, 1, format);
	}

	/**
	 * 访问，权限设置
	 * @param repositoryName 仓库列表
	 * @param allowPull  是否允许访问  1：允许  0：不允许
	 * @param format 类型： maven2、npm
	 */
	public void setPullPri(String repositoryName, Integer allowPull, String format){
		String readPri = DEFAULT_ANONYMOUS_READ.replace("{repositoryName}", repositoryName).replace("{format}", format);
		String browsePri = DEFAULT_ANONYMOUS_BROWSE.replace("{repositoryName}", repositoryName).replace("{format}", format);
		if (allowPull == 1) {
			// 允许访问，添加权限
			this.privileges.add(readPri);
			this.privileges.add(browsePri);
		} else if (allowPull == 0){
			// 不允许访问，去除权限
			this.privileges.remove(readPri);
			this.privileges.remove(browsePri);
		}
	}

	/**
	 * 获取匿名访问所需的，权限列表
	 * @return 权限list
	 */
	public static List<String> getAnonymousPrivileges(String repositoryName, String format) {
		String readPri = DEFAULT_ANONYMOUS_READ.replace("{repositoryName}", repositoryName).replace("{format}", format);
		String browsePri = DEFAULT_ANONYMOUS_BROWSE.replace("{repositoryName}", repositoryName).replace("{format}", format);
		List<String> privilegeList = new ArrayList<>();
		privilegeList.add(readPri);
		privilegeList.add(browsePri);
		return privilegeList;
	}

	private String id;
	private String source;
	private String name;
	private String description;
	private List<String> privileges;
	private List<String> roles;

}
