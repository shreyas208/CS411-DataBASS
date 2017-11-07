package com.shreyas208.databass.api.model;


/**
 * Used to represent failure and error code JSON file.
 */
public class Error {

    private boolean success;
    private String errorCode;

    public Error(boolean success, String errorCode) {
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
