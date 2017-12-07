package com.shreyas208.databass.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.CheckinResponse;
import com.shreyas208.databass.api.model.ProfileResponse;
import com.shreyas208.databass.api.model.RecentCheckin;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Profile Activity class.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, Callback<ProfileResponse> {

    private TravelationsApp app;

    private TextView tvDisplayName;
    private TextView tvJoinDate;
    private TextView tvCheckinCount;
    private TextView tvFollowerCount;
    private TextView tvFollowingCount;
    private RecyclerView rvRecentCheckins;
    private LinearLayout llCheckin;
    private Button btnCheckin;

    /**
     * Creates the Profile Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        app = (TravelationsApp) getApplication();

        tvDisplayName = findViewById(R.id.profile_tv_display_name);
        TextView tvUsername = findViewById(R.id.profile_tv_username);
        tvJoinDate = findViewById(R.id.profile_tv_join_date);
        tvCheckinCount = findViewById(R.id.profile_tv_checkin_count);
        tvFollowerCount = findViewById(R.id.profile_tv_follower_count);
        tvFollowingCount = findViewById(R.id.profile_tv_following_count);
        rvRecentCheckins = findViewById(R.id.profile_rv_recent_checkins);
        llCheckin = findViewById(R.id.checkin_ll_checkin);
        btnCheckin = findViewById(R.id.profile_btn_checkin);

        btnCheckin.setOnClickListener(this);
        rvRecentCheckins.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecentCheckins.setAdapter(new RecentCheckinsAdapter());

        tvDisplayName.setText(app.getDisplayName());
        tvUsername.setText(app.getUsername());

        //TravelationsApp.getApi().profile(app.getUsername(), app.getAccessToken(), ).enqueue(this);
    }

    private void attemptCheckin() {
        setCheckinControlEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            continueCheckin();
        } else {
            setCheckinControlEnabled(true);
            TravelationsApp.showToast(this, R.string.profile_toast_location_denied);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @SuppressLint("MissingPermission")
    private void continueCheckin() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location;
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            return;
        }
        TravelationsApp.getApi().checkin(app.getUsername(), app.getAccessToken(), location.getLatitude(), location.getLongitude()).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckinResponse> call, @NonNull Response<CheckinResponse> response) {
                CheckinResponse checkinResponse = response.body();
                if (checkinResponse == null) {
                    Log.e(TravelationsApp.LOG_TAG, "ui.ProfileActivity.continueCheckin.onResponse: response body was null");
                    TravelationsApp.showToast(ProfileActivity.this, R.string.profile_toast_checkin_failure);
                } else if (!checkinResponse.isSuccess()) {
                    Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", getLocalClassName(), response.code(), checkinResponse.getErrorCode()));
                    TravelationsApp.showToast(ProfileActivity.this, R.string.profile_toast_checkin_failure);
                } else {
                    TravelationsApp.showToast(ProfileActivity.this, String.format("Checked in at %s, %s", checkinResponse.getCityName(), checkinResponse.getCountryCode().toUpperCase()));
                    ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).addCheckin(checkinResponse.getCityName());
                    rvRecentCheckins.getAdapter().notifyDataSetChanged();
                    updateCheckinCount();
                }
                setCheckinControlEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CheckinResponse> call, @NonNull Throwable t) {
                Log.e(TravelationsApp.LOG_TAG, String.format("ui.ProfileActivity.continueCheckin.onFailure: request was unsuccessful, message:\n%s", t.getMessage()));
                TravelationsApp.showToast(ProfileActivity.this, R.string.toast_request_failure);
                setCheckinControlEnabled(true);
            }
        });
    }

    private void updateCheckinCount() {
        tvCheckinCount.setText(String.valueOf(rvRecentCheckins.getAdapter().getItemCount()));
    }

    @Override
    public void onResume() {
        super.onResume();
        tvDisplayName.setText(app.getDisplayName());
    }

    private void setCheckinControlEnabled(boolean enabled) {
        btnCheckin.setEnabled(enabled);
        llCheckin.setBackgroundColor(getResources().getColor(enabled ? R.color.colorAccentDark : R.color.gray));
        btnCheckin.setText(enabled ? R.string.profile_btn_checkin : R.string.profile_btn_checking_in);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_btn_checkin:
                attemptCheckin();
                break;
        }
    }

    @Override
    public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
        ProfileResponse profileResponse = response.body();
        if (profileResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response body was null", getLocalClassName()));
            TravelationsApp.showToast(this, R.string.profile_toast_failure);
            finish();
        } else if (!profileResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", getLocalClassName(), response.code(), profileResponse.getErrorCode()));
            TravelationsApp.showToast(this, R.string.profile_toast_failure);
            finish();
        } else {
            tvCheckinCount.setText(String.valueOf(profileResponse.getCheckinCount()));
            tvFollowerCount.setText(String.valueOf(profileResponse.getFollowerCount()));
            tvFollowingCount.setText(String.valueOf(profileResponse.getFollowingCount()));
            tvJoinDate.setText(profileResponse.getJoinDate());
            ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).setRecentCheckins(profileResponse.getRecentCheckins());
            rvRecentCheckins.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message:\n%s", this.getLocalClassName(), t.getMessage()));
        TravelationsApp.showToast(this, R.string.toast_request_failure);
        finish();
    }

    public class RecentCheckinsAdapter extends RecyclerView.Adapter<RecentCheckinsAdapter.ViewHolder> {

        private List<RecentCheckin> recentCheckins;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvCity;
            public final TextView tvTime;

            public ViewHolder(View v) {
                super(v);
                tvCity = v.findViewById(R.id.checkin_item_city_name);
                tvTime = v.findViewById(R.id.checkin_item_time);
            }
        }

        public RecentCheckinsAdapter() { }

        public RecentCheckinsAdapter(List<RecentCheckin> recentCheckins) {
            this.recentCheckins = recentCheckins;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_recent_checkin_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RecentCheckin recentCheckin = recentCheckins.get(position);
            holder.tvCity.setText(recentCheckin.getCityName());
            holder.tvTime.setText(recentCheckin.getCheckinTime());
        }

        @Override
        public int getItemCount() {
            return (recentCheckins == null) ? 0 : recentCheckins.size();
        }

        public void setRecentCheckins(List<RecentCheckin> recentCheckins) {
            this.recentCheckins = recentCheckins;
        }

        public void addCheckin(String city) {
            recentCheckins.add(0, new RecentCheckin(city, "just now", 0, 0));
        }
    }

}
