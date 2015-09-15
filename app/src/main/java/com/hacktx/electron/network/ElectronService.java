package com.hacktx.electron.network;

import com.hacktx.electron.model.Attendee;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ElectronService {

    @GET("/registrations/{email}")
    void getRegistrationData(@Path("email") String email, Callback<Attendee> cb);

    @FormUrlEncoded
    @POST("/checkin")
    void checkIn(@Field("email") String email, Callback<Attendee> cb);

}
