package com.shreyas208.databass.api.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Used to represent Profile response JSON file.
 */
public class FeedResponse {

    private final boolean success;
    @SerializedName("error_code") private final String errorCode;
    @SerializedName("email_address") private String emailAddress;
    @SerializedName("display_name") private final String displayName;
    @SerializedName("join_date") private final String joinDate;
    @SerializedName("checkin_count") private final int checkinCount;
    @SerializedName("score") private final int score;
    @SerializedName("follower_count") private final int followerCount;
    @SerializedName("following_count") private final int followingCount;
    @SerializedName("achievements") private final List<Achievement> achievements;
    @SerializedName("checkins") private final List<RecentCheckin> checkins;

    public FeedResponse(boolean success, String errorCode, String displayName, String joinDate, int checkinCount, int score, int followerCount, int followingCount, List<Achievement> achievements, List<RecentCheckin> checkins) {
        this.success = success;
        this.errorCode = errorCode;
        this.displayName = displayName;
        this.joinDate = joinDate;
        this.checkinCount = checkinCount;
        this.score = score;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.achievements = achievements;
        this.checkins = checkins;
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

    public int getScore() {
        return score;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public List<RecentCheckin> getCheckins() {
        return checkins;
    }
}
