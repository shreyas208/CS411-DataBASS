package com.shreyas208.databass.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.CheckinResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, MainActivity.LocationSubscriber {

    private MapView mMapView;
    private GoogleMap mMap;

    private MainActivity mActivity;

    private LinearLayout llCheckin;
    private Button btnCheckin;

    public CheckinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        llCheckin = v.findViewById(R.id.checkin_ll_checkin);
        btnCheckin = v.findViewById(R.id.checkin_btn_checkin);
        btnCheckin.setOnClickListener(this);

        mActivity = (MainActivity) getActivity();
        mActivity.setLocationSubscriber(this);

        mMapView = v.findViewById(R.id.checkin_map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return v;
    }

    @Override
    public void newLocation() {
        attemptShowLocationOnMap();
    }

    private void attemptShowLocationOnMap() {
        Log.i(TravelationsApp.LOG_TAG, "Attempt");
        if (mMap == null || mActivity.getLocation() == null) {
            return;
        }
        LatLng latLng = new LatLng(mActivity.getLocation().getLatitude(), mActivity.getLocation().getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    private void checkin() {
        Location location = mActivity.getLocation();

        if (location == null) {
            return;
        }

        TravelationsApp.getApi().checkin(((MainActivity)getActivity()).getApp().getUsername(), ((MainActivity)getActivity()).getApp().getAccessToken(), location.getLatitude(), location.getLongitude()).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckinResponse> call, @NonNull Response<CheckinResponse> response) {
                CheckinResponse checkinResponse = response.body();
                if (checkinResponse == null) {
                    Log.e(TravelationsApp.LOG_TAG, "ui.CheckinFragment.checkin.onResponse: response body was null");
                    TravelationsApp.showToast(getActivity(), R.string.profile_toast_checkin_failure);
                } else if (!checkinResponse.isSuccess()) {
                    Log.e(TravelationsApp.LOG_TAG, String.format("ui.CheckinFragment.checkin.onResponse: response was unsuccessful, code: %d, message: %s", getActivity().getLocalClassName(), checkinResponse.getErrorCode(), checkinResponse.getErrorCode()));
                    TravelationsApp.showToast(getActivity(), R.string.profile_toast_checkin_failure);
                } else {
                    TravelationsApp.showToast(getActivity(), String.format("Checked in at %s, %s", checkinResponse.getCityName(), checkinResponse.getCountryCode().toUpperCase()));
                }
                setCheckinControlEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CheckinResponse> call, @NonNull Throwable t) {
                Log.e(TravelationsApp.LOG_TAG, String.format("ui.CheckinFragment.checkin.onFailure: request was unsuccessful, message:\n%s", t.getMessage()));
                TravelationsApp.showToast(getActivity(), R.string.toast_request_failure);
                setCheckinControlEnabled(true);
            }
        });
    }

    private void setCheckinControlEnabled(boolean enabled) {
        btnCheckin.setEnabled(enabled);
        llCheckin.setBackgroundColor(getResources().getColor(enabled ? R.color.colorAccentDark : R.color.gray));
        btnCheckin.setText(enabled ? R.string.profile_btn_checkin : R.string.profile_btn_checking_in);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        attemptShowLocationOnMap();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkin_btn_checkin) {
            checkin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mActivity.setLocationSubscriber(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mActivity.removeLocationSubscriber();
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
}
