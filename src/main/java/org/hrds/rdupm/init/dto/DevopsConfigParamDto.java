package org.hrds.rdupm.init.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author chenxiuhong 2020/06/05 2:14 下午
 */
@Getter
@Setter
public class DevopsConfigParamDto {

	private String url;

	private String userName;

	private String password;

	private String project;

	private String email;

	private Boolean isPrivate;

}
