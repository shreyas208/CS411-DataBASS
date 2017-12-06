package com.shreyas208.databass.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.ProfileResponse;
import com.shreyas208.databass.api.model.RecentCheckin;

import java.util.List;

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

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = ((MainActivity) getActivity()).getApp();
        TravelationsApp.getApi().profile(app.getUsername(), app.getAccessToken()).enqueue(this);

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
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        if (checkins != null) {
            for (RecentCheckin checkin : checkins) {
                LatLng place = new LatLng(checkin.getLatitude(), checkin.getLongitude());
                mMap.addMarker(new MarkerOptions().position(place).title(checkin.getCityName()));
            }
        } else {
            Log.e("INFO", "checkins null");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
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
            checkins = profileResponse.getRecentCheckins();
        }
    }

    @Override
    public void onFailure(@NonNull Call<ProfileResponse> call, Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message:\n%s", "MapFragment", t.getMessage()));
        TravelationsApp.showToast(getActivity(), R.string.toast_request_failure);
    }
}
