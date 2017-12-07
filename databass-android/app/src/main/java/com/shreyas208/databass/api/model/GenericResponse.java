package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;
import com.shreyas208.databass.R;


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

    public int getErrorString() {

        switch (errorCode) {

            case "user_register_bad_credentials":
                return R.string.register_toast_bad_credentials;
            case "user_register_invalid_password":
                return R.string.register_toast_invalid_password;
            case "user_register_invalid_email":
                return R.string.register_toast_invalid_email;
            case "user_register_invalid_display_name":
                return R.string.register_toast_invalid_display_name;
            case "user_register_username_in_use":
                return R.string.register_toast_username_in_use;
            case "user_register_empty_field":
                return R.string.register_toast_empty_field;
            default:
                return R.string.toast_failure;

        }

    }
}
