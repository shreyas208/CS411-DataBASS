package com.shreyas208.databass.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.Achievement;
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
    private TextView tvScore;
    private RecyclerView rvAchievements;
    private RecyclerView rvRecentCheckins;

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
        tvScore = getView().findViewById(R.id.profile_tv_score);
        rvAchievements = getView().findViewById(R.id.profile_rv_achievements);
        rvRecentCheckins = getView().findViewById(R.id.profile_rv_recent_checkins);

        rvAchievements.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvAchievements.setAdapter(new AchievementsAdapter(new AchievementClickListener(getContext())));

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
    public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {

        ProfileResponse profileResponse = response.body();

        if (profileResponse == null) {

            Log.e(TravelationsApp.LOG_TAG, "ui.ProfileFragment.onResponse: response body was null");
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);

        } else if (!profileResponse.isSuccess()) {

            Log.e(TravelationsApp.LOG_TAG, String.format("ui.ProfileFragment.onResponse: response was unsuccessful, message: %s", profileResponse.getErrorCode()));
            TravelationsApp.showToast(getActivity(), R.string.profile_toast_failure);

        } else {

            tvDisplayName.setText(profileResponse.getDisplayName());

            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy", Locale.US);
            String readableDate = "Travelator since ";

            try {
                readableDate += outputFormat.format(inputFormat.parse(profileResponse.getJoinDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                readableDate += "";
            }

            tvJoinDate.setText(readableDate);
            tvCheckinCount.setText(String.valueOf(profileResponse.getCheckinCount()));
            tvFollowerCount.setText(String.valueOf(profileResponse.getFollowerCount()));
            tvFollowingCount.setText(String.valueOf(profileResponse.getFollowingCount()));
            tvScore.setText(String.valueOf(profileResponse.getScore()));

            ((AchievementsAdapter) rvAchievements.getAdapter()).setAchievements(profileResponse.getAchievements());
            rvAchievements.getAdapter().notifyDataSetChanged();

            ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).setRecentCheckins(profileResponse.getRecentCheckins());
            rvRecentCheckins.getAdapter().notifyDataSetChanged();

        }
        
    }

    @Override
    public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {

        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message: %s", "ProfileFragment", t.getMessage()));
        TravelationsApp.showToast(getActivity(), R.string.toast_request_failure);

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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_recent_checkin_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RecentCheckin recentCheckin = recentCheckins.get(position);
            String cityCountry = String.format("<b>%s</b>, %s", recentCheckin.getAccentName(), recentCheckin.getCountryName());

            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, M/d/yyyy 'at' h:mm a", Locale.US);
            String readableDate;

            try {
                readableDate = outputFormat.format(inputFormat.parse(recentCheckin.getCheckinTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                readableDate = "";
            }

            if (Build.VERSION.SDK_INT >= 24) {
                holder.tvCity.setText(Html.fromHtml(cityCountry, 0));
            } else {
                holder.tvCity.setText(Html.fromHtml(cityCountry));
            }
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

    public class AchievementClickListener {
        private final Context context;

        public AchievementClickListener(final Context context) {
            this.context = context;
        }

        public void onClick(Achievement achievement) {
            TravelationsApp.showToast(context, achievement.getTitle() + ": " + achievement.getDescription());
        }
    }

    public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {

        private List<Achievement> achievements;
        private AchievementClickListener listener;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView ivIcon;

            public ViewHolder(View v) {
                super(v);
                ivIcon = v.findViewById(R.id.achievement_item_icon);
            }
        }

        public AchievementsAdapter() { }

        public AchievementsAdapter(AchievementClickListener listener) {
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_achievement_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Achievement achievement = achievements.get(position);
            holder.ivIcon.setImageDrawable(getActivity().getResources().getDrawable(achievement.getDrawable()));
            holder.ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(achievement);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (achievements == null) ? 0 : achievements.size();
        }

        public void setAchievements(List<Achievement> achievements) {
            this.achievements = achievements;
        }

    }

}
