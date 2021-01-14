package com.shoppr.shoper.Service;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.JsonObject;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.AcceptModel;
import com.shoppr.shoper.Model.CancelModel;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.CustomerBalancceModel;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.Model.RatingsModel;
import com.shoppr.shoper.Model.Recharge.RechargeModel;
import com.shoppr.shoper.Model.RejectedModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Model.StoreListDetails.StoreListDetailsModel;
import com.shoppr.shoper.Model.UpdateProfileModel;
import com.shoppr.shoper.Model.VerifyRechargeModel;
import com.shoppr.shoper.Model.WalletHistory.WalletHistoryModel;
import com.shoppr.shoper.requestdata.LoginRequest;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.requestdata.RatingsRequest;
import com.shoppr.shoper.requestdata.RechargeRequest;
import com.shoppr.shoper.requestdata.UpdateProfileRequest;
import com.shoppr.shoper.requestdata.VerifyRechargeRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
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
    @NonNull
    @GET("store-details/{id}")
    Call<StoreListDetailsModel>apiStoreListDetails(@Header("Authorization") String token,
                                                   @Path("id")int id);

    @NonNull
    @GET("wallet-history")
    Call<WalletHistoryModel>apiWalletHistory(@Header("Authorization") String token);

    @NonNull
    @GET("shoppr-list")
    Call<ShoprListModel> apiShoprList();

    @NonNull
    @GET("customer-balance")
    Call<CustomerBalancceModel>apiCustomerbalance(@Header("Authorization") String token);

    @POST("recharge")
    Call<RechargeModel>apiRecharge(@Header("Authorization") String token,
                                   @Body RechargeRequest rechargeRequest);

    @POST("verify-recharge")
    Call<VerifyRechargeModel>apiVerifyRecharge(@Header("Authorization") String token,
                                               @Body VerifyRechargeRequest verifyRechargeRequest);
    @Multipart
    @POST("update-profile")
    Call<JsonObject> apiUpdateProfile(@HeaderMap Map<String, String> token, @Part MultipartBody.Part[] images, @PartMap() Map<String, RequestBody> partMap);

    @NonNull
    @GET("start-chat")
    Call<StartChatModel>apiChatStart(@Header("Authorization") String token);

    @NonNull
    @GET("chat-messages/{chat_id}")
    Call<ChatMessageModel>apiChatMessage(@Header("Authorization") String token,
                                         @Path("chat_id")int chat_id);
    @NonNull
    @GET("accept/{message_id}")
    Call<AcceptModel>apiAccept(@Header("Authorization") String token,
                               @Path("message_id")int message_id);
    @NonNull
    @GET("reject/{message_id}")
    Call<RejectedModel>apiRejected(@Header("Authorization") String token,
                                   @Path("message_id")int message_id);
    @NonNull
    @GET("cancel/{message_id}")
    Call<CancelModel>apiCancel(@Header("Authorization") String token,
                               @Path("message_id")int message_id);
    @POST("rate-service/{message_id}")
    Call<RatingsModel>apiRatings(@Header("Authorization") String token,
                                 @Path("message_id")int message_id,@Body RatingsRequest ratingsRequest);
}
