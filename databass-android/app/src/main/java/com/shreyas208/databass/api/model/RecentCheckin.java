package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;


public class RecentCheckin {
    @SerializedName("city_name") private final String cityName;
    @SerializedName("accent_name") private final String accentName;
    @SerializedName("country_name") private final String countryName;
    @SerializedName("checkin_time") private final String checkinTime;
    @SerializedName("latitude") private final float latitude;
    @SerializedName("longitude") private final float longitude;

    public RecentCheckin(String cityName, String accentName, String countryName, String checkinTime, float latitude, float longitude) {
        this.cityName = cityName;
        this.accentName = accentName;
        this.countryName = countryName;
        this.checkinTime = checkinTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return (cityName == null) ? "" : cityName;
    }

    public String getAccentName() {
        return accentName;
    }

    public String getCountryName() {
        return countryName;
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
