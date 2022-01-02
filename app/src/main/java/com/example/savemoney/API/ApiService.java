package com.example.savemoney.API;

import com.example.savemoney.API.model.ActivitiesResponse;
import com.example.savemoney.API.model.LoginRequest;
import com.example.savemoney.API.model.LoginResponse;
import com.example.savemoney.API.model.RegisterRequest;
import com.example.savemoney.API.model.RegisterResponse;
import com.example.savemoney.API.model.UpdateRequest;
import com.example.savemoney.API.model.UpdateResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

//    String BASE_URL = "http://192.168.1.15:3000/";
    String BASE_URL = "http://94.237.76.252:3000/";

    Gson gson = new GsonBuilder()
            .setDateFormat("yyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("/activity")
    Call<ActivitiesResponse> get(@Header("Authorization") String authToken);

    @PUT("/activity")
    Call<UpdateResponse> update(@Header("Authorization") String authToken, @Body UpdateRequest updateRequest);
}
