package com.shreyas208.databass.api.model;


/**
 * Used to represent Success response JSON file.
 */
public class Success {

    private boolean success;

    public Success(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
