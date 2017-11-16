package com.shreyas208.databass.api.model;


/**
 * Used to represent Login response JSON file.
 */
public class LoginSuccess {

    private boolean success;
    private String email;
    private String displayName;
    private String accessToken;

    public LoginSuccess(boolean success, String email, String displayName, String accessToken) {
        this.success = success;
        this.email = email;
        this.displayName = displayName;
        this.accessToken = accessToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
