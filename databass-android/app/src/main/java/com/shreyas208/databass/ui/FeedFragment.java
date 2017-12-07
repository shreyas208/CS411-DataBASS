package com.shreyas208.databass.ui;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.FeedResponse;
import com.shreyas208.databass.api.model.GenericResponse;
import com.shreyas208.databass.api.model.RecentCheckin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment implements Callback<FeedResponse>, View.OnClickListener {

    private RecyclerView rvRecentCheckins;
    private EditText etFollowUsername;
    private FloatingActionButton fabFollow;

    private TravelationsApp app;

    public FeedFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        app = ((MainActivity) getActivity()).getApp();
        TravelationsApp.getApi().feed(app.getUsername(), app.getAccessToken()).enqueue(this);
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle("Travelator Feed");

        rvRecentCheckins = getView().findViewById(R.id.feed_rv_checkins);
        rvRecentCheckins.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvRecentCheckins.setAdapter(new RecentCheckinsAdapter(new CheckinClickListener()));

        fabFollow = getView().findViewById(R.id.feed_fab_follow);
        fabFollow.setOnClickListener(this);

        etFollowUsername = getView().findViewById(R.id.feed_et_follow_username);
    }

    private void setFollowUserControlsEnabled(boolean enabled) {
        etFollowUsername.setEnabled(enabled);
        fabFollow.setEnabled(enabled);
    }

    private void attemptFollowUser() {
        setFollowUserControlsEnabled(false);

        String followUsername = etFollowUsername.getText().toString();
        if (followUsername.isEmpty()) {
            setFollowUserControlsEnabled(false);
            TravelationsApp.showToast(getActivity(), R.string.feed_toast_follow_username_empty);
            return;
        }

        TravelationsApp.getApi().follow(app.getUsername(), app.getAccessToken(), followUsername).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.e(TravelationsApp.LOG_TAG, String.format("%s.follow.onFailure: request was unsuccessful, message: %s", "FeedFragment", t.getMessage()));
                TravelationsApp.showToast(getActivity(), R.string.feed_toast_follow_failure);
            }
        });
    }

    @Override
    public void onResponse(@NonNull Call<FeedResponse> call, @NonNull Response<FeedResponse> response) {
        FeedResponse feedResponse = response.body();
        if (feedResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, "ui.FeedFragment.onResponse: response body was null");
            TravelationsApp.showToast(getActivity(), R.string.feed_toast_failure);
        } else if (!feedResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("ui.FeedFragment.onResponse: response was unsuccessful, message: %s", feedResponse.getErrorCode()));
            TravelationsApp.showToast(getActivity(), R.string.feed_toast_failure);
        } else {
            Log.i("LT", String.valueOf(feedResponse.getCheckins().size()));
            ((RecentCheckinsAdapter) rvRecentCheckins.getAdapter()).setRecentCheckins(feedResponse.getCheckins());
            rvRecentCheckins.getAdapter().notifyDataSetChanged();
        }

    }

    @Override
    public void onFailure(@NonNull Call<FeedResponse> call, @NonNull Throwable t) {

        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message: %s", "FeedFragment", t.getMessage()));
        TravelationsApp.showToast(getActivity(), R.string.toast_request_failure);

    }

    public class CheckinClickListener {
        public void onClick(RecentCheckin recentCheckin) {
            ((MainActivity) getActivity()).showProfile(recentCheckin.getUsername());
        }
    }

    public class RecentCheckinsAdapter extends RecyclerView.Adapter<RecentCheckinsAdapter.ViewHolder> {

        private List<RecentCheckin> recentCheckins;
        private CheckinClickListener listener;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvCity;
            public final TextView tvByline;
            public final LinearLayout llCheckin;

            public ViewHolder(View v) {
                super(v);
                tvCity = v.findViewById(R.id.checkin_item_city_name);
                tvByline = v.findViewById(R.id.checkin_item_byline);
                llCheckin = v.findViewById(R.id.checkin_item);
            }
        }

        public RecentCheckinsAdapter(CheckinClickListener listener) {
            this.listener  = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_recent_checkin_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final RecentCheckin recentCheckin = recentCheckins.get(position);
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

            String byline = String.format("<b>%s</> on %s", recentCheckin.getUsername(), readableDate);

            if (Build.VERSION.SDK_INT >= 24) {
                holder.tvCity.setText(Html.fromHtml(cityCountry, 0));
                holder.tvByline.setText(Html.fromHtml(byline, 0));
            } else {
                holder.tvCity.setText(Html.fromHtml(cityCountry));
                holder.tvByline.setText(Html.fromHtml(byline));
            }
            holder.llCheckin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(recentCheckin);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (recentCheckins == null) ? 0 : recentCheckins.size();
        }

        public void setRecentCheckins(List<RecentCheckin> recentCheckins) {
            this.recentCheckins = recentCheckins;
        }

    }

    @Override
    public void onClick(View v) {

    }

}
