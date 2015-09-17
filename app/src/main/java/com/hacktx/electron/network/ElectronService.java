package com.hacktx.electron.network;

import com.hacktx.electron.model.Attendee;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ElectronService {

    @GET("/user")
    void getRegistrationData(@Query("volunteer_id") String volunteerId, @Query("email") String email, Callback<Attendee> cb);

    @FormUrlEncoded
    @POST("/checkin")
    void checkIn(@Field("email") String email, Callback<Attendee> cb);

}
