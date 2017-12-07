package com.shreyas208.databass.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        }
    }

    @Override
    public void onFailure(Call<ProfileResponse> call, Throwable t) {

    }

}
