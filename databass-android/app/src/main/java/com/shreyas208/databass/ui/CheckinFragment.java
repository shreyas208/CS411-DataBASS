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
public class CheckinFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private MapView mMapView;
    private GoogleMap mMap;

    private Location mLocation;

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

        mMapView = v.findViewById(R.id.checkin_map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        attemptGetLocation();

        return v;
    }

    private void attemptGetLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            continueGetLocation();
        } else {
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_location_denied);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @SuppressLint("MissingPermission")
    private void continueGetLocation() {
        LocationManager locationManager;
        /*if (locationManager == null) {
            Log.i("INFO", "continueGetLocation bad");
            return;
        }
        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.i("INFO", "continueGetLocation pass");*/

        long MIN_TIME_BW_UPDATES = 10000;
        float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;

        try {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            if (locationManager == null) {
                Log.i("INFO", "continueGetLocation locationManager null");
                return;
            }

            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isPassiveEnabled = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {

                Log.i("INFO", "Location not Enabled");

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && mLocation == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS", "GPS Enabled");
                    mLocation = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isPassiveEnabled && mLocation == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    mLocation = locationManager
                            .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }

                if (isNetworkEnabled && mLocation == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    mLocation = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } else {
                Log.i("INFO", "Location Not enabled");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        attemptShowLocationOnMap();
    }

    private void attemptShowLocationOnMap() {
        if (mMap == null || mLocation == null) {
            if (mLocation == null) {
                Log.i("INFO", "mLocation null");
            } else {
                Log.i("INFO", "mMap null");
            }
            return;
        }
        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        Log.i("INFO", "latitude = " + mLocation.getLatitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.i("INFO", "attemptShowLocationOnMap pass");
    }

    private void checkin() {
        if (mLocation == null) {
            return;
        }

        TravelationsApp.getApi().checkin(((MainActivity)getActivity()).getApp().getUsername(), ((MainActivity)getActivity()).getApp().getAccessToken(), mLocation.getLatitude(), mLocation.getLongitude()).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckinResponse> call, @NonNull Response<CheckinResponse> response) {
                CheckinResponse checkinResponse = response.body();
                if (checkinResponse == null) {
                    Log.e(TravelationsApp.LOG_TAG, "ui.ProfileActivity.continueCheckin.onResponse: response body was null");
                    TravelationsApp.showToast(getActivity(), R.string.profile_toast_checkin_failure);
                } else if (!checkinResponse.isSuccess()) {
                    Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", getActivity().getLocalClassName(), response.code(), checkinResponse.getErrorCode()));
                    TravelationsApp.showToast(getActivity(), R.string.profile_toast_checkin_failure);
                } else {
                    TravelationsApp.showToast(getActivity(), String.format("Checked in at %s, %s", checkinResponse.getCityName(), checkinResponse.getCountryCode().toUpperCase()));
                }
                setCheckinControlEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CheckinResponse> call, @NonNull Throwable t) {
                Log.e(TravelationsApp.LOG_TAG, String.format("ui.ProfileActivity.continueCheckin.onFailure: request was unsuccessful, message:\n%s", t.getMessage()));
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
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
