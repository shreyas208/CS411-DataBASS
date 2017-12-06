package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;


public class RecentCheckin {
    @SerializedName("city_name") private final String cityName;
    @SerializedName("checkin_time") private final String checkinTime;
    @SerializedName("latitude") private final float latitude;
    @SerializedName("longitude") private final float longitude;

    public RecentCheckin(String cityName, String checkinTime, float latitude, float longitude) {
        this.cityName = cityName;
        this.checkinTime = checkinTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return (cityName == null) ? "" : cityName;
    }

    public String getCheckinTime() {
        return (checkinTime == null) ? "" : checkinTime;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
