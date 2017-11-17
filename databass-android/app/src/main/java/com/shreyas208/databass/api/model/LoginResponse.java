package com.shreyas208.databass.api.model;


import com.google.gson.annotations.SerializedName;

/**
 * Used to represent Login response JSON file.
 */
public class LoginResponse {

    private boolean success;
    @SerializedName("error_code") private String errorCode;
    @SerializedName("email_address") private String emailAddress;
    @SerializedName("display_name") private String displayName;
    @SerializedName("access_token") private String accessToken;

    public LoginResponse(boolean success, String emailAddress, String displayName, String accessToken) {
        this.success = success;
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        this.accessToken = accessToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
