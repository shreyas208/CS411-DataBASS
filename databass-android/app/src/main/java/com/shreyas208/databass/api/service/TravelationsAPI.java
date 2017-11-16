package com.shreyas208.databass.api.service;


import com.shreyas208.databass.api.model.ChangeDisplayName;
import com.shreyas208.databass.api.model.ChangePassword;
import com.shreyas208.databass.api.model.Login;
import com.shreyas208.databass.api.model.Logout;
import com.shreyas208.databass.api.model.Profile;
import com.shreyas208.databass.api.model.Registration;
import com.shreyas208.databass.api.model.Search;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TravelationsAPI {

    // TODO
    // Implement API functions
    // I recommend defining return types as Call<Object> and then using instanceof
    // in order to see which kind of object is returned, either an error or success

    @POST("/api/user/register")
    Call<Object> register(@Body Registration registration);

    @POST("/api/user/login")
    Call<Object> login(@Body Login login);

    @POST("/api/user/logout")
    Call<Object> logout(@Body Logout logout);

    @POST("/api/user/search")
    Call<Object> search(@Body Search search);

    @POST("/api/user/profile")
    Call<Object> profile(@Body Profile profile);

    @POST("/api/user/changePassword")
    Call<Object> changePassword(@Body ChangePassword changePassword);

    @POST("/api/user/changeDisplayName")
    Call<Object> changeDisplayName(@Body ChangeDisplayName changeDisplayName);

    @POST("/api/user/changeEmailAddress?username={user}&email_address={email}&access_token={token}")
    Call<Object> changeEmailAddress(@Path("user") String user,
                                    @Path("email") String email,
                                    @Path("token") String token);

    @POST("/api/user/checkin?username={user}&access_token={token}&timestamp={time}&latitude={latitude}&longitude={longitude}")
    Call<Object> checkin(@Path("user") String user,
                         @Path("token") String token,
                         @Path("time") String timestamp,
                         @Path("latitude") String latitude,
                         @Path("longitude") String longitude);

    @POST("/api/user/follow?follower_username={follower}&followee_username={followee}&access_token={token}")
    Call<Object> follow(@Path("follower") String follower,
                        @Path("followee") String followee,
                        @Path("token") String token);

    @POST("/api/user/unfollow?follower_username={follower}&followee_username={followee}&access_token={token}")
    Call<Object> unfollow(@Path("follower") String follower,
                        @Path("followee") String followee,
                        @Path("token") String token);

    @POST("/api/user/remove?username={user}&access_token={token}")
    Call<Object> remove(@Path("user") String user,
                        @Path("token") String token);

}
