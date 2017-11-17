package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shreyas Patil on 2017-11-16.
 */

public class GenericResponse {
    private boolean success;
    @SerializedName("error_code") private String errorCode;

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
