package com.hackathon.metropolisshuttle.network;

/**
 * Created by Kavya Shravan on 03/04/25.
 */

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.hackathon.metropolisshuttle.model.LoginRequest;
import com.hackathon.metropolisshuttle.model.LoginResponse;
import com.hackathon.metropolisshuttle.model.RouteData;

public interface APIService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("shuttle-stops")
    Call<RouteData> getRoutes();

}
