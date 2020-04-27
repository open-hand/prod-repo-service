package org.hrds.rdupm.harbor.infra.feign.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/03/17 2:29 下午
 */
@Getter
@Setter
public class UserDTO {
	private Long id;

	private String loginName;

	private String email;

	private Long organizationId;

	private String password;

	private String realName;

	private String phone;

	private String imageUrl;

	private String profilePhoto;

	private Boolean isEnabled;

	private Boolean ldap;

	private String language;

	private String timeZone;

	private Date lastPasswordUpdatedAt;

	private Date lastLoginAt;

	private Boolean isLocked;

	private Date lockedUntilAt;

	private Integer passwordAttempt;

	private List<RoleDTO> roles;

}

