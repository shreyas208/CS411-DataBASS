package com.shreyas208.databass.api.model;


import com.google.gson.annotations.SerializedName;

/**
 * Used to represent Login response JSON file.
 */
public class LoginResponse {

    private final boolean success;
    @SerializedName("error_code") private final String errorCode;
    @SerializedName("email_address") private final String emailAddress;
    @SerializedName("display_name") private final String displayName;
    @SerializedName("access_token") private final String accessToken;

    public LoginResponse(boolean success, String errorCode, String emailAddress, String displayName, String accessToken) {
        this.success = success;
        this.errorCode = errorCode;
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
