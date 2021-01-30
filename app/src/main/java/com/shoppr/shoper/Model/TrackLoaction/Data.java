package com.shoppr.shoper.Model.TrackLoaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("shoppr")
    @Expose
    private Shoppr shoppr;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Shoppr getShoppr() {
        return shoppr;
    }

    public void setShoppr(Shoppr shoppr) {
        this.shoppr = shoppr;
    }

}