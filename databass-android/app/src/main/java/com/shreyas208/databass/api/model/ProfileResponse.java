package com.shreyas208.databass.api.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Used to represent Profile response JSON file.
 */
public class ProfileResponse {

    private boolean success;
    @SerializedName("error_code") private String errorCode;
    @SerializedName("email_address") private String emailAddress;
    @SerializedName("display_name") private String displayName;
    @SerializedName("join_date") private String joinDate;
    @SerializedName("checkin_count") private int checkinCount;
    @SerializedName("follower_count") private int followerCount;
    @SerializedName("following_count") private int followingCount;
    @SerializedName("recent_checkins") private List<RecentCheckin> recentCheckins;

    public ProfileResponse(boolean success, String errorCode, String displayName, String joinDate, int checkinCount, int followerCount, int followingCount, List<RecentCheckin> recentCheckins) {
        this.success = success;
        this.errorCode = errorCode;
        this.displayName = displayName;
        this.joinDate = joinDate;
        this.checkinCount = checkinCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.recentCheckins = recentCheckins;
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

    public String getJoinDate() {
        return joinDate;
    }

    public int getCheckinCount() {
        return checkinCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public List<RecentCheckin> getRecentCheckins() {
        return recentCheckins;
    }
}
