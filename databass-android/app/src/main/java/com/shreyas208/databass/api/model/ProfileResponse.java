package com.shreyas208.databass.api.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Used to represent Profile response JSON file.
 */
public class ProfileResponse {

    private boolean success;
    private String errorCode;
    @SerializedName("display_name") private String displayName;
    @SerializedName("join_date") private String joinDate;
    @SerializedName("num_checkins") private int numCheckins;
    @SerializedName("recent_checkins") private List<RecentCheckin> recentCheckins;

    public ProfileResponse(boolean success, String errorCode, String displayName, String joinDate, int numCheckins, List<RecentCheckin> recentCheckins) {
        this.success = success;
        this.errorCode = errorCode;
        this.displayName = displayName;
        this.joinDate = joinDate;
        this.numCheckins = numCheckins;
        this.recentCheckins = recentCheckins;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public int getNumCheckins() {
        return numCheckins;
    }

    public List<RecentCheckin> getRecentCheckins() {
        return recentCheckins;
    }
}
