package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;


public class RecentCheckin {
    @SerializedName("city_name") private final String cityName;
    @SerializedName("checkin_time") private final String checkinTime;

    public RecentCheckin(String cityName, String checkinTime) {
        this.cityName = cityName;
        this.checkinTime = checkinTime;
    }

    public String getCityName() {
        return (cityName == null) ? "" : cityName;
    }

    public String getCheckinTime() {
        return (checkinTime == null) ? "" : checkinTime;
    }
}
