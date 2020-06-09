package org.hrds.rdupm.harbor.api.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * cxh
 */
@Getter
@Setter
public class IamGroupMemberVO {

    /**
     * 被更改角色的用户的用户名
     */
    private String username;

    /**
     * 项目Id
     */
    private Long resourceId;

    /**
     * 层级  site/organization/project
     */
    private String resourceType;

    /**
     * 权限列表
     */
    private List<String> roleLabels;

    /**
     * 被更改角色的用户的id
     */
    private Long userId;

    private String uuid;
}
