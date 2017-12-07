package com.shreyas208.databass.api.model;

import com.google.gson.annotations.SerializedName;
import com.shreyas208.databass.R;

/**
 * Created by Shreyas Patil on 2017-12-07.
 */

public class Achievement {
    @SerializedName("description") private final String description;
    @SerializedName("id") private final String id;
    @SerializedName("points") private final String points;
    @SerializedName("title") private final String title;

    public Achievement(String description, String id, String points, String title) {
        this.description = description;
        this.id = id;
        this.points = points;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public int getDrawable() {
        if (id.equals("5_stars")) {
            return R.drawable.five_stars;
        } else if (id.equals("frequent_traveler")) {
            return R.drawable.frequent_traveler;
        } else if (id.equals("just_getting_started")) {
            return R.drawable.just_getting_started;
        } else if (id.equals("nomad")) {
            return R.drawable.nomad;
        } else if (id.equals("no_more_rookie_numbers")) {
            return R.drawable.no_more_rookie_numbers;
        } else if (id.equals("on_your_way")) {
            return R.drawable.on_your_way;
        } else if (id.equals("serial_stalker")) {
            return R.drawable.serial_stalker;
        } else if (id.equals("stalker")) {
            return R.drawable.stalker;
        } else if (id.equals("welcome")) {
            return R.drawable.welcome;
        } else if (id.equals("you_get_around")) {
            return R.drawable.you_get_around;
        } else {
            return R.drawable.five_stars;
        }
    }
}
