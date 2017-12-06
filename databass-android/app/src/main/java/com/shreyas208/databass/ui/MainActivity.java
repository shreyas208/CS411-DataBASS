package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.service.DoNothingCallback;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TravelationsApp app;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (TravelationsApp) getApplication();

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
            case R.id.profile_menu_logout:
                TravelationsApp.getApi().logout(app.getUsername(), app.getAccessToken()).enqueue(new DoNothingCallback(this.getLocalClassName()));
                app.clearLoginValues();
                Intent i = new Intent(this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.profile_menu_settings:
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
        }
        return true;
    }



    protected TravelationsApp getApp() {
        return app;
    }
}
