package com.shreyas208.databass.api.model;


/**
 * Used to represent Profile request JSON file.
 */
public class Profile {

    private String username;
    private String accessToken;

    public Profile(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
