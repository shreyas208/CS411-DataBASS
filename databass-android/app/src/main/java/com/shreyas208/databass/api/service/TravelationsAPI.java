package com.shreyas208.databass.api.service;


import com.shreyas208.databass.api.model.ChangeDisplayName;
import com.shreyas208.databass.api.model.ChangePassword;
import com.shreyas208.databass.api.model.CheckinResponse;
import com.shreyas208.databass.api.model.GenericResponse;
import com.shreyas208.databass.api.model.LoginResponse;
import com.shreyas208.databass.api.model.Profile;
import com.shreyas208.databass.api.model.Search;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TravelationsAPI {

    // TODO
    // Implement API functions
    // I recommend defining return types as Call<Object> and then using instanceof
    // in order to see which kind of object is returned, either an error or success

    @POST("/api/user/register")
    @FormUrlEncoded
    Call<GenericResponse> register(@Field("username") String username, @Field("password") String password, @Field("email_address") String emailAddress, @Field("display_name") String displayName);

    @POST("/api/user/login")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("username") String username, @Field("password") String password);

    @POST("/api/user/logout")
    @FormUrlEncoded
    Call<GenericResponse> logout(@Field("username") String username, @Field("access_token") String accessToken);

    @POST("/api/user/search")
    @FormUrlEncoded
    Call<Object> search(@Field("username") String username, @Field("access_token") String accessToken, @Field("search_username") String searchUsername);

    @POST("/api/user/profile")
    @FormUrlEncoded
    Call<Object> profile(@Field("username") String username, @Field("access_token") String accessToken);

    @POST("api/user/checkin")
    @FormUrlEncoded
    Call<CheckinResponse> checkin(@Field("username") String username, @Field("access_token") String accessToken, @Field("latitude") double latitude, @Field("longitude") double longitude);

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
