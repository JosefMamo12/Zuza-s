package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServerAPI {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<Void>  register (@Body  MyRegisterRequest registerRequest);

    @GET("protected")
    Call<ProtectedResponse> getProtectedResource(@Header("Authorization") String token);
}
