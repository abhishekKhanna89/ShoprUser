package com.shoppr.shoper.Service;

import androidx.annotation.NonNull;


import com.google.gson.JsonObject;
import com.shoppr.shoper.Model.AcceptModel;
import com.shoppr.shoper.Model.AutoAssign.AutoAssignModel;
import com.shoppr.shoper.Model.CancelModel;
import com.shoppr.shoper.Model.CartCancel.CartCancelModel;
import com.shoppr.shoper.Model.CartView.CartViewModel;
import com.shoppr.shoper.Model.ChatList.ChatListModel;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.CheckLocation.CheckLocationModel;
import com.shoppr.shoper.Model.CustomerBalancceModel;
import com.shoppr.shoper.Model.GetRegisterMerchant.GetRegisterMerchantModel;
import com.shoppr.shoper.Model.Initiat.InitiatOrderModel;
import com.shoppr.shoper.Model.InitiatPayment.InitiatPaymentModel;
import com.shoppr.shoper.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.NotificationList.NotificationListModel;
import com.shoppr.shoper.Model.OrderDetails.OrderDetailsModel;
import com.shoppr.shoper.Model.OrderDetails.OrderHistory.OrderHistoryModel;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.Model.PaymentSuccess.PaymentSuccessModel;
import com.shoppr.shoper.Model.RatingsModel;
import com.shoppr.shoper.Model.Recharge.RechargeModel;
import com.shoppr.shoper.Model.RejectedModel;
import com.shoppr.shoper.Model.ResendOtp.ResendOtpModel;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Model.StoreListDetails.StoreListDetailsModel;
import com.shoppr.shoper.Model.TerminateChat.TerminateChatModel;
import com.shoppr.shoper.Model.TrackLoaction.TrackLoactionModel;
import com.shoppr.shoper.Model.VerifyRechargeModel;
import com.shoppr.shoper.Model.WalletHistory.WalletHistoryModel;
import com.shoppr.shoper.requestdata.InitiatePaymentRequest;
import com.shoppr.shoper.requestdata.LoginRequest;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.requestdata.PaymentSuccessRequest;
import com.shoppr.shoper.requestdata.RatingsRequest;
import com.shoppr.shoper.requestdata.RechargeRequest;
import com.shoppr.shoper.requestdata.RegisterRequest;
import com.shoppr.shoper.requestdata.ShareLocationRequest;
import com.shoppr.shoper.requestdata.TextTypeRequest;
import com.shoppr.shoper.requestdata.VerifyRechargeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("register")
    Call<LoginModel>registerUser(@Body RegisterRequest registerRequest);

    @POST("verify-otp")
    Call<OtpVerifyModel>otpService(@Body OtpVerifyRequest verifyRequest);
    @NonNull
    @GET("get-profile")
    Call<MyProfileModel> apiMyProfile(@Header("Authorization") String token);
    @NonNull
    @POST("stores-list")
    Call<StoreListModel>apiStoreList(@Header("Authorization") String token,@Query("lat")String lat,
                                     @Query("lang")String lang,
                                     @Query("category_id[]") List<String> category_id,
                                     @Query("search")String search,
                                     @Query("sortby")String sortby,
                                     @Query("location")String location,
                                     @Query("city")String city);

    @NonNull
    @POST("stores-list")
    Call<StoreListModel>apiStoreCategoryList(@Header("Authorization") String token,@Query("lat")String lat,
                                             @Query("lang")String lang,
                                             @Query("location")String location,
                                             @Query("city")String city);

    @NonNull
    @GET("store-details/{id}")
    Call<StoreListDetailsModel>apiStoreListDetails(@Header("Authorization") String token,
                                                   @Path("id")int id);

    @NonNull
    @GET("wallet-history")
    Call<WalletHistoryModel>apiWalletHistory(@Header("Authorization") String token);

    @NonNull
    @POST("shoppr-list")
    Call<ShoprListModel> apiShoprList(@Header("Authorization") String token,
                                      @Query("location")String location,
                                      @Query("city")String city);

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
    @GET("chats")
    Call<ChatListModel>apiUserChatList(@Header("Authorization")String token);


    @NonNull
    @POST("start-chat/{store_id}")
    Call<StartChatModel>apiChatStart(@Header("Authorization") String token,
                                     @Path("store_id")int store_id,
                                     @Query("lat")String lat,
                                     @Query("lang")String lang,
                                     @Query("location")String location,
                                     @Query("city")String city);
   /* @NonNull
    @GET("start-chat/{store_id}")
    Call<StartChatModel>apiChatStart(@Header("Authorization") String token);*/
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

    @POST("send-message/{chat_id}")
    Call<SendModel>apiSend(@Header("Authorization") String token,
                           @Path("chat_id")int chat_id, @Body TextTypeRequest textTypeRequest);

    @Multipart
    @POST("send-message/{chat_id}")
    Call<SendModel>apiImageSend(@HeaderMap Map<String, String> token, @Path("chat_id")int chat_id,@Part MultipartBody.Part image, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("send-message/{chat_id}")
    Call<SendModel>apiAudioSend(@HeaderMap Map<String, String> token, @Path("chat_id")int chat_id,@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);

    @NonNull
    @GET("cart/{chat_id}")
    Call<CartViewModel>apiCartView(@Header("Authorization") String token,
                                   @Path("chat_id")int chat_id);

    @NonNull
    @GET("cart-cancel/{message_id}")
    Call<CartCancelModel>apiCartCancel(@Header("Authorization") String token,
                                       @Path("message_id")int chat_id);

    @POST("send-message/{chat_id}")
    Call<SendModel>apiShareLocation(@Header("Authorization") String token,
                           @Path("chat_id")int chat_id, @Body ShareLocationRequest shareLocationRequest);

    @NonNull
    @GET("initiate-order/{chat_id}")
    Call<InitiatOrderModel>apiInitiateOrder(@Header("Authorization") String token,
                                            @Path("chat_id")int chat_id);

    @POST("initiate-payment/{order_id}")
    Call<InitiatPaymentModel>apiInitiatePayment(@Header("Authorization") String token,
                                                @Path("order_id")int chat_id,
                                                @Body InitiatePaymentRequest initiatePaymentRequest);

    @POST("verify-payment")
    Call<PaymentSuccessModel>apiPaymentSuccess(@Header("Authorization") String token,
                                               @Body PaymentSuccessRequest paymentSuccessRequest);

    @NonNull
    @GET("orders")
    Call<OrderDetailsModel>apiMyOrder(@Header("Authorization") String token);

    @NonNull
    @GET("order-details/{order_id}")
    Call<OrderHistoryModel>apiOrderDetails(@Header("Authorization") String token,
                                           @Path("order_id")int order_id);

    @NonNull
    @GET("track-location/{message_id}")
    Call<TrackLoactionModel>apiTrackLocation(@Header("Authorization") String token,
                                             @Path("message_id")int message_id);

    @NonNull
    @GET("initiate-video-call/{chat_id}")
    Call<InitiateVideoCallModel>apiInitiateVideoCall(@Header("Authorization") String token,
                                                     @Path("chat_id")int chat_id);

    @NonNull
    @GET("notifications")
    Call<NotificationListModel>apiNotificationList(@Header("Authorization") String token,
                                                   @Query("page")int page);

    @Multipart
    @POST("register-as-merchant")
    Call<JsonObject> apiRegisterMerchant(@HeaderMap Map<String, String> token, @Part MultipartBody.Part[] images, @PartMap() Map<String, RequestBody> partMap);

    @NonNull
    @GET("view-application")
    Call<GetRegisterMerchantModel>apiGetMerchantRegister(@Header("Authorization") String token);

    @NonNull
    @POST("auto-assign/{chat_id}")
    Call<AutoAssignModel>apiAutoAssign(@Header("Authorization") String token,
                                       @Path("chat_id")int chat_id,
                                       @Query("location")String location,
                                       @Query("city")String city);

    @POST("check-availability")
    Call<CheckLocationModel>apiCheckLocation(@Header("Authorization") String token, @Query("location")String location,
                                             @Query("city")String city);
    @NonNull
    @GET("logout")
    Call<LogoutModel>apiLogoutStatus(@Header("Authorization") String token);

    @POST("resend-otp")
    Call<ResendOtpModel>apiResendOtp(@Query("type")String type,
                                     @Query("mobile")String mobile);
    @NonNull
    @GET("terminate-chat/{id}")
    Call<TerminateChatModel> apiChatTerminate(@Header("Authorization") String token,
                                              @Path("id") int id);
}
