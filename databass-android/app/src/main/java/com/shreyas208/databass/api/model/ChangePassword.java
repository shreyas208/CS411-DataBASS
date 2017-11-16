package com.shreyas208.databass.api.model;

/**
 * Created by Eric on 11/16/2017.
 */

public class ChangePassword {

    private String username;
    private String old_password;
    private String new_password;
    private String access_token;

    public ChangePassword(String username, String old_password, String new_password, String access_token) {
        this.username = username;
        this.old_password = old_password;
        this.new_password = new_password;
        this.access_token = access_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
