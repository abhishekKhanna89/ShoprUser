package com.shoppr.shoper.Model.InitiateVideoCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("rtm_token")
    @Expose
    private String rtm_token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setRtm_token(String rtm_token) {
        this.rtm_token = rtm_token;
    }

    public String getRtm_token() {
        return rtm_token;
    }
}