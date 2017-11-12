package com.shreyas208.databass.api.service;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TravelationsAPI {

    // TODO
    // Implement API functions
    // I recommend defining return types as Call<Object> and then using instanceof
    // in order to see which kind of object is returned, either an error or success

    @POST("/api/user/register?username={user}&password={pass}&email_address={email}&display_name={disp}")
    Call<Object> register(@Path("user") String user,
                          @Path("pass") String password,
                          @Path("email") String email,
                          @Path("disp") String displayName);

    @POST("/api/user/login?username={user}&password={pass}")
    Call<Object> login(@Path("user") String user,
                       @Path("pass") String password);

    @POST("/api/user/logout?username={user}&access_token={token}")
    Call<Object> logout(@Path("user") String user,
                        @Path("token") String token);

    @POST("/api/user/profile?username={user}&access_token={token}")
    Call<Object> profile(@Path("user") String user,
                        @Path("token") String token);

    @POST("/api/user/changePassword?username={user}&old_password={old}&new_password={new}&access_token={token}")
    Call<Object> changePassword(@Path("user") String user,
                                @Path("old") String oldPassword,
                                @Path("new") String newPassword,
                                @Path("token") String token);

    @POST("/api/user/changeDisplayName?username={user}&display_name={display}&access_token={token}")
    Call<Object> changeDisplayName(@Path("user") String user,
                                   @Path("display") String displayName,
                                   @Path("token") String token);

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
