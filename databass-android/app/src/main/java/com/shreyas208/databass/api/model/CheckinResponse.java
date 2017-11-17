package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;



public class CheckinResponse {

    private final boolean success;
    @SerializedName("error_code")
    private final String errorCode;
    @SerializedName("city_name")
    private final String cityName;
    @SerializedName("region_name")
    private final String regionName;
    @SerializedName("region_code")
    private final String regionCode;
    @SerializedName("country_name")
    private final String countryName;
    @SerializedName("country_code")
    private final String countryCode;

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
