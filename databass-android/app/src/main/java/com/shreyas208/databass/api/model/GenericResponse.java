package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;


public class GenericResponse {
    private final boolean success;
    @SerializedName("error_code") private final String errorCode;

    public GenericResponse(boolean success, String errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
