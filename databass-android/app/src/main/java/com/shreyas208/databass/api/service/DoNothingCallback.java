package com.shreyas208.databass.api.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.shreyas208.databass.TravelationsApp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class DoNothingCallback implements Callback<Void> {

    private final String logPrefix;

    public DoNothingCallback(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    @Override
    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
        if (!response.isSuccessful()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d", logPrefix, response.code()));
        }
    }

    @Override
    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful", logPrefix));
    }
}
