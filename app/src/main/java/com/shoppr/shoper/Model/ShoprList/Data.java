package com.shoppr.shoper.Model.ShoprList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("shopper")
    @Expose
    private List<Shopper> shopper = null;
    @SerializedName("notifications")
    @Expose
    private String notifications;

    public List<Shopper> getShopper() {
        return shopper;
    }

    public void setShopper(List<Shopper> shopper) {
        this.shopper = shopper;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }
}
