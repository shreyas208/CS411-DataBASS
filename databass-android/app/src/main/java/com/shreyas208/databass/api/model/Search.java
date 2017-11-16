package com.shreyas208.databass.api.model;

/**
 * Created by Eric on 11/16/2017.
 */

public class Search {

    private String username;
    private String search_username;
    private String access_token;

    public Search(String username, String search_username, String access_token) {
        this.username = username;
        this.search_username = search_username;
        this.access_token = access_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSearch_username() {
        return search_username;
    }

    public void setSearch_username(String search_username) {
        this.search_username = search_username;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
