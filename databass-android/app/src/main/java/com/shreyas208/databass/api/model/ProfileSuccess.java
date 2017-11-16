package com.shreyas208.databass.api.model;


/**
 * Used to represent Profile response JSON file.
 */
public class ProfileSuccess {

    private boolean success;
    private int joinDateTime;
    private String displayName;
    private int numCitiesVisited;
    private CheckIn[] checkIns;

    public ProfileSuccess(boolean success, int joinDateTime, String displayName, int numCitiesVisited, CheckIn[] checkIns) {
        this.success = success;
        this.joinDateTime = joinDateTime;
        this.displayName = displayName;
        this.numCitiesVisited = numCitiesVisited;
        this.checkIns = checkIns;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getJoinDateTime() {
        return joinDateTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getNumCitiesVisited() {
        return numCitiesVisited;
    }

    public CheckIn[] getCheckIns() {
        return checkIns;
    }

}
