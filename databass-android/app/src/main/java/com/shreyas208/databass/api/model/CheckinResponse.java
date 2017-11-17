package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shreyas Patil on 2017-11-16.
 */

public class CheckinResponse {

    private boolean success;
    @SerializedName("error_code") String errorCode;
    @SerializedName("city_name") String cityName;
    @SerializedName("region_name") String regionName;
    @SerializedName("region_code") String regionCode;
    @SerializedName("country_name") String countryName;
    @SerializedName("country_code") String countryCode;

    public CheckinResponse(boolean success, String errorCode, String cityName, String regionName, String regionCode, String countryName, String countryCode) {
        this.success = success;
        this.errorCode = errorCode;
        this.cityName = cityName;
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
