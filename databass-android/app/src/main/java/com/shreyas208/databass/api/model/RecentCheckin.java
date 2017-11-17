package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shreyas Patil on 2017-11-16.
 */

public class RecentCheckin {
    @SerializedName("city_name") String cityName;
    @SerializedName("checkin_time") String checkinTime;
}
