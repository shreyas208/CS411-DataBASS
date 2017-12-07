package com.shreyas208.databass.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.service.DoNothingCallback;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {

    private TravelationsApp app;

    private FusedLocationProviderClient  mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private LocationSubscriber mLocationSubscriber;

    private BottomNavigationView bottomNav;

    public interface LocationSubscriber {
        void newLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (TravelationsApp) getApplication();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null && (location.getAccuracy() > mLocation.getAccuracy() || location.getTime() - mLocation.getTime() > 5*1000)) {
                        // new location is more accurate or more than 5 minutes newer
                        setLocation(location);
                    }
                }
            }
        };
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30*1000)
                .setSmallestDisplacement(10)
                .setInterval(1000)
                .setNumUpdates(3);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        setLocation(location);
                    }
                }
            });
        } else {
            TravelationsApp.showToast(this, R.string.toast_location_denied);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        bottomNav = findViewById(R.id.main_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new CheckinFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_logout:
                TravelationsApp.getApi().logout(app.getUsername(), app.getAccessToken()).enqueue(new DoNothingCallback(this.getLocalClassName()));
                app.clearLoginValues();
                Intent i = new Intent(this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.main_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_checkin) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new CheckinFragment()).commit();
        } else if (item.getItemId() == R.id.nav_profile) {
            ProfileFragment fragment = ProfileFragment.newInstance(app.getUsername());
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        } else if (item.getItemId() == R.id.nav_map) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new MapFragment()).commit();
        } else if (item.getItemId() == R.id.nav_feed) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new FeedFragment()).commit();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } else {
            TravelationsApp.showToast(this, R.string.toast_location_denied);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setLocation(Location location) {
        mLocation = location;
        if (mLocationSubscriber != null) {
            mLocationSubscriber.newLocation();
        }
    }

    protected Location getLocation() {
        return mLocation;
    }

    protected void setLocationSubscriber(LocationSubscriber locationSubscriber) {
        mLocationSubscriber = locationSubscriber;
        if (mLocation != null) {
            locationSubscriber.newLocation();
        }
    }

    protected void showProfile(String username) {
        ProfileFragment fragment = ProfileFragment.newInstance(username);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack(null).commit();
    }

    protected void removeLocationSubscriber() {
        mLocationSubscriber = null;
    }

    protected TravelationsApp getApp() {
        return app;
    }
}
