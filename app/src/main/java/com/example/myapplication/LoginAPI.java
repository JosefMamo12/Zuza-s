package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginAPI {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("protected")
    Call<ProtectedResponse> getProtectedResource(@Header("Authorization") String token);
}
