package com.bitian.common.dto;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * jwt用户
 * @author Administrator
 *
 */
public class JwtUser{
	
	private long id;
    private String username; //登录名
    private String password; //密码

    public JwtUser() {
	}

    public JwtUser(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
