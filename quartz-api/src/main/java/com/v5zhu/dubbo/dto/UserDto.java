package com.v5zhu.dubbo.dto;

/**
 * Created by zhuxl@paxsz.com on 2016/7/25.
 */
public class UserDto implements java.io.Serializable{
    private Long id;
    private String loginName;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
