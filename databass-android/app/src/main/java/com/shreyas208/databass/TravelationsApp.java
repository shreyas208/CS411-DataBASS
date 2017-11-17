package com.shreyas208.databass;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.shreyas208.databass.api.service.TravelationsAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TravelationsApp class. Used to share information across different views and
 * function throughout the app.
 */
public class TravelationsApp extends Application {

    public static final String LOG_TAG = "Travelations";

    private static final String API_BASE_URL = "http://fa17-cs411-18.cs.illinois.edu/";
    private static final String PREFERENCE_FILE = "com.shreyas208.databass.prefs";
    private static final String SP_KEY_USERNAME = "username";
    private static final String SP_KEY_ACCESS_TOKEN = "access_token";
    private static final String SP_KEY_DISPLAY_NAME = "display_name";
    private static final String SP_KEY_EMAIL_ADDRESS = "email_address";

    private String username;
    private String accessToken;
    private String displayName;
    private String emailAddress;

    private static TravelationsAPI api;
    private static SharedPreferences sharedPreferences;

    public static TravelationsAPI getApi() {
        if (api == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            api  = retrofit.create(TravelationsAPI.class);
        }

        return api;
    }

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isLoggedIn()) {

        }
    }

    public boolean isLoggedIn() {
        return !getAccessToken().isEmpty();
    }

    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = getSharedPreferences().getString(SP_KEY_USERNAME, "");
        }
        return getSharedPreferences().getString(SP_KEY_ACCESS_TOKEN, "");
    }

    public String getUsername() {
        if (username == null) {
            username = getSharedPreferences().getString(SP_KEY_USERNAME, "");
        }
        return username;
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = getSharedPreferences().getString(SP_KEY_DISPLAY_NAME, "");
        }
        return displayName;
    }

    public String getEmailAddress() {
        if (emailAddress == null) {
            emailAddress = getSharedPreferences().getString(SP_KEY_EMAIL_ADDRESS, "");
        }
        return emailAddress;
    }

    public void setLoginValues(String username, String accessToken, String emailAddress, String displayName) {
        this.username = username;
        this.accessToken = accessToken;
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        SharedPreferences.Editor spEditor = getSharedPreferences().edit();
        spEditor.putString(SP_KEY_USERNAME, username);
        spEditor.putString(SP_KEY_ACCESS_TOKEN, accessToken);
        spEditor.putString(SP_KEY_EMAIL_ADDRESS, emailAddress);
        spEditor.putString(SP_KEY_DISPLAY_NAME, displayName);
        spEditor.apply();
    }
}
