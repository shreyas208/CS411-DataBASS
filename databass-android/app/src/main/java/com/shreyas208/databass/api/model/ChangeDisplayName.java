package com.shreyas208.databass.api.model;

/**
 * Created by Eric on 11/16/2017.
 */

public class ChangeDisplayName {

    private String username;
    private String display_name;
    private String access_token;

    public ChangeDisplayName(String username, String display_name, String access_token) {
        this.username = username;
        this.display_name = display_name;
        this.access_token = access_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
