package org.hrds.rdupm.harbor.infra.dto;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/19 17:14
 */
public class UserUpdateDTO {
    private String comment;
    private String email;
    private String realname;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
