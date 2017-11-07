package com.shreyas208.databass.api.model;


/**
 * Used to represent a user check in. Unsure what the variables should
 * be here though, so currently only have timestamp and city.
 */
public class CheckIn {

    private int timestamp;
    private String city;

    public CheckIn(int timestamp, String city) {
        this.timestamp = timestamp;
        this.city = city;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
