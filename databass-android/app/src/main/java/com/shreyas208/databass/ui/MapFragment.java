package com.shreyas208.databass.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.ProfileResponse;
import com.shreyas208.databass.api.model.RecentCheckin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, Callback<ProfileResponse> {

    private MapView mMapView;
    private GoogleMap mMap;
    private TravelationsApp app;
    private List<RecentCheckin> checkins;
    private Set<String> cities;
    private List<LatLng> allCoordinates;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = ((MainActivity) getActivity()).getApp();

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Log.i(TravelationsApp.LOG_TAG, "map ready");
        //attemptAddMarkersToMap();

    }

    public void attemptAddMarkersToMap() {

        if (mMap == null || checkins == null) {
            return;
        }

        for (RecentCheckin checkin : checkins) {
            LatLng place = new LatLng(checkin.getLatitude(), checkin.getLongitude());
            mMap.addMarker(new MarkerOptions().position(place).title(checkin.getCityName()));
            Log.i("INFO", "Added city '" + checkin.getCityName() + "' to map");
        }

        mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(allCoordinates).color(Color.argb(150, 128, 90, 203))
                .endCap(new RoundCap())
                .jointType(JointType.ROUND));


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("INFO", "onResume");

        checkins = new ArrayList<>();
        cities = new HashSet<>();
        allCoordinates = new ArrayList<>();

        TravelationsApp.getApi().profile(app.getUsername(), app.getAccessToken(), app.getUsername()).enqueue(this);

        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("INFO", "onStop");

        checkins = null;
        cities = null;
        allCoordinates = null;

        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
        ProfileResponse profileResponse = response.body();
        if (profileResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response body was null", "MapFragment"));
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);
        } else if (!profileResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", "MapFragment", response.code(), profileResponse.getErrorCode()));
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);
        } else {
            for (RecentCheckin checkin : profileResponse.getRecentCheckins()) {
                if (!cities.contains(checkin.getCityName())) {
                    cities.add(checkin.getCityName());
                    checkins.add(checkin);
                }
                allCoordinates.add(new LatLng(checkin.getLatitude(), checkin.getLongitude()));
            }
            attemptAddMarkersToMap();
        }
    }

    @Override
    public void onFailure(@NonNull Call<ProfileResponse> call, Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message:\n%s", "MapFragment", t.getMessage()));
        TravelationsApp.showToast(getActivity(), R.string.toast_request_failure);
    }
}
