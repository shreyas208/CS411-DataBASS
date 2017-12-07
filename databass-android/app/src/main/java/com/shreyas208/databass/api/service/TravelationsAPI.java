package com.shreyas208.databass.api.service;


import com.shreyas208.databass.api.model.CheckinResponse;
import com.shreyas208.databass.api.model.FeedResponse;
import com.shreyas208.databass.api.model.GenericResponse;
import com.shreyas208.databass.api.model.LoginResponse;
import com.shreyas208.databass.api.model.ProfileResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface TravelationsAPI {

    @POST("user/register")
    @FormUrlEncoded
    Call<GenericResponse> register(@Field("username") String username,
                                   @Field("password") String password,
                                   @Field("email_address") String emailAddress,
                                   @Field("display_name") String displayName);

    @POST("user/login")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);

    @POST("user/logout")
    @FormUrlEncoded
    Call<Void> logout(@Field("username") String username,
                                 @Field("access_token") String accessToken);

    @POST("user/search")
    @FormUrlEncoded
    Call<Object> search(@Field("username") String username,
                        @Field("access_token") String accessToken,
                        @Field("search_username") String searchUsername);

    @POST("user/profile/{requestUsername}")
    @FormUrlEncoded
    Call<ProfileResponse> profile(@Field("username") String username,
                                  @Field("access_token") String accessToken,
                                  @Path("requestUsername") String requestUsername);

    @POST("user/feed")
    @FormUrlEncoded
    Call<FeedResponse> feed(@Field("username") String username,
                            @Field("access_token") String accessToken);

    @POST("user/checkin")
    @FormUrlEncoded
    Call<CheckinResponse> checkin(@Field("username") String username,
                                  @Field("access_token") String accessToken,
                                  @Field("latitude") double latitude,
                                  @Field("longitude") double longitude);

    @POST("user/changePassword")
    @FormUrlEncoded
    Call<GenericResponse> changePassword(@Field("username") String username,
                                         @Field("access_token") String accessToken,
                                         @Field("old_password") String oldPassword,
                                         @Field("new_password") String newPassword);

    @POST("user/changeDisplayName")
    @FormUrlEncoded
    Call<GenericResponse> changeDisplayName(@Field("username") String username,
                                            @Field("access_token") String accessToken,
                                            @Field("display_name") String displayName);

    @POST("user/changeEmailAddress")
    @FormUrlEncoded
    Call<GenericResponse> changeEmailAddress(@Field("username") String username,
                                             @Field("access_token") String accessToken,
                                             @Field("email_address") String emailAddress);

    @POST("user/follow")
    @FormUrlEncoded
    Call<GenericResponse> follow(@Field("follower_username") String followerUsername,
                        @Field("access_token") String accessToken,
                        @Field("followee_username") String followeeUsername);

    @POST("user/unfollow")
    @FormUrlEncoded
    Call<GenericResponse> unfollow(@Field("follower_username") String followerUsername,
                          @Field("access_token") String accessToken,
                          @Field("followee_username") String followeeUsername);

    @POST("user/remove")
    @FormUrlEncoded
    Call<Object> remove(@Field("username") String username,
                        @Field("access_token") String accessToken);

}
