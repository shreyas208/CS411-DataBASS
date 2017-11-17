package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.ProfileResponse;
import com.shreyas208.databass.api.model.RecentCheckin;
import com.shreyas208.databass.api.service.DoNothingCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Profile Activity class.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, Callback<ProfileResponse> {

    private TravelationsApp app;

    private TextView tvDisplayName, tvUsername, tvJoinDate, tvCheckinCount, tvFollowerCount, tvFollowingCount;
    private RecyclerView rvRecentCheckins;
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
        tvUsername = findViewById(R.id.profile_tv_username);
        tvJoinDate = findViewById(R.id.profile_tv_join_date);
        tvCheckinCount = findViewById(R.id.profile_tv_checkin_count);
        tvFollowerCount = findViewById(R.id.profile_tv_follower_count);
        tvFollowingCount = findViewById(R.id.profile_tv_following_count);
        rvRecentCheckins = findViewById(R.id.profile_rv_recent_checkins);
        btnCheckin = findViewById(R.id.profile_btn_checkin);

        btnCheckin.setOnClickListener(this);
        rvRecentCheckins.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecentCheckins.setAdapter(new RecentCheckinsAdapter());

        tvDisplayName.setText(app.getDisplayName());
        tvUsername.setText(app.getUsername());

        TravelationsApp.getApi().profile(app.getUsername(), app.getAccessToken()).enqueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.profile_menu_logout:
                TravelationsApp.getApi().logout(app.getUsername(), app.getAccessToken()).enqueue(new DoNothingCallback(this.getLocalClassName()));
                app.clearLoginValues();
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    public void onClick(View v) {

    }

    @Override
    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
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
            ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).setRecentCheckins(profileResponse.getRecentCheckins());
            rvRecentCheckins.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<ProfileResponse> call, Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message:\n%s", this.getLocalClassName(), t.getMessage()));
        TravelationsApp.showToast(this, R.string.toast_request_failure);
        finish();
    }

    public class RecentCheckinsAdapter extends RecyclerView.Adapter<RecentCheckinsAdapter.ViewHolder> {

        private List<RecentCheckin> recentCheckins;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvCity, tvTime;

            public ViewHolder(View v) {
                super(v);
                tvCity = v.findViewById(R.id.checkin_item_city_name);
                tvTime = v.findViewById(R.id.checkin_item_time);
            }
        }

        public RecentCheckinsAdapter() {

        }

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
    }

}
