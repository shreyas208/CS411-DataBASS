package com.shreyas208.databass.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.shreyas208.databass.api.model.ProfileResponse;
import com.shreyas208.databass.api.model.RecentCheckin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements Callback<ProfileResponse> {


    private static final String ARG_USERNAME = "username";
    private String username;

    private TextView tvDisplayName;
    private TextView tvJoinDate;
    private TextView tvCheckinCount;
    private TextView tvFollowerCount;
    private TextView tvFollowingCount;
    private RecyclerView rvRecentCheckins;
    private LinearLayout llCheckin;
    private Button btnCheckin;

    private TravelationsApp app;

    public ProfileFragment() { }

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = ((MainActivity) getActivity()).getApp();
        TravelationsApp.getApi().profile(app.getUsername(), app.getAccessToken(), username).enqueue(this);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvDisplayName = getView().findViewById(R.id.profile_tv_display_name);
        TextView tvUsername = getView().findViewById(R.id.profile_tv_username);
        tvJoinDate = getView().findViewById(R.id.profile_tv_join_date);
        tvCheckinCount = getView().findViewById(R.id.profile_tv_checkin_count);
        tvFollowerCount = getView().findViewById(R.id.profile_tv_follower_count);
        tvFollowingCount = getView().findViewById(R.id.profile_tv_following_count);
        rvRecentCheckins = getView().findViewById(R.id.profile_rv_recent_checkins);
        llCheckin = getView().findViewById(R.id.checkin_ll_checkin);

        rvRecentCheckins.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvRecentCheckins.setAdapter(new RecentCheckinsAdapter());

        tvUsername.setText(username);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
        ProfileResponse profileResponse = response.body();
        if (profileResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, "ui.ProfileFragment.onResponse: response body was null");
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);
        } else if (!profileResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("ui.ProfileFragment.onResponse: response was unsuccessful, message: %s", profileResponse.getErrorCode()));
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);
        } else {
            tvDisplayName.setText(profileResponse.getDisplayName());
            tvJoinDate.setText(profileResponse.getJoinDate());
            tvCheckinCount.setText(String.valueOf(profileResponse.getCheckinCount()));
            tvFollowerCount.setText(String.valueOf(profileResponse.getFollowerCount()));
            tvFollowingCount.setText(String.valueOf(profileResponse.getFollowingCount()));
            ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).setRecentCheckins(profileResponse.getRecentCheckins());
            rvRecentCheckins.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<ProfileResponse> call, Throwable t) {

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
            String cityCounty = recentCheckin.getAccentName() + ", " + recentCheckin.getCountryName();

            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            Date date;
            try {
                date = format.parse(recentCheckin.getCheckinTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            String readableDate = new SimpleDateFormat("EEE, M/d/yyyy 'at' h:mm a", Locale.US).format(date);

            holder.tvCity.setText(cityCounty);
            holder.tvTime.setText(readableDate);
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
