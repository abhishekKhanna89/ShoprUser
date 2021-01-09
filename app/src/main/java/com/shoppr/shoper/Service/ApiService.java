package com.shoppr.shoper.Service;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.JsonObject;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.requestdata.LoginRequest;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import okhttp3.RequestBody;

public interface ApiService {
    @POST("login-with-otp")
    Call<LoginModel> loginUser(@Body LoginRequest requestBody);
    @POST("verify-otp")
    Call<OtpVerifyModel>otpService(@Body OtpVerifyRequest verifyRequest);
    @NonNull
    @GET("get-profile")
    Call<MyProfileModel> apiMyProfile(@Header("Authorization") String token);
    @NonNull
    @GET("stores-list")
    Call<StoreListModel>apiStoreList(@Query("lat")String lat,
                                     @Query("lang")String lang);
    
}
