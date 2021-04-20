package com.shoppr.shoper.Service;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiExecutor {

   // public static String baseUrl="http://shoppr.avaskmcompany.xyz/api/";
    public static String baseUrl="http://shoprs.co.in/api/";
    private static Retrofit retrofit;

    public static ApiService getApiService(Context mContext) {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        return retrofit.create(ApiService.class);
    }



}
