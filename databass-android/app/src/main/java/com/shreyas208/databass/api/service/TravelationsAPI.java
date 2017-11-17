package com.shreyas208.databass.api.service;


import com.shreyas208.databass.api.model.CheckinResponse;
import com.shreyas208.databass.api.model.GenericResponse;
import com.shreyas208.databass.api.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface TravelationsAPI {

    @POST("/api/user/register")
    @FormUrlEncoded
    Call<GenericResponse> register(@Field("username") String username,
                                   @Field("password") String password,
                                   @Field("email_address") String emailAddress,
                                   @Field("display_name") String displayName);

    @POST("/api/user/login")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);

    @POST("/api/user/logout")
    @FormUrlEncoded
    Call<Void> logout(@Field("username") String username,
                                 @Field("access_token") String accessToken);

    @POST("/api/user/search")
    @FormUrlEncoded
    Call<Object> search(@Field("username") String username,
                        @Field("access_token") String accessToken,
                        @Field("search_username") String searchUsername);

    @POST("/api/user/profile")
    @FormUrlEncoded
    Call<Object> profile(@Field("username") String username,
                         @Field("access_token") String accessToken);

    @POST("api/user/checkin")
    @FormUrlEncoded
    Call<CheckinResponse> checkin(@Field("username") String username,
                                  @Field("access_token") String accessToken,
                                  @Field("latitude") double latitude,
                                  @Field("longitude") double longitude);

    @POST("/api/user/changePassword")
    @FormUrlEncoded
    Call<GenericResponse> changePassword(@Field("username") String username,
                                         @Field("access_token") String accessToken,
                                         @Field("old_password") String oldPassword,
                                         @Field("new_password") String newPassword);

    @POST("/api/user/changeDisplayName")
    @FormUrlEncoded
    Call<GenericResponse> changeDisplayName(@Field("username") String username,
                                            @Field("access_token") String accessToken,
                                            @Field("display_name") String displayName);

    @POST("/api/user/changeEmailAddress")
    @FormUrlEncoded
    Call<GenericResponse> changeEmailAddress(@Field("username") String username,
                                             @Field("access_token") String accessToken,
                                             @Field("email_address") String emailAddress);

    @POST("/api/user/follow")
    @FormUrlEncoded
    Call<Object> follow(@Field("follower_username") String followerUsername,
                        @Field("access_token") String accessToken,
                        @Field("followee_username") String followeeUsername);

    @POST("/api/user/unfollow")
    @FormUrlEncoded
    Call<Object> unfollow(@Field("follower_username") String followerUsername,
                          @Field("access_token") String accessToken,
                          @Field("followee_username") String followeeUsername);

    @POST("/api/user/remove")
    @FormUrlEncoded
    Call<Object> remove(@Field("username") String username,
                        @Field("access_token") String accessToken);

}
