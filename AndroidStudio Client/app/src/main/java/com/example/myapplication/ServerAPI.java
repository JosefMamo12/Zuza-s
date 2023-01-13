package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServerAPI {
    static String baseUrl = "http://10.0.2.2:8080";

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<Void>  register (@Body  MyRegisterRequest registerRequest);

    @POST("register")
    Call<Void> forgotPassword(@Body String email);

    @GET("protected")
    Call<ProtectedResponse> getProtectedResource(@Header("Authorization") String token);
}
