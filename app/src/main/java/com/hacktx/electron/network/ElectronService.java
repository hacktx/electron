package com.hacktx.electron.network;

import com.hacktx.electron.model.Attendee;
import com.hacktx.electron.model.CheckInPayload;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ElectronService {

    @GET("/check-in")
    void getRegistrationData(@Query("volunteer_email") String volunteerEmail, @Query("email") String email, Callback<Attendee> cb);

    @POST("/check-in")
    void checkIn(@Body CheckInPayload checkinPayload, Callback<Attendee> cb);

}
