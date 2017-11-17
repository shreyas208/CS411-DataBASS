package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shreyas Patil on 2017-11-16.
 */

public class RecentCheckin {
    @SerializedName("city_name") private String cityName;
    @SerializedName("checkin_time") private String checkinTime;

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
